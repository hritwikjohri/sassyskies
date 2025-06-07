package com.hritwik.sassyskies.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hritwik.sassyskies.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // Get user data from Firestore
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val user = if (document.exists()) {
                            document.toObject(User::class.java)
                        } else {
                            // Create new user document
                            val newUser = User(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                displayName = firebaseUser.displayName ?: "",
                                isVerified = firebaseUser.isEmailVerified
                            )
                            firestore.collection("users")
                                .document(firebaseUser.uid)
                                .set(newUser)
                            newUser
                        }
                        trySend(user)
                    }
                    .addOnFailureListener {
                        trySend(null)
                    }
            } else {
                trySend(null)
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")

            // Send email verification
            firebaseUser.sendEmailVerification().await()

            // Create user document in Firestore
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                isVerified = false
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Sign in failed")

            val document = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = document.toObject(User::class.java)
                ?: throw Exception("User data not found")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateApiKeys(weatherApiKey: String, geminiApiKey: String): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("No authenticated user")

            firestore.collection("users")
                .document(currentUser.uid)
                .update(
                    mapOf(
                        "weatherApiKey" to weatherApiKey,
                        "geminiApiKey" to geminiApiKey
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUsageCount(): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("No authenticated user")

            firestore.collection("users")
                .document(currentUser.uid)
                .update(
                    mapOf(
                        "usageCount" to com.google.firebase.firestore.FieldValue.increment(1),
                        "lastUsage" to System.currentTimeMillis()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}