package com.example.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.model.AppTab
import com.example.model.ToastMessage
import com.example.ui.components.GeneratorSection
import com.example.ui.components.RecentHistoryCard
import com.example.ui.components.ScannerSection
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Fuchsia50
import com.example.ui.theme.Pink50
import com.example.ui.theme.Pink500
import com.example.ui.theme.PlusJakartaFontFamily
import com.example.ui.theme.Red500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Violet100
import com.example.ui.theme.Violet50
import com.example.ui.theme.Violet500
import com.example.ui.theme.Violet600

@Composable
fun EliasQrAppScreen(
    qrViewModel: QrViewModel = viewModel()
) {
    val uiState by qrViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Navigate back from Scan tab to Generate tab on back press
    BackHandler(enabled = uiState.currentTab != AppTab.GENERATE) {
        qrViewModel.switchTab(AppTab.GENERATE)
    }

    // Runtime Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            qrViewModel.startCameraScanning()
        } else {
            qrViewModel.onCameraAccessError("Camera access denied or not available.")
        }
    }

    // Zero-permission Photo Picker for scanning QR from gallery images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            qrViewModel.scanImageFromUri(context, uri)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Violet50, Fuchsia50, Pink50)
                )
            )
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = Color.Transparent,
            topBar = {
                EliasHeaderBar()
            }
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val isExpanded = maxWidth >= 720.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 920.dp)
                    ) {
                        // Top Segmented Tab Bar (Generate QR / Scan QR)
                        EliasTabSwitcher(
                            currentTab = uiState.currentTab,
                            onSelectTab = { qrViewModel.switchTab(it) }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Main Active Section
                        AnimatedContent(
                            targetState = uiState.currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "main_tab_transition"
                        ) { activeTab ->
                            when (activeTab) {
                                AppTab.GENERATE -> {
                                    GeneratorSection(
                                        uiState = uiState,
                                        isExpanded = isExpanded,
                                        onSelectType = qrViewModel::setQrType,
                                        onLinkChange = qrViewModel::updateLinkInput,
                                        onTextChange = qrViewModel::updateTextInput,
                                        onEmailToChange = qrViewModel::updateEmailTo,
                                        onEmailSubjectChange = qrViewModel::updateEmailSubject,
                                        onEmailMessageChange = qrViewModel::updateEmailMessage,
                                        onPhoneChange = qrViewModel::updatePhoneInput,
                                        onSmsPhoneChange = qrViewModel::updateSmsPhone,
                                        onSmsMessageChange = qrViewModel::updateSmsMessage,
                                        onWifiSsidChange = qrViewModel::updateWifiSsid,
                                        onWifiPasswordChange = qrViewModel::updateWifiPassword,
                                        onWifiSecurityChange = qrViewModel::updateWifiSecurity,
                                        onWifiHiddenChange = qrViewModel::updateWifiHidden,
                                        onSelectColorTheme = qrViewModel::selectColorTheme,
                                        onGenerateClick = qrViewModel::generateQrCode,
                                        onSharePngClick = { qrViewModel.shareGeneratedQr(context) },
                                        onSaveGalleryClick = { qrViewModel.saveGeneratedQrToGallery(context) }
                                    )
                                }

                                AppTab.SCAN -> {
                                    ScannerSection(
                                        uiState = uiState,
                                        isExpanded = isExpanded,
                                        onStartCameraClick = {
                                            val hasPerm = ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.CAMERA
                                            ) == PackageManager.PERMISSION_GRANTED
                                            if (hasPerm) {
                                                qrViewModel.startCameraScanning()
                                            } else {
                                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                            }
                                        },
                                        onStopCameraClick = qrViewModel::stopCameraScanning,
                                        onPickImageClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        },
                                        onToggleTorch = qrViewModel::toggleTorch,
                                        onQrDetected = qrViewModel::handleScanSuccess,
                                        onCameraError = qrViewModel::onCameraAccessError,
                                        onCopyResultClick = { textToCopy ->
                                            copyToClipboard(context, textToCopy)
                                            qrViewModel.showToast("Result copied to clipboard!")
                                        },
                                        onOpenActionUriClick = { actionUri ->
                                            openUriSafely(
                                                context = context,
                                                uriString = actionUri,
                                                onError = {
                                                    qrViewModel.showToast(
                                                        "No app found to open this link.",
                                                        isError = true
                                                    )
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        // Recent QR History Section
                        if (uiState.historyItems.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            RecentHistoryCard(
                                items = uiState.historyItems,
                                onSelectItem = qrViewModel::selectHistoryItem,
                                onClearHistory = qrViewModel::clearHistory
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }

        // Custom Floating Toast Notification (#toast-container)
        AnimatedVisibility(
            visible = uiState.activeToast != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            uiState.activeToast?.let { toast ->
                EliasToastBanner(toast = toast)
            }
        }
    }
}

@Composable
private fun EliasHeaderBar() {
    Surface(
        color = Color.White.copy(alpha = 0.85f),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = Violet600,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.header_title),
                    style = TextStyle(
                        fontFamily = PlusJakartaFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Violet600, Pink500)
                        )
                    ),
                    modifier = Modifier.testTag("header_title")
                )
            }
            HorizontalDivider(color = Slate100, thickness = 1.dp)
        }
    }
}

@Composable
private fun EliasTabSwitcher(
    currentTab: AppTab,
    onSelectTab: (AppTab) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            EliasTabButton(
                title = stringResource(R.string.tab_generate),
                icon = Icons.Default.AutoAwesome,
                isSelected = currentTab == AppTab.GENERATE,
                onClick = { onSelectTab(AppTab.GENERATE) },
                testTag = "tab_generate",
                modifier = Modifier.weight(1f)
            )
            EliasTabButton(
                title = stringResource(R.string.tab_scan),
                icon = Icons.Default.CameraAlt,
                isSelected = currentTab == AppTab.SCAN,
                onClick = { onSelectTab(AppTab.SCAN) },
                testTag = "tab_scan",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EliasTabButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val bgBrush = if (isSelected) {
        Brush.verticalGradient(
            colors = listOf(Color.White, Violet100.copy(alpha = 0.75f))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Slate50.copy(alpha = 0.6f), Slate50.copy(alpha = 0.6f))
        )
    }

    Column(
        modifier = modifier
            .background(brush = bgBrush)
            .clickable(onClick = onClick)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Violet500 else Slate500,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Violet500 else Slate500
            )
        }
        // Bottom active border indicator (3dp solid #8B5CF6)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(if (isSelected) Violet500 else Color.Transparent)
        )
    }
}

@Composable
private fun EliasToastBanner(toast: ToastMessage) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (toast.isError) Red500 else Emerald500,
        shadowElevation = 6.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .testTag("toast_banner")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = if (toast.isError) Icons.Default.Error else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = toast.message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Elias QR Code", text)
    clipboard?.setPrimaryClip(clip)
}

private fun openUriSafely(
    context: Context,
    uriString: String,
    onError: () -> Unit
) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }.onFailure {
        onError()
    }
}
