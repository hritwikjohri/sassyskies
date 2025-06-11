package com.hritwik.sassyskies.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ServerValue
import com.hritwik.sassyskies.model.auth.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) {

    companion object {
        private const val USERS_PATH = "users"
    }

    private val usersRef: DatabaseReference = database.getReference(USERS_PATH)

    init {
        // Enable offline persistence for Realtime Database
        try {
            database.setPersistenceEnabled(true)
        } catch (e: Exception) {
            // Persistence can only be enabled once, ignore if already set
        }
    }

    /**
     * Observes Firebase authentication state and returns user data from Realtime Database
     */
    fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser

            if (firebaseUser != null) {
                // User is authenticated, listen to their data in Realtime Database
                val userRef = usersRef.child(firebaseUser.uid)

                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Convert database snapshot to User object
                            val user = snapshot.getValue(User::class.java)?.copy(
                                // Ensure we have the latest auth data
                                isVerified = firebaseUser.isEmailVerified,
                                email = firebaseUser.email ?: "",
                                lastLogin = System.currentTimeMillis()
                            )
                            trySend(user)
                        } else {
                            // User data doesn't exist, create it (non-suspend version)
                            createUserDataAsync(firebaseUser.uid, firebaseUser.email ?: "", firebaseUser.displayName ?: "")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Database read failed, create minimal user from auth data
                        val fallbackUser = User(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            displayName = firebaseUser.displayName ?: "",
                            isVerified = firebaseUser.isEmailVerified,
                            lastLogin = System.currentTimeMillis()
                        )
                        trySend(fallbackUser)
                    }
                }

                userRef.addValueEventListener(valueEventListener)
            } else {
                // No authenticated user
                trySend(null)
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * Creates a new user account and saves profile to Realtime Database
     */
    suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            // Create Firebase Auth account
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")

            // Send email verification
            try {
                firebaseUser.sendEmailVerification().await()
            } catch (e: Exception) {
                // Email verification failed, but continue
            }

            // Update Firebase Auth profile
            try {
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    this.displayName = displayName
                }
                firebaseUser.updateProfile(profileUpdates).await()
            } catch (e: Exception) {
                // Profile update failed, but continue
            }

            // Create user data in Realtime Database
            val currentTime = System.currentTimeMillis()
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                isVerified = false,
                createdAt = currentTime,
                updatedAt = currentTime,
                lastLogin = currentTime
            )

            // Save to Realtime Database
            usersRef.child(firebaseUser.uid).setValue(user).await()

            Result.success(user)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("email-already-in-use") == true -> "An account with this email already exists."
                e.message?.contains("weak-password") == true -> "Password is too weak. Please choose a stronger password."
                e.message?.contains("invalid-email") == true -> "Invalid email address format."
                e.message?.contains("network") == true -> "Network error. Please check your connection."
                else -> "Account creation failed: ${e.localizedMessage ?: e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Signs in user and updates last login time in Realtime Database
     */
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // Authenticate with Firebase Auth
            val authResult = withTimeout(10000) {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
            }
            val firebaseUser = authResult.user ?: throw Exception("Sign in failed")

            try {
                // Fetch user data from Realtime Database
                val snapshot = withTimeout(8000) {
                    usersRef.child(firebaseUser.uid).get().await()
                }

                val user = if (snapshot.exists()) {
                    // Update existing user with latest auth info and last login
                    snapshot.getValue(User::class.java)?.copy(
                        isVerified = firebaseUser.isEmailVerified,
                        email = firebaseUser.email ?: email,
                        lastLogin = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    ) ?: throw Exception("Failed to parse user data")
                } else {
                    // Create user data if it doesn't exist
                    val currentTime = System.currentTimeMillis()
                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: email,
                        displayName = firebaseUser.displayName ?: "",
                        isVerified = firebaseUser.isEmailVerified,
                        createdAt = currentTime,
                        updatedAt = currentTime,
                        lastLogin = currentTime
                    )

                    // Save to Realtime Database
                    withTimeout(8000) {
                        usersRef.child(firebaseUser.uid).setValue(newUser).await()
                    }

                    newUser
                }

                // Update last login time
                updateLastLogin(firebaseUser.uid)

                Result.success(user)

            } catch (databaseError: Exception) {
                // Database failed, create minimal user from auth data
                val currentTime = System.currentTimeMillis()
                val fallbackUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    displayName = firebaseUser.displayName ?: "",
                    isVerified = firebaseUser.isEmailVerified,
                    createdAt = currentTime,
                    updatedAt = currentTime,
                    lastLogin = currentTime
                )

                Result.success(fallbackUser)
            }

        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("network") == true -> "Network error. Please check your connection."
                e.message?.contains("timeout", ignoreCase = true) == true -> "Connection timeout. Please try again."
                e.message?.contains("password") == true -> "Invalid email or password."
                e.message?.contains("email") == true -> "Invalid email format."
                e.message?.contains("user-not-found") == true -> "No account found with this email."
                e.message?.contains("wrong-password") == true -> "Incorrect password."
                e.message?.contains("user-disabled") == true -> "This account has been disabled."
                e.message?.contains("too-many-requests") == true -> "Too many failed attempts. Please try again later."
                e.message?.contains("invalid-credential") == true -> "Invalid email or password."
                else -> "Login failed: ${e.localizedMessage ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Signs out user from Firebase Auth
     */
    suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sends password reset email
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("user-not-found") == true -> "No account found with this email address."
                e.message?.contains("invalid-email") == true -> "Invalid email address format."
                e.message?.contains("network") == true -> "Network error. Please check your connection."
                else -> "Password reset failed: ${e.localizedMessage ?: e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Updates user profile in both Firebase Auth and Realtime Database
     */
    suspend fun updateUserProfile(displayName: String, profileImageUrl: String = ""): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("No authenticated user")

            // Update Firebase Auth profile
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                this.displayName = displayName
                if (profileImageUrl.isNotEmpty()) {
                    this.photoUri = android.net.Uri.parse(profileImageUrl)
                }
            }
            currentUser.updateProfile(profileUpdates).await()

            // Update Realtime Database
            val updates = mutableMapOf<String, Any>(
                "display_name" to displayName,
                "updated_at" to ServerValue.TIMESTAMP
            )

            if (profileImageUrl.isNotEmpty()) {
                updates["profile_image_url"] = profileImageUrl
            }

            usersRef.child(currentUser.uid).updateChildren(updates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update profile: ${e.message}"))
        }
    }

    /**
     * Permanently deletes user account and Realtime Database data
     */
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("No authenticated user")
            val uid = currentUser.uid

            // Delete user data from Realtime Database first
            usersRef.child(uid).removeValue().await()

            // Delete Firebase Auth account
            currentUser.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete account: ${e.message}"))
        }
    }

    /**
     * Gets current user profile from Realtime Database
     */
    suspend fun getUserProfile(): Result<User?> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: return Result.success(null)

            val snapshot = usersRef.child(currentUser.uid).get().await()

            val user = if (snapshot.exists()) {
                snapshot.getValue(User::class.java)?.copy(
                    isVerified = currentUser.isEmailVerified,
                    email = currentUser.email ?: ""
                )
            } else {
                null
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get user profile: ${e.message}"))
        }
    }

    /**
     * Helper function to create user data in Realtime Database (async version)
     */
    private fun createUserDataAsync(uid: String, email: String, displayName: String) {
        val currentTime = System.currentTimeMillis()
        val user = User(
            uid = uid,
            email = email,
            displayName = displayName,
            isVerified = firebaseAuth.currentUser?.isEmailVerified ?: false,
            createdAt = currentTime,
            updatedAt = currentTime,
            lastLogin = currentTime
        )

        usersRef.child(uid).setValue(user)
            .addOnSuccessListener {
                // User data created successfully
            }
            .addOnFailureListener {
                // Failed to create user data
            }
    }

    /**
     * Helper function to update last login time
     */
    private fun updateLastLogin(uid: String) {
        usersRef.child(uid).child("last_login").setValue(ServerValue.TIMESTAMP)
            .addOnFailureListener {
                // Failed to update last login, but not critical
            }
    }

    suspend fun updateApiKeys(weatherApiKey: String, geminiApiKey: String): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("No authenticated user")

            // Basic validation
            if (weatherApiKey.isBlank()) {
                throw Exception("Weather API key cannot be empty")
            }
            if (geminiApiKey.isBlank()) {
                throw Exception("Gemini API key cannot be empty")
            }
            if (weatherApiKey.length < 16) {
                throw Exception("Weather API key appears to be too short")
            }
            if (geminiApiKey.length < 16) {
                throw Exception("Gemini API key appears to be too short")
            }

            // Update Realtime Database with API keys
            val updates = mapOf(
                "weather_api_key" to weatherApiKey,
                "gemini_api_key" to geminiApiKey,
                "updated_at" to System.currentTimeMillis()
            )

            usersRef.child(currentUser.uid).updateChildren(updates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to save API keys: ${e.message}"))
        }
    }

    /**
     * Gets the user's API keys
     */
    suspend fun getApiKeys(): Result<Pair<String, String>> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("No authenticated user")

            val snapshot = usersRef.child(currentUser.uid).get().await()
            val user = snapshot.getValue(User::class.java)

            if (user != null) {
                Result.success(Pair(user.weatherApiKey, user.geminiApiKey))
            } else {
                Result.success(Pair("", ""))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get API keys: ${e.message}"))
        }
    }

    /**
     * Checks if user has valid API keys
     */
    suspend fun hasValidApiKeys(): Result<Boolean> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: return Result.success(false)

            val snapshot = usersRef.child(currentUser.uid).get().await()
            val user = snapshot.getValue(User::class.java)

            Result.success(user?.hasAllApiKeys() == true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to check API keys: ${e.message}"))
        }
    }

    /**
     * Get all users (admin function - be careful with security rules)
     */
    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val snapshot = usersRef.get().await()
            val users = mutableListOf<User>()

            for (userSnapshot in snapshot.children) {
                userSnapshot.getValue(User::class.java)?.let { user ->
                    users.add(user)
                }
            }

            Result.success(users)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get users: ${e.message}"))
        }
    }

    /**
     * Listen to a specific user's data changes in real-time
     */
    fun listenToUser(uid: String): Flow<User?> = callbackFlow {
        val userRef = usersRef.child(uid)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = if (snapshot.exists()) {
                    snapshot.getValue(User::class.java)
                } else {
                    null
                }
                trySend(user)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }

        userRef.addValueEventListener(valueEventListener)

        awaitClose {
            userRef.removeEventListener(valueEventListener)
        }
    }
}