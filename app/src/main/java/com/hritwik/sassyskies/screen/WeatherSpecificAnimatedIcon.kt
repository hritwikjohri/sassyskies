package com.hritwik.sassyskies.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun WeatherSpecificAnimatedIcon(
    iconUrl: String,
    weatherCondition: String
) {
    when {
        // Rain - Vertical shake
        weatherCondition.contains("rain", ignoreCase = true) ||
                weatherCondition.contains("drizzle", ignoreCase = true) -> {
            RainyAnimation(iconUrl)
        }
        // Snow - Floating/swaying
        weatherCondition.contains("snow", ignoreCase = true) -> {
            SnowAnimation(iconUrl)
        }
        // Storm - Intense shake
        weatherCondition.contains("storm", ignoreCase = true) ||
                weatherCondition.contains("thunder", ignoreCase = true) -> {
            StormAnimation(iconUrl)
        }
        // Sun - Gentle rotation
        weatherCondition.contains("clear", ignoreCase = true) ||
                weatherCondition.contains("sun", ignoreCase = true) -> {
            SunnyAnimation(iconUrl)
        }
        // Clouds - Gentle float
        weatherCondition.contains("cloud", ignoreCase = true) -> {
            CloudyAnimation(iconUrl)
        }
        // Fog/Mist - Fade in/out
        weatherCondition.contains("fog", ignoreCase = true) ||
                weatherCondition.contains("mist", ignoreCase = true) ||
                weatherCondition.contains("haze", ignoreCase = true) -> {
            FogAnimation(iconUrl)
        }
        // Wind - Side to side movement
        weatherCondition.contains("wind", ignoreCase = true) -> {
            WindyAnimation(iconUrl)
        }
        // Tornado - Fast rotation
        weatherCondition.contains("tornado", ignoreCase = true) ||
                weatherCondition.contains("squall", ignoreCase = true) -> {
            TornadoAnimation(iconUrl)
        }
        // Dust/Sand - Vibration
        weatherCondition.contains("dust", ignoreCase = true) ||
                weatherCondition.contains("sand", ignoreCase = true) ||
                weatherCondition.contains("ash", ignoreCase = true) -> {
            DustAnimation(iconUrl)
        }
        // Extreme weather - Combination shake
        weatherCondition.contains("extreme", ignoreCase = true) ||
                weatherCondition.contains("hurricane", ignoreCase = true) ||
                weatherCondition.contains("cyclone", ignoreCase = true) -> {
            ExtremeWeatherAnimation(iconUrl)
        }
        // Default - Subtle pulse
        else -> {
            PulsingWeatherIcon(iconUrl)
        }
    }
}

@Composable
private fun RainyAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rain_shake"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                translationY = offsetY
            }
    )
}

@Composable
private fun SnowAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "snow_sway"
    )
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "snow_float"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                translationX = offsetX
                translationY = offsetY
            }
    )
}

@Composable
private fun StormAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "storm")
    val shake by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "storm_shake"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                translationX = shake
                translationY = shake * 0.5f
            }
    )
}

@Composable
private fun SunnyAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunny")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_rotation"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .rotate(rotation)
    )
}

@Composable
private fun CloudyAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "cloudy")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_drift"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                translationX = offsetX
            }
    )
}

@Composable
private fun FogAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "fog")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fog_fade"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                this.alpha = alpha
            }
    )
}

@Composable
private fun WindyAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "windy")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wind_sway"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wind_tilt"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .rotate(rotation)
            .graphicsLayer {
                translationX = offsetX
            }
    )
}

@Composable
private fun TornadoAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "tornado")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tornado_spin"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tornado_pulse"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .rotate(rotation)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    )
}

@Composable
private fun DustAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "dust")
    val shakeX by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dust_shake_x"
    )
    val shakeY by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dust_shake_y"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                translationX = shakeX
                translationY = shakeY
            }
    )
}

@Composable
private fun ExtremeWeatherAnimation(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "extreme")
    val shake by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "extreme_shake"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 120, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "extreme_rotation"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "extreme_scale"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .rotate(rotation)
            .graphicsLayer {
                translationX = shake
                translationY = shake * 0.7f
                scaleX = scale
                scaleY = scale
            }
    )
}

@Composable
private fun PulsingWeatherIcon(iconUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "default_pulse"
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    )
}