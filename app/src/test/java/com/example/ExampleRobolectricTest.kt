package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.QrCodeHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Elias QR Code", appName)
    }

    @Test
    fun `generate and decode QR code round-trip`() {
        val payload = "https://github.com/elias-qr"
        val qrBitmap = QrCodeHelper.generateQrBitmap(payload, sizePx = 400)
        assertNotNull(qrBitmap)
        val decoded = QrCodeHelper.decodeQrFromBitmap(qrBitmap)
        assertEquals(payload, decoded)
    }

    @Test
    fun `parse WiFi QR payload`() {
        val wifiPayload = "WIFI:T:WPA;S:EliasHomeNet;P:SecretPass123;H:false;;"
        val parsed = QrCodeHelper.parseScannedContent(wifiPayload)
        assertEquals("WiFi Network", parsed.typeLabel)
        assertEquals("EliasHomeNet", parsed.metadataMap["Network (SSID)"])
        assertEquals("SecretPass123", parsed.metadataMap["Password"])
        assertEquals("WPA", parsed.metadataMap["Security"])
    }
}
