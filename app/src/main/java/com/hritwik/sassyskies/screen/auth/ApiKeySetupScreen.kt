package com.hritwik.sassyskies.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hritwik.sassyskies.ui.theme.JosefinSans
import com.hritwik.sassyskies.viewmodel.ApiKeyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeySetupScreen(
    onSetupComplete: () -> Unit,
    apiKeyViewModel: ApiKeyViewModel = hiltViewModel()
) {
    val uiState by apiKeyViewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current
    var weatherApiKey by remember { mutableStateOf(uiState.weatherApiKey) }
    var geminiApiKey by remember { mutableStateOf(uiState.geminiApiKey) }
    var weatherKeyVisible by remember { mutableStateOf(false) }
    var geminiKeyVisible by remember { mutableStateOf(false) }

    // Update fields when uiState changes
    LaunchedEffect(uiState.weatherApiKey, uiState.geminiApiKey) {
        weatherApiKey = uiState.weatherApiKey
        geminiApiKey = uiState.geminiApiKey
    }

    // Navigate on success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSetupComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Setup Your\nAPI Keys",
                fontFamily = JosefinSans,
                fontSize = 48.sp,
                lineHeight = 45.sp,
                letterSpacing = (-2).sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Personal API keys for unlimited weather sarcasm!",
                fontFamily = JosefinSans,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Instructions Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Why API Keys?",
                    fontFamily = JosefinSans,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "• Personal rate limits (1000+ requests/day)\n• No sharing with other users\n• Better performance and reliability\n• Free to get and use!",
                    fontFamily = JosefinSans,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // API Keys Form
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Weather API Section
            ApiKeySection(
                title = "OpenWeatherMap API Key",
                description = "For weather data and forecasts",
                value = weatherApiKey,
                onValueChange = { weatherApiKey = it },
                isVisible = weatherKeyVisible,
                onVisibilityToggle = { weatherKeyVisible = !weatherKeyVisible },
                getApiUrl = "https://home.openweathermap.org/users/sign_in",
                placeholder = "OpenWeatherMap API key",
                onGetApiClick = {
                    uriHandler.openUri("https://home.openweathermap.org/users/sign_in")
                },
                focusManager = focusManager
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            // Gemini API Section
            ApiKeySection(
                title = "Google AI (Gemini) API Key",
                description = "For AI-generated sarcastic messages",
                value = geminiApiKey,
                onValueChange = { geminiApiKey = it },
                isVisible = geminiKeyVisible,
                onVisibilityToggle = { geminiKeyVisible = !geminiKeyVisible },
                getApiUrl = "https://aistudio.google.com/app/apikey",
                placeholder = "Gemini API key",
                onGetApiClick = {
                    uriHandler.openUri("https://aistudio.google.com/app/apikey")
                },
                focusManager = focusManager,
                isLast = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    apiKeyViewModel.updateApiKeys(
                        weatherApiKey.trim(),
                        geminiApiKey.trim()
                    )
                },
                enabled = weatherApiKey.isNotBlank() &&
                        geminiApiKey.isNotBlank() &&
                        !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save API Keys",
                        fontFamily = JosefinSans,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Error Message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error,
                        fontFamily = JosefinSans,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ApiKeySection(
    title: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    getApiUrl: String,
    placeholder: String,
    onGetApiClick: () -> Unit,
    focusManager: FocusManager,
    isLast: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = JosefinSans,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontFamily = JosefinSans,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onGetApiClick,
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Get API Key",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Get Key",
                    fontSize = 12.sp,
                    fontFamily = JosefinSans
                )
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "API Key"
                )
            },
            trailingIcon = {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (isVisible) "Hide key" else "Show key"
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = if (isLast) ImeAction.Done else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}