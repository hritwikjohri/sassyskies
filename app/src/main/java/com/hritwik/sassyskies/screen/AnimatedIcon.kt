package com.hritwik.sassyskies.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hritwik.sassyskies.model.weather.core.WeatherResponse

@Composable
fun AnimatedIcon(
    weather: WeatherResponse,
    modifier: Modifier = Modifier
) {
    val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
    val weatherMain = weather.weather.firstOrNull()?.main?.lowercase() ?: ""
    val isDay = iconCode.endsWith("d")

    val iconFileName = when {
        weatherMain.contains("clear") -> if (isDay) "clear-day.svg" else "clear-night.svg"
        weatherMain.contains("clouds") -> when {
            iconCode.startsWith("02") -> if (isDay) "partly-cloudy-day.svg" else "partly-cloudy-night.svg"
            iconCode.startsWith("03") -> "cloudy.svg"
            iconCode.startsWith("04") -> "overcast.svg"
            else -> "cloudy.svg"
        }
        weatherMain.contains("rain") -> when {
            iconCode.startsWith("09") -> "rain.svg"
            iconCode.startsWith("10") -> if (isDay) "partly-cloudy-day-rain.svg" else "partly-cloudy-night-rain.svg"
            else -> "rain.svg"
        }
        weatherMain.contains("drizzle") -> "drizzle.svg"
        weatherMain.contains("thunderstorm") -> "thunderstorms.svg"
        weatherMain.contains("snow") -> "snow.svg"
        weatherMain.contains("mist") || weatherMain.contains("fog") -> "fog.svg"
        weatherMain.contains("smoke") -> "smoke.svg"
        weatherMain.contains("haze") -> "haze.svg"
        weatherMain.contains("dust") || weatherMain.contains("sand") -> "dust.svg"
        weatherMain.contains("tornado") -> "tornado.svg"
        else -> if (isDay) "clear-day.svg" else "clear-night.svg"
    }

    val svgPath = "file:///android_asset/weather_icons/$iconFileName"

    // Apply weather-specific animations
    val animatedModifier = when {
        weatherMain.contains("rain") -> modifier.then(rainAnimation())
        weatherMain.contains("snow") -> modifier.then(snowAnimation())
        weatherMain.contains("clear") -> modifier.then(sunAnimation())
        weatherMain.contains("clouds") -> modifier.then(cloudAnimation())
        weatherMain.contains("thunderstorm") -> modifier.then(stormAnimation())
        weatherMain.contains("fog") -> modifier.then(fogAnimation())
        else -> modifier.then(defaultAnimation())
    }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(svgPath)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = animatedModifier,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun rainAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rain_bounce"
    )

    return Modifier.graphicsLayer {
        translationY = offsetY
    }
}

@Composable
private fun snowAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "snow_drift"
    )

    return Modifier.graphicsLayer {
        translationX = offsetX
    }
}

@Composable
private fun sunAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_rotation"
    )

    return Modifier.rotate(rotation)
}

@Composable
private fun cloudAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "cloud")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_float"
    )

    return Modifier.graphicsLayer {
        translationX = offsetX
    }
}

@Composable
private fun stormAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "storm")
    val shake by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "storm_shake"
    )

    return Modifier.graphicsLayer {
        translationX = shake
        translationY = shake * 0.5f
    }
}

@Composable
private fun fogAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "fog")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fog_fade"
    )

    return Modifier.graphicsLayer {
        this.alpha = alpha
    }
}

@Composable
private fun defaultAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "default")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "default_pulse"
    )

    return Modifier.scale(scale)
}