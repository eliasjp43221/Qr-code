package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.QrHistoryItem
import com.example.ui.EliasQrUiState
import com.example.ui.theme.Pink50
import com.example.ui.theme.Pink500
import com.example.ui.theme.Pink600
import com.example.ui.theme.Red500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Violet50
import com.example.ui.theme.Violet500
import com.example.ui.theme.Violet600

@Composable
fun ScannerSection(
    uiState: EliasQrUiState,
    isExpanded: Boolean,
    onStartCameraClick: () -> Unit,
    onStopCameraClick: () -> Unit,
    onPickImageClick: () -> Unit,
    onToggleTorch: () -> Unit,
    onQrDetected: (String) -> Unit,
    onCameraError: (String) -> Unit,
    onCopyResultClick: (String) -> Unit,
    onOpenActionUriClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isExpanded) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            ScannerCameraCard(
                uiState = uiState,
                isExpanded = true,
                onStartCameraClick = onStartCameraClick,
                onStopCameraClick = onStopCameraClick,
                onPickImageClick = onPickImageClick,
                onToggleTorch = onToggleTorch,
                onQrDetected = onQrDetected,
                onCameraError = onCameraError,
                modifier = Modifier.weight(0.62f)
            )
            ScanResultCard(
                uiState = uiState,
                onCopyResultClick = onCopyResultClick,
                onOpenActionUriClick = onOpenActionUriClick,
                modifier = Modifier.weight(0.38f)
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ScannerCameraCard(
                uiState = uiState,
                isExpanded = false,
                onStartCameraClick = onStartCameraClick,
                onStopCameraClick = onStopCameraClick,
                onPickImageClick = onPickImageClick,
                onToggleTorch = onToggleTorch,
                onQrDetected = onQrDetected,
                onCameraError = onCameraError,
                modifier = Modifier.fillMaxWidth()
            )
            ScanResultCard(
                uiState = uiState,
                onCopyResultClick = onCopyResultClick,
                onOpenActionUriClick = onOpenActionUriClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ScannerCameraCard(
    uiState: EliasQrUiState,
    isExpanded: Boolean,
    onStartCameraClick: () -> Unit,
    onStopCameraClick: () -> Unit,
    onPickImageClick: () -> Unit,
    onToggleTorch: () -> Unit,
    onQrDetected: (String) -> Unit,
    onCameraError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.section_scan_qr),
                    style = MaterialTheme.typography.titleLarge,
                    color = Slate700
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Camera Button
                    Button(
                        onClick = onStartCameraClick,
                        enabled = !uiState.isScanningCamera,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (uiState.isScanningCamera) {
                                        listOf(Violet600.copy(alpha = 0.45f), Pink500.copy(alpha = 0.45f))
                                    } else {
                                        listOf(Violet600, Pink500)
                                    }
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .testTag("btn_cam_scan")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_camera),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }

                    // Image Upload Button
                    Surface(
                        onClick = onPickImageClick,
                        shape = RoundedCornerShape(10.dp),
                        color = Slate100,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_image_scan")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = Slate700,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.btn_image),
                                style = MaterialTheme.typography.labelLarge,
                                color = Slate700
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scanner Viewfinder Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (isExpanded) 16f / 10f else 1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .testTag("reader_container"),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isScanningCamera) {
                    CameraScannerView(
                        isTorchOn = uiState.isTorchOn,
                        onToggleTorch = onToggleTorch,
                        onQrDetected = onQrDetected,
                        onCameraError = onCameraError,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Overlay when camera is inactive (#scanner-overlay)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Slate900.copy(alpha = 0.9f))
                            .padding(24.dp)
                            .testTag("scanner_overlay"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.75f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.scanner_inactive_title),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.scanner_inactive_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate400,
                            textAlign = TextAlign.Center
                        )

                        // Quick convenience button if the user already generated a QR code in this session
                        if (!uiState.generatedQrData.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            OutlinedButton(
                                onClick = { onQrDetected(uiState.generatedQrData) },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, Violet500.copy(alpha = 0.7f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.testTag("btn_scan_generated_qr")
                            ) {
                                Text(
                                    text = "Scan Last Generated QR",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.isScanningCamera) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onStopCameraClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red500,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_stop_scan")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.btn_stop_camera),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanResultCard(
    uiState: EliasQrUiState,
    onCopyResultClick: (String) -> Unit,
    onOpenActionUriClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scanText = uiState.lastScanResult
    val parsed = uiState.parsedScanResult

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.section_scan_result),
                style = MaterialTheme.typography.titleLarge,
                color = Slate700
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 210.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Slate50)
                    .border(1.dp, Slate100, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                if (scanText.isNullOrBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .testTag("scan_empty"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = null,
                            tint = Slate400.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.scan_awaiting),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate400
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scan_result_container")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Pink500,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(R.string.scan_success),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Pink500
                                )
                            }

                            if (parsed != null) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Violet50
                                ) {
                                    Text(
                                        text = parsed.typeLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Violet600,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Raw Scanned Text Box
                        SelectionContainer {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = scanText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate700,
                                    modifier = Modifier.testTag("scan_result_text")
                                )
                            }
                        }

                        // Parsed Metadata (e.g., WiFi SSID/Password, Email, SMS)
                        if (parsed != null && parsed.metadataMap.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                parsed.metadataMap.forEach { (key, value) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$key:",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Slate500
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = value,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Slate800,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (key == "Password") {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copy password",
                                                    tint = Violet600,
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable { onCopyResultClick(value) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Copy Button
                            Button(
                                onClick = { onCopyResultClick(scanText) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Slate200.copy(alpha = 0.7f),
                                    contentColor = Slate700
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_copy_scan")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.btn_copy),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            // Open Link / Action Button (#btn-open-link)
                            val actionUri = parsed?.actionUri
                            if (!actionUri.isNullOrBlank()) {
                                Button(
                                    onClick = { onOpenActionUriClick(actionUri) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Violet600,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("btn_open_link")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = parsed.actionButtonText ?: stringResource(R.string.btn_open),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentHistoryCard(
    items: List<QrHistoryItem>,
    onSelectItem: (QrHistoryItem) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Violet600,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.section_recent_history),
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate700
                    )
                }

                TextButton(
                    onClick = onClearHistory,
                    modifier = Modifier.testTag("btn_clear_history")
                ) {
                    Text(
                        text = stringResource(R.string.btn_clear_history),
                        style = MaterialTheme.typography.labelLarge,
                        color = Slate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.take(5).forEach { item ->
                    Surface(
                        onClick = { onSelectItem(item) },
                        shape = RoundedCornerShape(12.dp),
                        color = Slate50,
                        border = BorderStroke(1.dp, Slate100),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("history_item_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (item.isScanned) Pink50 else Violet50,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (item.isScanned) "S" else "G",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isScanned) Pink600 else Violet600
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.rawValue,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate800,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${if (item.isScanned) "Scanned" else "Generated"} • ${item.typeLabel}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Slate500
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
