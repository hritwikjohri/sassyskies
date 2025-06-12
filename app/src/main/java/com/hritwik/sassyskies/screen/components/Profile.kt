package com.hritwik.sassyskies.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hritwik.sassyskies.ui.theme.JosefinSans
import com.hritwik.sassyskies.viewmodel.ApiKeyViewModel
import com.hritwik.sassyskies.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    onNavigateBack: () -> Unit,
    onNavigateToApiKeys: () -> Unit,
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    apiKeyViewModel: ApiKeyViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val apiKeyUiState by apiKeyViewModel.uiState.collectAsStateWithLifecycle()
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Sign out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = {
                Text(
                    text = "Sign Out",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out? You'll need to enter your API keys again when you sign back in.",
                    fontFamily = JosefinSans
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        authViewModel.signOut()
                        onSignOut()
                    }
                ) {
                    Text(
                        text = "Sign Out",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(
                        text = "Cancel",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            authUiState.currentUser?.let { user ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = user.profileImageUrl.ifEmpty {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            contentDescription = "User Image"
                        )
                    }

                    // User Info
                    Text(
                        text = user.displayName.ifEmpty { "Sassy User" },
                        fontFamily = JosefinSans,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user.email,
                            fontFamily = JosefinSans,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        if (user.isVerified) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Verified",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Member since
                    Text(
                        text = "Member since ${SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(user.createdAt))}",
                        fontFamily = JosefinSans,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "API Keys Status",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = JosefinSans,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Weather API Status
                    ApiKeyStatusRow(
                        label = "Weather API",
                        isConfigured = apiKeyUiState.weatherApiKey.isNotEmpty(),
                        status = if (apiKeyUiState.weatherApiKey.isNotEmpty()) "Configured" else "Not set"
                    )

                    // Gemini API Status
                    ApiKeyStatusRow(
                        label = "Gemini AI API",
                        isConfigured = apiKeyUiState.geminiApiKey.isNotEmpty(),
                        status = if (apiKeyUiState.geminiApiKey.isNotEmpty()) "Configured" else "Not set"
                    )

                    // Manage API Keys Button
                    OutlinedButton(
                        onClick = onNavigateToApiKeys,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Manage API Keys",
                            fontFamily = JosefinSans,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSignOutDialog = true }
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign out",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Sign Out",
                        fontFamily = JosefinSans,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ApiKeyStatusRow(
    label: String,
    isConfigured: Boolean,
    status: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = JosefinSans,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = status,
            fontFamily = JosefinSans,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isConfigured) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
    }
}