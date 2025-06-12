package com.hritwik.sassyskies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hritwik.sassyskies.core.AppRoute
import com.hritwik.sassyskies.screen.Splash
import com.hritwik.sassyskies.ui.theme.SassySkiesTheme
import com.hritwik.sassyskies.viewmodel.ApiKeyViewModel
import com.hritwik.sassyskies.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SassySkiesTheme {
                val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
                var showMainApp by remember { mutableStateOf(false) }
                var initialRoute by remember { mutableStateOf<String?>(null) }

                // Determine the initial route based on auth state
                LaunchedEffect(authUiState.isAuthenticated, authUiState.currentUser, authUiState.isLoading) {
                    // Only proceed when we have a definitive auth state (not loading)
                    if (!authUiState.isLoading) {
                        initialRoute = when {
                            !authUiState.isAuthenticated -> "Login"
                            authUiState.isAuthenticated && authUiState.currentUser?.hasAllApiKeys() != true -> "ApiKeySetup"
                            authUiState.isAuthenticated && authUiState.currentUser?.hasAllApiKeys() == true -> "Home"
                            else -> "Login" // Fallback
                        }
                        showMainApp = true
                    }
                }

                // Show splash until we determine the route
                if (!showMainApp || initialRoute == null) {
                    Splash()
                } else {
                    AppRoute(
                        startDestination = initialRoute!!,
                        authViewModel = authViewModel,
                        apiKeyViewModel = apiKeyViewModel
                    )
                }
            }
        }
    }
}