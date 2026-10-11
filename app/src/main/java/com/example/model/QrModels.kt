package com.example.model

enum class AppTab {
    GENERATE,
    SCAN
}

enum class QrType(val id: String, val label: String) {
    LINK("link", "Link"),
    TEXT("text", "Text"),
    EMAIL("email", "Email"),
    PHONE("phone", "Phone"),
    SMS("sms", "SMS"),
    WIFI("wifi", "WiFi")
}

enum class WifiSecurity(val code: String, val label: String) {
    WPA("WPA", "WPA/WPA2"),
    WEP("WEP", "WEP"),
    NONE("nopass", "None")
}

enum class QrColorTheme(
    val label: String,
    val darkColorArgb: Int,
    val lightColorArgb: Int
) {
    CLASSIC("Classic Black", 0xFF000000.toInt(), 0xFFFFFFFF.toInt()),
    VIOLET("Elias Violet", 0xFF5B21B6.toInt(), 0xFFFFFFFF.toInt()),
    BERRY("Berry Pink", 0xFF9D174D.toInt(), 0xFFFFFFFF.toInt()),
    SLATE("Midnight Slate", 0xFF0F172A.toInt(), 0xFFF8FAFC.toInt()),
    EMERALD("Cyber Emerald", 0xFF065F46.toInt(), 0xFFFFFFFF.toInt())
}

data class ToastMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val isError: Boolean = false
)

data class QrHistoryItem(
    val id: String,
    val rawValue: String,
    val typeLabel: String,
    val isScanned: Boolean,
    val timestamp: Long
)

data class ParsedScanDetails(
    val typeLabel: String,
    val primaryText: String,
    val metadataMap: Map<String, String> = emptyMap(),
    val actionUri: String? = null,
    val actionButtonText: String? = null,
    val isHttpLink: Boolean = false
)
