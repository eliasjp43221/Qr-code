package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.ParsedScanDetails
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder
import java.util.EnumMap

object QrCodeHelper {

    private val decodeHints: Map<DecodeHintType, Any> = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
        put(
            DecodeHintType.POSSIBLE_FORMATS,
            listOf(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.AZTEC,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.CODE_128,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8
            )
        )
        put(DecodeHintType.TRY_HARDER, true)
        put(DecodeHintType.CHARACTER_SET, "UTF-8")
    }

    /**
     * Generates a QR Code Bitmap with ErrorCorrectionLevel.H matching the web app.
     */
    fun generateQrBitmap(
        content: String,
        sizePx: Int = 640,
        darkColor: Int = AndroidColor.BLACK,
        lightColor: Int = AndroidColor.WHITE
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            put(EncodeHintType.MARGIN, 1)
        }
        val bitMatrix = QRCodeWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx,
            hints
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) darkColor else lightColor
            }
        }
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }

    /**
     * Builds the composite branded PNG card identical to downloadQR() in the Elias QR Code script:
     * - White background with subtle #F1F5F9 border
     * - Centered QR Code
     * - Title: "Elias QR Code" in bold #1E293B
     * - Data preview (truncated to 38 chars) in #64748B
     * - Footer: "Generated safely on your device" in bold #8B5CF6
     */
    fun createBrandedShareCardBitmap(qrBitmap: Bitmap, qrData: String): Bitmap {
        val padding = 64
        val textSectionHeight = 220
        val sourceWidth = qrBitmap.width
        val sourceHeight = qrBitmap.height

        val canvasWidth = sourceWidth + (padding * 2)
        val canvasHeight = sourceHeight + (padding * 2) + textSectionHeight

        val resultBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // Fill solid white background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, canvasWidth.toFloat(), canvasHeight.toFloat(), bgPaint)

        // Subtle outer border (#F1F5F9)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        val borderRect = RectF(6f, 6f, canvasWidth - 6f, canvasHeight - 6f)
        canvas.drawRoundRect(borderRect, 28f, 28f, borderPaint)

        // Draw QR Code
        canvas.drawBitmap(qrBitmap, padding.toFloat(), padding.toFloat(), null)

        val centerX = canvasWidth / 2f
        val textBaseY = (sourceHeight + padding * 2).toFloat()

        // Title: "Elias QR Code"
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.parseColor("#1E293B")
            textSize = 44f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Elias QR Code", centerX, textBaseY + 34f, titlePaint)

        // Truncated Data Text
        val dataPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.parseColor("#64748B")
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        val displayData = if (qrData.length > 38) {
            qrData.substring(0, 35) + "..."
        } else {
            qrData
        }
        canvas.drawText(displayData, centerX, textBaseY + 95f, dataPaint)

        // Branding Footer: "Generated safely on your device"
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.parseColor("#8B5CF6")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Generated safely on your device", centerX, canvasHeight - 42f, footerPaint)

        return resultBitmap
    }

    /**
     * Saves the branded QR code PNG directly to the device gallery via MediaStore.
     */
    fun saveBrandedQrToGallery(context: Context, qrBitmap: Bitmap, qrData: String): Result<Uri> {
        return runCatching {
            val brandedBitmap = createBrandedShareCardBitmap(qrBitmap, qrData)
            val fileName = "Elias_QR_${System.currentTimeMillis()}.png"
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EliasQRCode")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IllegalStateException("Could not create MediaStore entry")

            resolver.openOutputStream(imageUri)?.use { outStream ->
                if (!brandedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                    throw IllegalStateException("Failed to compress PNG")
                }
            } ?: throw IllegalStateException("Could not open output stream")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
            imageUri
        }
    }

    /**
     * Shares the branded QR code PNG via Android's native Share Sheet (Intent.ACTION_SEND).
     */
    fun shareBrandedQrPng(context: Context, qrBitmap: Bitmap, qrData: String): Result<Unit> {
        return runCatching {
            val brandedBitmap = createBrandedShareCardBitmap(qrBitmap, qrData)
            val exportDir = File(context.cacheDir, "qr_exports").apply { mkdirs() }
            // Clean up old cached files
            exportDir.listFiles()?.forEach { it.delete() }

            val file = File(exportDir, "Elias_QR_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                brandedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TITLE, "Elias QR Code")
                putExtra(Intent.EXTRA_TEXT, "My Custom QR Code: $qrData")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Elias QR Code").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    }

    /**
     * Decodes a QR code from a gallery Bitmap using ZXing with multiple binarizer fallbacks.
     */
    fun decodeQrFromBitmap(originalBitmap: Bitmap): String? {
        val maxDim = 1400
        val bitmap = if (originalBitmap.width > maxDim || originalBitmap.height > maxDim) {
            val ratio = minOf(
                maxDim.toFloat() / originalBitmap.width,
                maxDim.toFloat() / originalBitmap.height
            )
            Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * ratio).toInt().coerceAtLeast(1),
                (originalBitmap.height * ratio).toInt().coerceAtLeast(1),
                true
            )
        } else {
            originalBitmap
        }

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, pixels)
        val reader = MultiFormatReader().apply { setHints(decodeHints) }

        // 1. Standard HybridBinarizer
        try {
            return reader.decodeWithState(BinaryBitmap(HybridBinarizer(source))).text
        } catch (_: Exception) {
            reader.reset()
        }

        // 2. GlobalHistogramBinarizer
        try {
            return reader.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(source))).text
        } catch (_: Exception) {
            reader.reset()
        }

        // 3. Inverted Luminance Source (for white-on-dark QR codes)
        try {
            return reader.decodeWithState(BinaryBitmap(HybridBinarizer(source.invert()))).text
        } catch (_: Exception) {
            reader.reset()
        }

        return null
    }

    /**
     * Decodes a QR code from CameraX YUV_420_888 Y-plane bytes.
     */
    fun decodeQrFromYuvData(yData: ByteArray, width: Int, height: Int): String? {
        val source = PlanarYUVLuminanceSource(
            yData,
            width,
            height,
            0,
            0,
            width,
            height,
            false
        )
        val reader = MultiFormatReader().apply { setHints(decodeHints) }
        try {
            return reader.decodeWithState(BinaryBitmap(HybridBinarizer(source))).text
        } catch (_: Exception) {
            reader.reset()
        }
        try {
            return reader.decodeWithState(BinaryBitmap(HybridBinarizer(source.invert()))).text
        } catch (_: Exception) {
            reader.reset()
        }
        return null
    }

    /**
     * Parses raw scanned QR payload into rich, user-friendly metadata and action links.
     */
    fun parseScannedContent(raw: String): ParsedScanDetails {
        val trimmed = raw.trim()
        return when {
            trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) -> {
                ParsedScanDetails(
                    typeLabel = "Website Link",
                    primaryText = trimmed,
                    actionUri = trimmed,
                    actionButtonText = "Open",
                    isHttpLink = true
                )
            }

            trimmed.startsWith("WIFI:", ignoreCase = true) -> {
                val body = trimmed.substringAfter("WIFI:", "")
                val parts = body.split(";").filter { it.contains(":") }
                val map = mutableMapOf<String, String>()
                for (part in parts) {
                    val key = part.substringBefore(":").uppercase()
                    val value = part.substringAfter(":")
                    if (value.isNotEmpty()) {
                        when (key) {
                            "S" -> map["Network (SSID)"] = value
                            "P" -> map["Password"] = value
                            "T" -> map["Security"] = if (value == "nopass") "None" else value
                            "H" -> if (value.equals("true", ignoreCase = true)) {
                                map["Hidden"] = "Yes"
                            }
                        }
                    }
                }
                ParsedScanDetails(
                    typeLabel = "WiFi Network",
                    primaryText = trimmed,
                    metadataMap = map
                )
            }

            trimmed.startsWith("mailto:", ignoreCase = true) -> {
                val withoutScheme = trimmed.substringAfter("mailto:")
                val emailAddr = withoutScheme.substringBefore("?")
                val query = withoutScheme.substringAfter("?", "")
                val meta = mutableMapOf<String, String>()
                if (emailAddr.isNotBlank()) meta["To"] = emailAddr
                query.split("&").forEach { param ->
                    val k = param.substringBefore("=").lowercase()
                    val v = runCatching {
                        URLDecoder.decode(param.substringAfter("=", ""), "UTF-8")
                    }.getOrDefault(param.substringAfter("=", ""))
                    if (v.isNotBlank()) {
                        if (k == "subject") meta["Subject"] = v
                        if (k == "body") meta["Message"] = v
                    }
                }
                ParsedScanDetails(
                    typeLabel = "Email Message",
                    primaryText = trimmed,
                    metadataMap = meta,
                    actionUri = trimmed,
                    actionButtonText = "Email"
                )
            }

            trimmed.startsWith("tel:", ignoreCase = true) -> {
                val phone = trimmed.substringAfter("tel:")
                ParsedScanDetails(
                    typeLabel = "Phone Number",
                    primaryText = trimmed,
                    metadataMap = mapOf("Phone" to phone),
                    actionUri = trimmed,
                    actionButtonText = "Call"
                )
            }

            trimmed.startsWith("smsto:", ignoreCase = true) ||
                trimmed.startsWith("sms:", ignoreCase = true) -> {
                val payload = trimmed.substringAfter(":")
                val number = payload.substringBefore(":")
                val message = payload.substringAfter(":", "")
                val meta = mutableMapOf("Phone" to number)
                if (message.isNotBlank()) meta["Message"] = message
                ParsedScanDetails(
                    typeLabel = "SMS Message",
                    primaryText = trimmed,
                    metadataMap = meta,
                    actionUri = "smsto:$number",
                    actionButtonText = "SMS"
                )
            }

            else -> {
                ParsedScanDetails(
                    typeLabel = "Plain Text",
                    primaryText = trimmed
                )
            }
        }
    }
}
