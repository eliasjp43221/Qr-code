package com.example.ui.components

import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.Pink500
import com.example.ui.theme.Violet500
import com.example.util.QrCodeHelper
import java.util.concurrent.Executors

@Composable
fun CameraScannerView(
    isTorchOn: Boolean,
    onToggleTorch: () -> Unit,
    onQrDetected: (String) -> Unit,
    onCameraError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    var activeCamera by remember { mutableStateOf<Camera?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(isTorchOn, activeCamera) {
        runCatching {
            activeCamera?.cameraControl?.enableTorch(isTorchOn)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val mainExecutor = ContextCompat.getMainExecutor(context)

        val listener = Runnable {
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(analysisExecutor) { imageProxy ->
                            processImageProxy(imageProxy) { decoded ->
                                mainExecutor.execute {
                                    onQrDetected(decoded)
                                }
                            }
                        }
                    }

                val cameraSelector = when {
                    cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ->
                        CameraSelector.DEFAULT_BACK_CAMERA
                    cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ->
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    else -> null
                }

                if (cameraSelector == null) {
                    onCameraError("No camera available on this device.")
                } else {
                    cameraProvider.unbindAll()
                    activeCamera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                }
            } catch (e: Exception) {
                onCameraError("Camera access denied or not available.")
            }
        }

        cameraProviderFuture.addListener(listener, mainExecutor)

        onDispose {
            runCatching {
                cameraProviderFuture.get().unbindAll()
            }
            analysisExecutor.shutdown()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_line_progress"
    )

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .testTag("camera_preview_view")
        )

        // Viewfinder target frame & animated laser line
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boxSide = minOf(size.width, size.height) * 0.68f
            val left = (size.width - boxSide) / 2f
            val top = (size.height - boxSide) / 2f
            val right = left + boxSide
            val bottom = top + boxSide
            val cornerLen = boxSide * 0.16f
            val strokePx = 4.dp.toPx()

            // Subtle target border
            drawRoundRect(
                color = Color.White.copy(alpha = 0.25f),
                topLeft = Offset(left, top),
                size = Size(boxSide, boxSide),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Corner brackets in Violet & Pink gradient
            val cornerBrush = Brush.linearGradient(
                colors = listOf(Violet500, Pink500),
                start = Offset(left, top),
                end = Offset(right, bottom)
            )

            // Top-left corner
            drawLine(
                brush = cornerBrush,
                start = Offset(left, top + cornerLen),
                end = Offset(left, top),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
            drawLine(
                brush = cornerBrush,
                start = Offset(left, top),
                end = Offset(left + cornerLen, top),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )

            // Top-right corner
            drawLine(
                brush = cornerBrush,
                start = Offset(right - cornerLen, top),
                end = Offset(right, top),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
            drawLine(
                brush = cornerBrush,
                start = Offset(right, top),
                end = Offset(right, top + cornerLen),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )

            // Bottom-left corner
            drawLine(
                brush = cornerBrush,
                start = Offset(left, bottom - cornerLen),
                end = Offset(left, bottom),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
            drawLine(
                brush = cornerBrush,
                start = Offset(left, bottom),
                end = Offset(left + cornerLen, bottom),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )

            // Bottom-right corner
            drawLine(
                brush = cornerBrush,
                start = Offset(right - cornerLen, bottom),
                end = Offset(right, bottom),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
            drawLine(
                brush = cornerBrush,
                start = Offset(right, bottom),
                end = Offset(right, bottom - cornerLen),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )

            // Animated laser scan line
            val laserY = top + (boxSide * laserProgress)
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Violet500,
                        Pink500,
                        Violet500,
                        Color.Transparent
                    ),
                    startX = left,
                    endX = right
                ),
                start = Offset(left + 12f, laserY),
                end = Offset(right - 12f, laserY),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Torch / Flashlight toggle button
        IconButton(
            onClick = onToggleTorch,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(48.dp)
                .background(
                    color = if (isTorchOn) Violet500.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.55f),
                    shape = CircleShape
                )
                .testTag("btn_toggle_torch")
        ) {
            Icon(
                imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = if (isTorchOn) "Turn off flashlight" else "Turn on flashlight",
                tint = Color.White
            )
        }
    }
}

private fun processImageProxy(
    imageProxy: ImageProxy,
    onResult: (String) -> Unit
) {
    try {
        val plane = imageProxy.planes.firstOrNull() ?: return
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val width = imageProxy.width
        val height = imageProxy.height

        val yBytes = ByteArray(width * height)
        if (rowStride == width) {
            buffer.rewind()
            buffer.get(yBytes, 0, width * height)
        } else {
            val rowData = ByteArray(rowStride)
            buffer.rewind()
            for (row in 0 until height) {
                val length = if (row == height - 1) width else rowStride
                if (buffer.remaining() >= length) {
                    buffer.get(rowData, 0, length)
                    System.arraycopy(rowData, 0, yBytes, row * width, width)
                }
            }
        }

        val decoded = QrCodeHelper.decodeQrFromYuvData(yBytes, width, height)
        if (!decoded.isNullOrBlank()) {
            onResult(decoded)
        }
    } catch (_: Exception) {
        // Ignore single frame decode errors
    } finally {
        imageProxy.close()
    }
}
