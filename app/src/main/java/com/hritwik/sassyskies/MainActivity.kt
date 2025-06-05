package com.hritwik.sassyskies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hritwik.sassyskies.core.AppRoute
import com.hritwik.sassyskies.ui.theme.SassySkiesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SassySkiesTheme {
                AppRoute()
            }
        }
    }
}