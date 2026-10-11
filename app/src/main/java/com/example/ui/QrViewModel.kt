package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppTab
import com.example.model.ParsedScanDetails
import com.example.model.QrColorTheme
import com.example.model.QrHistoryItem
import com.example.model.QrType
import com.example.model.ToastMessage
import com.example.model.WifiSecurity
import com.example.util.QrCodeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class EliasQrUiState(
    val currentTab: AppTab = AppTab.GENERATE,
    val currentType: QrType = QrType.LINK,
    // Link input
    val linkInput: String = "",
    // Text input
    val textInput: String = "",
    // Email inputs
    val emailTo: String = "",
    val emailSubject: String = "",
    val emailMessage: String = "",
    // Phone input
    val phoneInput: String = "",
    // SMS inputs
    val smsPhone: String = "",
    val smsMessage: String = "",
    // WiFi inputs
    val wifiSsid: String = "",
    val wifiPassword: String = "",
    val wifiSecurity: WifiSecurity = WifiSecurity.WPA,
    val wifiHidden: Boolean = false,
    // Color theme
    val selectedColorTheme: QrColorTheme = QrColorTheme.CLASSIC,
    // Generated result
    val generatedQrData: String? = null,
    val generatedQrBitmap: Bitmap? = null,
    val brandedCardBitmap: Bitmap? = null,
    val showBrandedCardInPreview: Boolean = false,
    // Scanner state
    val isScanningCamera: Boolean = false,
    val isTorchOn: Boolean = false,
    val lastScanResult: String? = null,
    val parsedScanResult: ParsedScanDetails? = null,
    // History
    val historyItems: List<QrHistoryItem> = emptyList(),
    // Toast notification
    val activeToast: ToastMessage? = null
)

class QrViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("elias_qr_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(EliasQrUiState())
    val uiState: StateFlow<EliasQrUiState> = _uiState.asStateFlow()

    private var toastJob: Job? = null

    init {
        loadHistory()
    }

    fun switchTab(tab: AppTab) {
        if (_uiState.value.currentTab == tab) return
        _uiState.update { state ->
            state.copy(
                currentTab = tab,
                // Stop camera automatically if navigating away from Scan tab
                isScanningCamera = if (tab == AppTab.GENERATE) false else state.isScanningCamera,
                isTorchOn = if (tab == AppTab.GENERATE) false else state.isTorchOn
            )
        }
    }

    fun setQrType(type: QrType) {
        _uiState.update { it.copy(currentType = type) }
    }

    fun updateLinkInput(value: String) {
        _uiState.update { it.copy(linkInput = value) }
    }

    fun updateTextInput(value: String) {
        _uiState.update { it.copy(textInput = value) }
    }

    fun updateEmailTo(value: String) {
        _uiState.update { it.copy(emailTo = value) }
    }

    fun updateEmailSubject(value: String) {
        _uiState.update { it.copy(emailSubject = value) }
    }

    fun updateEmailMessage(value: String) {
        _uiState.update { it.copy(emailMessage = value) }
    }

    fun updatePhoneInput(value: String) {
        _uiState.update { it.copy(phoneInput = value) }
    }

    fun updateSmsPhone(value: String) {
        _uiState.update { it.copy(smsPhone = value) }
    }

    fun updateSmsMessage(value: String) {
        _uiState.update { it.copy(smsMessage = value) }
    }

    fun updateWifiSsid(value: String) {
        _uiState.update { it.copy(wifiSsid = value) }
    }

    fun updateWifiPassword(value: String) {
        _uiState.update { it.copy(wifiPassword = value) }
    }

    fun updateWifiSecurity(security: WifiSecurity) {
        _uiState.update { it.copy(wifiSecurity = security) }
    }

    fun updateWifiHidden(hidden: Boolean) {
        _uiState.update { it.copy(wifiHidden = hidden) }
    }

    fun selectColorTheme(theme: QrColorTheme) {
        _uiState.update { it.copy(selectedColorTheme = theme) }
        val existingData = _uiState.value.generatedQrData
        if (!existingData.isNullOrBlank()) {
            renderQrCode(existingData, theme, showSuccessToast = false)
        }
    }

    /**
     * Validates the current type's inputs and generates a QR code matching generateQRCode() in the HTML app.
     */
    fun generateQrCode() {
        val state = _uiState.value
        val qrData: String = try {
            when (state.currentType) {
                QrType.LINK -> {
                    val url = state.linkInput.trim()
                    if (url.isEmpty()) throw IllegalArgumentException("Please enter a URL.")
                    if (url.startsWith("http://", ignoreCase = true) ||
                        url.startsWith("https://", ignoreCase = true)
                    ) {
                        url
                    } else {
                        "https://$url"
                    }
                }

                QrType.TEXT -> {
                    val txt = state.textInput.trim()
                    if (txt.isEmpty()) throw IllegalArgumentException("Please enter some text.")
                    txt
                }

                QrType.EMAIL -> {
                    val to = state.emailTo.trim()
                    val sub = state.emailSubject.trim()
                    val msg = state.emailMessage.trim()
                    if (to.isEmpty()) throw IllegalArgumentException("Please enter an email address.")
                    "mailto:$to?subject=${Uri.encode(sub)}&body=${Uri.encode(msg)}"
                }

                QrType.PHONE -> {
                    val phone = state.phoneInput.trim()
                    if (phone.isEmpty()) throw IllegalArgumentException("Please enter a phone number.")
                    "tel:$phone"
                }

                QrType.SMS -> {
                    val smsTo = state.smsPhone.trim()
                    val smsMsg = state.smsMessage.trim()
                    if (smsTo.isEmpty()) throw IllegalArgumentException("Please enter a phone number.")
                    "smsto:$smsTo:$smsMsg"
                }

                QrType.WIFI -> {
                    val ssid = state.wifiSsid.trim()
                    val pass = state.wifiPassword
                    val sec = state.wifiSecurity.code
                    val hidden = state.wifiHidden
                    if (ssid.isEmpty()) throw IllegalArgumentException("Please enter network name (SSID).")
                    "WIFI:T:$sec;S:$ssid;P:$pass;H:$hidden;;"
                }
            }
        } catch (e: IllegalArgumentException) {
            showToast(e.message ?: "Invalid input", isError = true)
            return
        }

        renderQrCode(qrData, state.selectedColorTheme, showSuccessToast = true)
        addHistoryItem(
            rawValue = qrData,
            typeLabel = state.currentType.label,
            isScanned = false
        )
    }

    private fun renderQrCode(
        qrData: String,
        colorTheme: QrColorTheme,
        showSuccessToast: Boolean
    ) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                runCatching {
                    val qrBmp = QrCodeHelper.generateQrBitmap(
                        content = qrData,
                        sizePx = 640,
                        darkColor = colorTheme.darkColorArgb,
                        lightColor = colorTheme.lightColorArgb
                    )
                    val brandedBmp = QrCodeHelper.createBrandedShareCardBitmap(qrBmp, qrData)
                    Pair(qrBmp, brandedBmp)
                }
            }
            result.onSuccess { (qrBmp, brandedBmp) ->
                _uiState.update {
                    it.copy(
                        generatedQrData = qrData,
                        generatedQrBitmap = qrBmp,
                        brandedCardBitmap = brandedBmp,
                        showBrandedCardInPreview = false
                    )
                }
                if (showSuccessToast) {
                    showToast("QR Code Generated Successfully!", isError = false)
                }
            }.onFailure {
                showToast("Failed to generate QR code.", isError = true)
            }
        }
    }

    /**
     * Shares the branded QR code PNG via Android Share Sheet and updates preview to the branded card.
     */
    fun shareGeneratedQr(context: Context) {
        val state = _uiState.value
        val qrBitmap = state.generatedQrBitmap
        val qrData = state.generatedQrData
        if (qrBitmap == null || qrData.isNullOrBlank()) {
            showToast("No QR Code to download!", isError = true)
            return
        }

        // Show the branded card with title + info in preview, matching downloadQR() behavior
        _uiState.update { it.copy(showBrandedCardInPreview = true) }

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                QrCodeHelper.shareBrandedQrPng(context, qrBitmap, qrData)
            }
            result.onSuccess {
                showToast("শেয়ার সাকসেসফুল! (Ready to share PNG)", isError = false)
            }.onFailure {
                showToast("Could not launch share sheet.", isError = true)
            }
        }
    }

    /**
     * Saves the branded QR code PNG directly to the device's Pictures/EliasQRCode gallery folder.
     */
    fun saveGeneratedQrToGallery(context: Context) {
        val state = _uiState.value
        val qrBitmap = state.generatedQrBitmap
        val qrData = state.generatedQrData
        if (qrBitmap == null || qrData.isNullOrBlank()) {
            showToast("No QR Code to save!", isError = true)
            return
        }

        _uiState.update { it.copy(showBrandedCardInPreview = true) }

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                QrCodeHelper.saveBrandedQrToGallery(context, qrBitmap, qrData)
            }
            result.onSuccess {
                showToast("Saved PNG to Gallery (Pictures/EliasQRCode)!", isError = false)
            }.onFailure {
                showToast("Failed to save PNG to gallery.", isError = true)
            }
        }
    }

    fun startCameraScanning() {
        _uiState.update { it.copy(isScanningCamera = true) }
    }

    fun stopCameraScanning() {
        _uiState.update { it.copy(isScanningCamera = false, isTorchOn = false) }
    }

    fun toggleTorch() {
        _uiState.update { it.copy(isTorchOn = !it.isTorchOn) }
    }

    fun onCameraAccessError(message: String = "Camera access denied or not available.") {
        _uiState.update { it.copy(isScanningCamera = false, isTorchOn = false) }
        showToast(message, isError = true)
    }

    fun handleScanSuccess(decodedText: String) {
        if (decodedText.isBlank()) return
        // Avoid duplicate rapid triggers for the exact same code while camera is active
        if (_uiState.value.lastScanResult == decodedText && _uiState.value.isScanningCamera) {
            return
        }
        val parsed = QrCodeHelper.parseScannedContent(decodedText)
        _uiState.update {
            it.copy(
                lastScanResult = decodedText,
                parsedScanResult = parsed
            )
        }
        showToast("QR Code Scanned!", isError = false)
        addHistoryItem(
            rawValue = decodedText,
            typeLabel = parsed.typeLabel,
            isScanned = true
        )
    }

    /**
     * Scans a QR code from an image selected via the Android Photo Picker.
     */
    fun scanImageFromUri(context: Context, uri: Uri) {
        if (_uiState.value.isScanningCamera) {
            stopCameraScanning()
        }
        viewModelScope.launch {
            val decoded = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null) {
                            QrCodeHelper.decodeQrFromBitmap(bitmap)
                        } else {
                            null
                        }
                    }
                }.getOrNull()
            }
            if (!decoded.isNullOrBlank()) {
                handleScanSuccess(decoded)
            } else {
                showToast("No QR code found in this image.", isError = true)
            }
        }
    }

    /**
     * Loads a history item back into either the Generator preview or Scanner result.
     */
    fun selectHistoryItem(item: QrHistoryItem) {
        if (item.isScanned) {
            val parsed = QrCodeHelper.parseScannedContent(item.rawValue)
            _uiState.update {
                it.copy(
                    currentTab = AppTab.SCAN,
                    lastScanResult = item.rawValue,
                    parsedScanResult = parsed
                )
            }
            showToast("Loaded scanned QR from history")
        } else {
            _uiState.update { it.copy(currentTab = AppTab.GENERATE) }
            renderQrCode(item.rawValue, _uiState.value.selectedColorTheme, showSuccessToast = false)
            showToast("Loaded generated QR from history")
        }
    }

    fun clearHistory() {
        prefs.edit().remove("history_json").apply()
        _uiState.update { it.copy(historyItems = emptyList()) }
        showToast("History cleared")
    }

    fun showToast(message: String, isError: Boolean = false) {
        toastJob?.cancel()
        val toast = ToastMessage(
            id = System.currentTimeMillis(),
            message = message,
            isError = isError
        )
        _uiState.update { it.copy(activeToast = toast) }
        toastJob = viewModelScope.launch {
            delay(3200)
            _uiState.update { state ->
                if (state.activeToast?.id == toast.id) state.copy(activeToast = null) else state
            }
        }
    }

    private fun addHistoryItem(rawValue: String, typeLabel: String, isScanned: Boolean) {
        val currentList = _uiState.value.historyItems.toMutableList()
        currentList.removeAll { it.rawValue == rawValue && it.isScanned == isScanned }
        val newItem = QrHistoryItem(
            id = UUID.randomUUID().toString(),
            rawValue = rawValue,
            typeLabel = typeLabel,
            isScanned = isScanned,
            timestamp = System.currentTimeMillis()
        )
        currentList.add(0, newItem)
        val trimmed = currentList.take(15)
        _uiState.update { it.copy(historyItems = trimmed) }
        saveHistory(trimmed)
    }

    private fun saveHistory(items: List<QrHistoryItem>) {
        val arr = JSONArray()
        items.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("rawValue", item.rawValue)
                put("typeLabel", item.typeLabel)
                put("isScanned", item.isScanned)
                put("timestamp", item.timestamp)
            }
            arr.put(obj)
        }
        prefs.edit().putString("history_json", arr.toString()).apply()
    }

    private fun loadHistory() {
        val rawJson = prefs.getString("history_json", null) ?: return
        runCatching {
            val arr = JSONArray(rawJson)
            val list = mutableListOf<QrHistoryItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    QrHistoryItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        rawValue = obj.optString("rawValue", ""),
                        typeLabel = obj.optString("typeLabel", "QR Code"),
                        isScanned = obj.optBoolean("isScanned", false),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            _uiState.update { it.copy(historyItems = list.filter { item -> item.rawValue.isNotBlank() }) }
        }
    }
}
