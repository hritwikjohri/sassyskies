package com.hritwik.sassyskies.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hritwik.sassyskies.ui.theme.JosefinSans
import com.hritwik.sassyskies.ui.theme.onWarningContainer
import com.hritwik.sassyskies.ui.theme.warningContainer

@Composable
fun LocationErrorContent(
    errorMessage: String,
    onRetryLocation: () -> Unit,
    onUseDefaultLocation: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.warningContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Location Error",
                tint = MaterialTheme.colorScheme.onWarningContainer,
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = "Location Issue",
                fontFamily = JosefinSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onWarningContainer,
                textAlign = TextAlign.Center
            )

            Text(
                text = errorMessage,
                fontFamily = JosefinSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onWarningContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onRetryLocation,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onWarningContainer
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            MaterialTheme.colorScheme.onWarningContainer
                        )
                    )
                ) {
                    Text(
                        "Retry",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onUseDefaultLocation,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onWarningContainer,
                        contentColor = MaterialTheme.colorScheme.warningContainer
                    )
                ) {
                    Text(
                        "Use Default",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
