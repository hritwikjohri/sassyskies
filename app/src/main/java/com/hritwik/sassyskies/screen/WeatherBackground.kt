//package com.hritwik.sassyskies.screen
//
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.keyframes
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.withFrameNanos
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.graphics.drawscope.rotate
//import androidx.compose.ui.platform.LocalInspectionMode
//import com.hritwik.sassyskies.model.WeatherResponse
//import kotlinx.coroutines.delay
//import kotlin.math.sin
//import kotlin.random.Random
//
//@Composable
//fun DynamicWeatherBackground(
//    weather: WeatherResponse,
//    modifier: Modifier = Modifier,
//    content: @Composable () -> Unit
//) {
//    val weatherCondition = weather.weather.firstOrNull()?.main?.lowercase() ?: "clear"
//    val isDay = weather.weather.firstOrNull()?.icon?.endsWith("d") == true
//    val temperature = weather.main.temp.toInt()
//    val isInPreview = LocalInspectionMode.current
//
//    Box(modifier = modifier.fillMaxSize()) {
//        // Base gradient background
//        BaseGradientBackground(
//            weatherCondition = weatherCondition,
//            isDay = isDay,
//            temperature = temperature
//        )
//
//        // Weather-specific animated overlay (skip in preview mode for performance)
//        if (!isInPreview) {
//            when {
//                weatherCondition.contains("rain") -> RainBackground()
//                weatherCondition.contains("snow") -> SnowBackground()
//                weatherCondition.contains("storm") || weatherCondition.contains("thunder") -> StormBackground()
//                weatherCondition.contains("cloud") -> CloudyBackground(isDay)
//                weatherCondition.contains("clear") && !isDay -> StarryBackground()
//                weatherCondition.contains("fog") || weatherCondition.contains("mist") -> FogBackground()
//                weatherCondition.contains("wind") -> WindyBackground()
//                else -> StarryBackground()
//            }
//        }
//
//        // Content overlay
//        content()
//    }
//}
//
//@Composable
//private fun BaseGradientBackground(
//    weatherCondition: String,
//    isDay: Boolean,
//    temperature: Int
//) {
//    val colors = remember(weatherCondition, isDay, temperature) {
//        getEnhancedGradientColors(weatherCondition, isDay, temperature)
//    }
//
//    val infiniteTransition = rememberInfiniteTransition(label = "background_gradient")
//    val animatedOffset by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 25000, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "gradient_animation"
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = colors,
//                    startY = animatedOffset * 300f,
//                    endY = Float.POSITIVE_INFINITY
//                )
//            )
//    )
//}
//
//@Composable
//private fun RainBackground() {
//    val rainDrops = remember {
//        List(80) {
//            RainDrop(
//                x = Random.nextFloat(),
//                y = Random.nextFloat(),
//                speed = Random.nextFloat() * 0.025f + 0.015f,
//                length = Random.nextFloat() * 30f + 15f,
//                opacity = Random.nextFloat() * 0.4f + 0.3f
//            )
//        }
//    }
//
//    var animationTime by remember { mutableFloatStateOf(0f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            withFrameNanos { frameTimeNanos ->
//                animationTime = (frameTimeNanos / 1_000_000_000f)
//            }
//            delay(16)
//        }
//    }
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        rainDrops.forEach { drop ->
//            drawEnhancedRainDrop(drop, animationTime, size)
//        }
//    }
//}
//
//@Composable
//private fun SnowBackground() {
//    val snowFlakes = remember {
//        List(60) {
//            SnowFlake(
//                x = Random.nextFloat(),
//                y = Random.nextFloat(),
//                speed = Random.nextFloat() * 0.008f + 0.004f,
//                size = Random.nextFloat() * 6f + 2f,
//                swing = Random.nextFloat() * 0.025f + 0.015f,
//                opacity = Random.nextFloat() * 0.6f + 0.4f
//            )
//        }
//    }
//
//    var animationTime by remember { mutableFloatStateOf(0f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            withFrameNanos { frameTimeNanos ->
//                animationTime = (frameTimeNanos / 1_000_000_000f)
//            }
//            delay(16)
//        }
//    }
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        snowFlakes.forEach { flake ->
//            drawEnhancedSnowFlake(flake, animationTime, size)
//        }
//    }
//}
//
//@Composable
//private fun StormBackground() {
//    val infiniteTransition = rememberInfiniteTransition(label = "storm")
//
//    // Enhanced lightning flash effect
//    val lightningAlpha by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 0.9f,
//        animationSpec = infiniteRepeatable(
//            animation = keyframes {
//                durationMillis = 4000
//                0f at 0
//                0f at 2000
//                0.9f at 2030
//                0f at 2050
//                0.7f at 2070
//                0f at 2090
//                0.5f at 2110
//                0f at 2130
//                0f at 4000
//            },
//            repeatMode = RepeatMode.Restart
//        ),
//        label = "lightning"
//    )
//
//    // Multiple cloud layers
//    val cloudOffset1 by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 200f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 12000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ),
//        label = "clouds1"
//    )
//
//    val cloudOffset2 by infiniteTransition.animateFloat(
//        initialValue = 100f,
//        targetValue = -100f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 15000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ),
//        label = "clouds2"
//    )
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        // Draw multiple cloud layers
//        drawRect(
//            color = Color.Black.copy(alpha = 0.4f),
//            topLeft = Offset(cloudOffset1 - 100f, 0f),
//            size = Size(size.width / 2.5f, size.height / 3)
//        )
//
//        drawRect(
//            color = Color.Black.copy(alpha = 0.3f),
//            topLeft = Offset(size.width - cloudOffset2, size.height / 8),
//            size = Size(size.width / 3, size.height / 4)
//        )
//
//        drawRect(
//            color = Color.Black.copy(alpha = 0.25f),
//            topLeft = Offset(cloudOffset1 / 2, size.height / 6),
//            size = Size(size.width / 4, size.height / 5)
//        )
//
//        // Enhanced lightning flash
//        if (lightningAlpha > 0f) {
//            drawRect(
//                color = Color.White.copy(alpha = lightningAlpha * 0.8f),
//                size = size
//            )
//
//            // Lightning bolts effect
//            drawRect(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color.White.copy(alpha = lightningAlpha),
//                        Color.Transparent
//                    )
//                ),
//                topLeft = Offset(size.width * 0.3f, 0f),
//                size = Size(3f, size.height * 0.6f)
//            )
//
//            drawRect(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color.White.copy(alpha = lightningAlpha * 0.7f),
//                        Color.Transparent
//                    )
//                ),
//                topLeft = Offset(size.width * 0.7f, 0f),
//                size = Size(2f, size.height * 0.4f)
//            )
//        }
//    }
//
//    // Add heavy rain effect
//    RainBackground()
//}
//
//@Composable
//private fun CloudyBackground(isDay: Boolean) {
//    val clouds = remember {
//        List(10) {
//            CloudData(
//                x = Random.nextFloat(),
//                y = Random.nextFloat() * 0.7f,
//                size = Random.nextFloat() * 120f + 80f,
//                speed = Random.nextFloat() * 0.008f + 0.003f,
//                opacity = Random.nextFloat() * 0.4f + 0.2f,
//                layer = Random.nextInt(3)
//            )
//        }
//    }
//
//    var animationTime by remember { mutableFloatStateOf(0f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            withFrameNanos { frameTimeNanos ->
//                animationTime = (frameTimeNanos / 1_000_000_000f)
//            }
//            delay(16)
//        }
//    }
//
//    val cloudColor = if (isDay) Color.White else Color.Gray
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        // Draw clouds in layers for depth
//        clouds.sortedBy { it.layer }.forEach { cloud ->
//            drawEnhancedCloud(cloud, animationTime, size, cloudColor, isDay)
//        }
//    }
//}
//
//@Composable
//private fun StarryBackground() {
//    val stars = remember {
//        List(120) {
//            Star(
//                x = Random.nextFloat(),
//                y = Random.nextFloat(),
//                size = Random.nextFloat() * 2.5f + 0.5f,
//                twinkleSpeed = Random.nextFloat() * 1.5f + 0.5f,
//                brightness = Random.nextFloat() * 0.6f + 0.4f
//            )
//        }
//    }
//
//    var animationTime by remember { mutableFloatStateOf(0f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            withFrameNanos { frameTimeNanos ->
//                animationTime = (frameTimeNanos / 1_000_000_000f)
//            }
//            delay(16)
//        }
//    }
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        stars.forEach { star ->
//            val twinkle = (sin(animationTime * star.twinkleSpeed) + 1f) / 2f
//            val alpha = star.brightness * twinkle
//
//            drawCircle(
//                color = Color.White.copy(alpha = alpha),
//                radius = star.size,
//                center = Offset(star.x * size.width, star.y * size.height)
//            )
//
//            // Add sparkle effect for brighter stars
//            if (star.brightness > 0.8f && twinkle > 0.7f) {
//                drawCircle(
//                    color = Color.White.copy(alpha = alpha * 0.5f),
//                    radius = star.size * 1.5f,
//                    center = Offset(star.x * size.width, star.y * size.height)
//                )
//            }
//        }
//
//        // Enhanced moon
//        val moonX = size.width * 0.85f
//        val moonY = size.height * 0.12f
//
//        // Moon glow
//        drawCircle(
//            brush = Brush.radialGradient(
//                colors = listOf(
//                    Color.White.copy(alpha = 0.3f),
//                    Color.Transparent
//                ),
//                center = Offset(moonX, moonY),
//                radius = 80f
//            ),
//            radius = 80f,
//            center = Offset(moonX, moonY)
//        )
//
//        // Moon body
//        drawCircle(
//            brush = Brush.radialGradient(
//                colors = listOf(
//                    Color.White.copy(alpha = 0.95f),
//                    Color.White.copy(alpha = 0.8f)
//                ),
//                center = Offset(moonX, moonY),
//                radius = 35f
//            ),
//            radius = 35f,
//            center = Offset(moonX, moonY)
//        )
//    }
//}
//
//@Composable
//private fun FogBackground() {
//    val fogLayers = remember {
//        List(7) {
//            FogLayer(
//                y = Random.nextFloat() * 0.9f,
//                speed = Random.nextFloat() * 0.004f + 0.001f,
//                opacity = Random.nextFloat() * 0.4f + 0.1f,
//                height = Random.nextFloat() * 180f + 80f,
//                density = Random.nextFloat() * 0.3f + 0.2f
//            )
//        }
//    }
//
//    var animationTime by remember { mutableFloatStateOf(0f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            withFrameNanos { frameTimeNanos ->
//                animationTime = (frameTimeNanos / 1_000_000_000f)
//            }
//            delay(16)
//        }
//    }
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        fogLayers.forEach { layer ->
//            val x = (animationTime * layer.speed * size.width) % (size.width + 300f) - 150f
//
//            drawRect(
//                brush = Brush.horizontalGradient(
//                    colors = listOf(
//                        Color.Transparent,
//                        Color.Gray.copy(alpha = layer.opacity * layer.density),
//                        Color.LightGray.copy(alpha = layer.opacity),
//                        Color.Gray.copy(alpha = layer.opacity * layer.density),
//                        Color.Transparent
//                    )
//                ),
//                topLeft = Offset(x, layer.y * size.height),
//                size = Size(300f, layer.height)
//            )
//        }
//    }
//}
//
//@Composable
//private fun WindyBackground() {
//    val windElements = remember {
//        List(25) {
//            WindElement(
//                x = Random.nextFloat(),
//                y = Random.nextFloat(),
//                speedX = Random.nextFloat() * 0.015f + 0.008f,
//                speedY = Random.nextFloat() * 0.003f + 0.001f,
//                rotation = Random.nextFloat() * 360f,
//                rotationSpeed = Random.nextFloat() * 8f + 3f,
//                size = Random.nextFloat() * 6f + 3f,
//                type = Random.nextInt(3) // 0: leaf, 1: particle, 2: debris
//            )
//        }
//    }
//
//    var animationTime by remember { mutableFloatStateOf(0f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            withFrameNanos { frameTimeNanos ->
//                animationTime = (frameTimeNanos / 1_000_000_000f)
//            }
//            delay(16)
//        }
//    }
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        windElements.forEach { element ->
//            val currentX = (element.x + animationTime * element.speedX) % 1.3f - 0.15f
//            val currentY = (element.y + animationTime * element.speedY +
//                    sin(animationTime * 2f) * 0.1f) % 1.2f - 0.1f
//            val currentRotation = element.rotation + animationTime * element.rotationSpeed
//
//            if (currentX >= -0.15f && currentX <= 1.15f && currentY >= -0.1f && currentY <= 1.1f) {
//                rotate(currentRotation, Offset(currentX * size.width, currentY * size.height)) {
//                    when (element.type) {
//                        0 -> { // Leaf
//                            drawCircle(
//                                color = Color(0xFF4CAF50).copy(alpha = 0.6f),
//                                radius = element.size,
//                                center = Offset(currentX * size.width, currentY * size.height)
//                            )
//                        }
//                        1 -> { // Particle
//                            drawCircle(
//                                color = Color(0xFFBDBDBD).copy(alpha = 0.5f),
//                                radius = element.size * 0.6f,
//                                center = Offset(currentX * size.width, currentY * size.height)
//                            )
//                        }
//                        2 -> { // Debris
//                            drawRect(
//                                color = Color(0xFF8D6E63).copy(alpha = 0.4f),
//                                topLeft = Offset(
//                                    currentX * size.width - element.size/2,
//                                    currentY * size.height - element.size/2
//                                ),
//                                size = Size(element.size, element.size)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Enhanced helper function for gradient colors
//private fun getEnhancedGradientColors(
//    weatherCondition: String,
//    isDay: Boolean,
//    temperature: Int
//): List<Color> {
//    return when {
//        weatherCondition.contains("rain") -> if (isDay) {
//            listOf(
//                Color(0xFF87CEEB),
//                Color(0xFF4682B4),
//                Color(0xFF2E5266),
//                Color(0xFF1E3A8A)
//            )
//        } else {
//            listOf(
//                Color(0xFF2C3E50),
//                Color(0xFF34495E),
//                Color(0xFF1C2833),
//                Color(0xFF0F172A)
//            )
//        }
//
//        weatherCondition.contains("snow") -> if (isDay) {
//            listOf(
//                Color(0xFFF8FAFC),
//                Color(0xFFE2E8F0),
//                Color(0xFFCBD5E1),
//                Color(0xFFB0BEC5)
//            )
//        } else {
//            listOf(
//                Color(0xFF475569),
//                Color(0xFF334155),
//                Color(0xFF1E293B),
//                Color(0xFF0F172A)
//            )
//        }
//
//        weatherCondition.contains("storm") ->
//            listOf(
//                Color(0xFF1E293B),
//                Color(0xFF0F172A),
//                Color(0xFF020617),
//                Color(0xFF000000)
//            )
//
//        weatherCondition.contains("cloud") -> if (isDay) {
//            listOf(
//                Color(0xFFE2E8F0),
//                Color(0xFF94A3B8),
//                Color(0xFF64748B),
//                Color(0xFF475569)
//            )
//        } else {
//            listOf(
//                Color(0xFF475569),
//                Color(0xFF334155),
//                Color(0xFF1E293B),
//                Color(0xFF0F172A)
//            )
//        }
//
//        weatherCondition.contains("clear") -> if (isDay) {
//            when {
//                temperature > 30 -> listOf(
//                    Color(0xFFFEF3C7),
//                    Color(0xFFFBBF24),
//                    Color(0xFFF59E0B),
//                    Color(0xFFEA580C)
//                )
//                temperature < 10 -> listOf(
//                    Color(0xFFDBEAFE),
//                    Color(0xFF93C5FD),
//                    Color(0xFF3B82F6),
//                    Color(0xFF1E40AF)
//                )
//                else -> listOf(
//                    Color(0xFFECFDF5),
//                    Color(0xFF6EE7B7),
//                    Color(0xFF10B981),
//                    Color(0xFF047857)
//                )
//            }
//        } else {
//            listOf(
//                Color(0xFF1E1B4B),
//                Color(0xFF312E81),
//                Color(0xFF3730A3),
//                Color(0xFF1E40AF)
//            )
//        }
//
//        weatherCondition.contains("fog") || weatherCondition.contains("mist") ->
//            listOf(
//                Color(0xFFF1F5F9),
//                Color(0xFFCBD5E1),
//                Color(0xFF94A3B8),
//                Color(0xFF64748B)
//            )
//
//        else -> if (isDay) {
//            listOf(
//                Color(0xFFE0F2FE),
//                Color(0xFF7DD3FC),
//                Color(0xFF0EA5E9),
//                Color(0xFF0284C7)
//            )
//        } else {
//            listOf(
//                Color(0xFF1E1B4B),
//                Color(0xFF312E81),
//                Color(0xFF3730A3),
//                Color(0xFF1E40AF)
//            )
//        }
//    }
//}
//
//// Enhanced data classes
//private data class RainDrop(
//    val x: Float,
//    var y: Float,
//    val speed: Float,
//    val length: Float,
//    val opacity: Float
//)
//
//private data class SnowFlake(
//    val x: Float,
//    var y: Float,
//    val speed: Float,
//    val size: Float,
//    val swing: Float,
//    val opacity: Float
//)
//
//private data class CloudData(
//    val x: Float,
//    val y: Float,
//    val size: Float,
//    val speed: Float,
//    val opacity: Float,
//    val layer: Int
//)
//
//private data class Star(
//    val x: Float,
//    val y: Float,
//    val size: Float,
//    val twinkleSpeed: Float,
//    val brightness: Float
//)
//
//private data class FogLayer(
//    val y: Float,
//    val speed: Float,
//    val opacity: Float,
//    val height: Float,
//    val density: Float
//)
//
//private data class WindElement(
//    val x: Float,
//    val y: Float,
//    val speedX: Float,
//    val speedY: Float,
//    var rotation: Float,
//    val rotationSpeed: Float,
//    val size: Float,
//    val type: Int
//)
//
//// Enhanced drawing functions
//private fun DrawScope.drawEnhancedRainDrop(drop: RainDrop, time: Float, canvasSize: Size) {
//    val currentY = (drop.y + time * drop.speed) % 1.3f - 0.15f
//
//    if (currentY >= -0.15f && currentY <= 1.15f) {
//        drawLine(
//            brush = Brush.verticalGradient(
//                colors = listOf(
//                    Color.Blue.copy(alpha = drop.opacity * 0.8f),
//                    Color.Cyan.copy(alpha = drop.opacity * 0.6f),
//                    Color.Transparent
//                )
//            ),
//            start = Offset(drop.x * canvasSize.width, currentY * canvasSize.height),
//            end = Offset(drop.x * canvasSize.width, (currentY * canvasSize.height) + drop.length),
//            strokeWidth = 2.5f
//        )
//    }
//}
//
//private fun DrawScope.drawEnhancedSnowFlake(flake: SnowFlake, time: Float, canvasSize: Size) {
//    val currentY = (flake.y + time * flake.speed) % 1.3f - 0.15f
//    val swingOffset = sin(time * 1.5f) * flake.swing * canvasSize.width
//
//    if (currentY >= -0.15f && currentY <= 1.15f) {
//        drawCircle(
//            brush = Brush.radialGradient(
//                colors = listOf(
//                    Color.White.copy(alpha = flake.opacity),
//                    Color.White.copy(alpha = flake.opacity * 0.5f),
//                    Color.Transparent
//                )
//            ),
//            radius = flake.size,
//            center = Offset(flake.x * canvasSize.width + swingOffset, currentY * canvasSize.height)
//        )
//    }
//}
//
//private fun DrawScope.drawEnhancedCloud(
//    cloud: CloudData,
//    time: Float,
//    canvasSize: Size,
//    baseColor: Color,
//    isDay: Boolean
//) {
//    val currentX = (cloud.x + time * cloud.speed * (cloud.layer + 1)) % 1.4f - 0.2f
//
//    if (currentX >= -0.3f && currentX <= 1.3f) {
//        val layerAlpha = cloud.opacity * (1f - cloud.layer * 0.1f)
//        val color = if (isDay) {
//            baseColor.copy(alpha = layerAlpha)
//        } else {
//            baseColor.copy(alpha = layerAlpha * 0.7f)
//        }
//
//        // Draw cloud as multiple overlapping circles with better shading
//        repeat(6) { i ->
//            val offsetX = (i - 2.5f) * cloud.size * 0.25f
//            val offsetY = sin(i.toFloat() * 0.8f) * cloud.size * 0.15f
//            val circleSize = cloud.size * (0.7f + i * 0.08f)
//
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(
//                        color,
//                        color.copy(alpha = layerAlpha * 0.6f),
//                        Color.Transparent
//                    ),
//                    radius = circleSize
//                ),
//                radius = circleSize,
//                center = Offset(
//                    currentX * canvasSize.width + offsetX,
//                    cloud.y * canvasSize.height + offsetY
//                )
//            )
//        }
//    }
//}