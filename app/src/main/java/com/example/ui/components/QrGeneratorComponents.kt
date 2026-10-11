package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.QrColorTheme
import com.example.model.QrType
import com.example.model.WifiSecurity
import com.example.ui.EliasQrUiState
import com.example.ui.theme.Pink500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
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
fun GeneratorSection(
    uiState: EliasQrUiState,
    isExpanded: Boolean,
    onSelectType: (QrType) -> Unit,
    onLinkChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onEmailToChange: (String) -> Unit,
    onEmailSubjectChange: (String) -> Unit,
    onEmailMessageChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSmsPhoneChange: (String) -> Unit,
    onSmsMessageChange: (String) -> Unit,
    onWifiSsidChange: (String) -> Unit,
    onWifiPasswordChange: (String) -> Unit,
    onWifiSecurityChange: (WifiSecurity) -> Unit,
    onWifiHiddenChange: (Boolean) -> Unit,
    onSelectColorTheme: (QrColorTheme) -> Unit,
    onGenerateClick: () -> Unit,
    onSharePngClick: () -> Unit,
    onSaveGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isExpanded) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            EnterDetailsCard(
                uiState = uiState,
                onSelectType = onSelectType,
                onLinkChange = onLinkChange,
                onTextChange = onTextChange,
                onEmailToChange = onEmailToChange,
                onEmailSubjectChange = onEmailSubjectChange,
                onEmailMessageChange = onEmailMessageChange,
                onPhoneChange = onPhoneChange,
                onSmsPhoneChange = onSmsPhoneChange,
                onSmsMessageChange = onSmsMessageChange,
                onWifiSsidChange = onWifiSsidChange,
                onWifiPasswordChange = onWifiPasswordChange,
                onWifiSecurityChange = onWifiSecurityChange,
                onWifiHiddenChange = onWifiHiddenChange,
                onSelectColorTheme = onSelectColorTheme,
                onGenerateClick = onGenerateClick,
                modifier = Modifier.weight(0.62f)
            )
            QrResultCard(
                generatedQrBitmap = uiState.generatedQrBitmap,
                brandedCardBitmap = uiState.brandedCardBitmap,
                showBrandedCardInPreview = uiState.showBrandedCardInPreview,
                generatedQrData = uiState.generatedQrData,
                onSharePngClick = onSharePngClick,
                onSaveGalleryClick = onSaveGalleryClick,
                modifier = Modifier.weight(0.38f)
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            EnterDetailsCard(
                uiState = uiState,
                onSelectType = onSelectType,
                onLinkChange = onLinkChange,
                onTextChange = onTextChange,
                onEmailToChange = onEmailToChange,
                onEmailSubjectChange = onEmailSubjectChange,
                onEmailMessageChange = onEmailMessageChange,
                onPhoneChange = onPhoneChange,
                onSmsPhoneChange = onSmsPhoneChange,
                onSmsMessageChange = onSmsMessageChange,
                onWifiSsidChange = onWifiSsidChange,
                onWifiPasswordChange = onWifiPasswordChange,
                onWifiSecurityChange = onWifiSecurityChange,
                onWifiHiddenChange = onWifiHiddenChange,
                onSelectColorTheme = onSelectColorTheme,
                onGenerateClick = onGenerateClick,
                modifier = Modifier.fillMaxWidth()
            )
            QrResultCard(
                generatedQrBitmap = uiState.generatedQrBitmap,
                brandedCardBitmap = uiState.brandedCardBitmap,
                showBrandedCardInPreview = uiState.showBrandedCardInPreview,
                generatedQrData = uiState.generatedQrData,
                onSharePngClick = onSharePngClick,
                onSaveGalleryClick = onSaveGalleryClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EnterDetailsCard(
    uiState: EliasQrUiState,
    onSelectType: (QrType) -> Unit,
    onLinkChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onEmailToChange: (String) -> Unit,
    onEmailSubjectChange: (String) -> Unit,
    onEmailMessageChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSmsPhoneChange: (String) -> Unit,
    onSmsMessageChange: (String) -> Unit,
    onWifiSsidChange: (String) -> Unit,
    onWifiPasswordChange: (String) -> Unit,
    onWifiSecurityChange: (WifiSecurity) -> Unit,
    onWifiHiddenChange: (Boolean) -> Unit,
    onSelectColorTheme: (QrColorTheme) -> Unit,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
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
                text = stringResource(R.string.section_enter_details),
                style = MaterialTheme.typography.titleLarge,
                color = Slate700
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.label_qr_code_type),
                style = MaterialTheme.typography.labelLarge,
                color = Slate600
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 6 QR Code Types in two clean rows of 3
            val types = QrType.entries
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.take(3).forEach { qrType ->
                        QrTypeSelectorButton(
                            qrType = qrType,
                            isSelected = uiState.currentType == qrType,
                            onClick = { onSelectType(qrType) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.drop(3).forEach { qrType ->
                        QrTypeSelectorButton(
                            qrType = qrType,
                            isSelected = uiState.currentType == qrType,
                            onClick = { onSelectType(qrType) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Input Form
            AnimatedContent(
                targetState = uiState.currentType,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "qr_type_inputs"
            ) { selectedType ->
                when (selectedType) {
                    QrType.LINK -> LinkInputGroup(
                        value = uiState.linkInput,
                        onValueChange = onLinkChange
                    )

                    QrType.TEXT -> TextInputGroup(
                        value = uiState.textInput,
                        onValueChange = onTextChange
                    )

                    QrType.EMAIL -> EmailInputGroup(
                        emailTo = uiState.emailTo,
                        emailSubject = uiState.emailSubject,
                        emailMessage = uiState.emailMessage,
                        onEmailToChange = onEmailToChange,
                        onEmailSubjectChange = onEmailSubjectChange,
                        onEmailMessageChange = onEmailMessageChange
                    )

                    QrType.PHONE -> PhoneInputGroup(
                        value = uiState.phoneInput,
                        onValueChange = onPhoneChange
                    )

                    QrType.SMS -> SmsInputGroup(
                        smsPhone = uiState.smsPhone,
                        smsMessage = uiState.smsMessage,
                        onSmsPhoneChange = onSmsPhoneChange,
                        onSmsMessageChange = onSmsMessageChange
                    )

                    QrType.WIFI -> WifiInputGroup(
                        ssid = uiState.wifiSsid,
                        password = uiState.wifiPassword,
                        security = uiState.wifiSecurity,
                        isHidden = uiState.wifiHidden,
                        onSsidChange = onWifiSsidChange,
                        onPasswordChange = onWifiPasswordChange,
                        onSecurityChange = onWifiSecurityChange,
                        onHiddenChange = onWifiHiddenChange
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // QR Color Style Picker
            Text(
                text = stringResource(R.string.label_qr_style),
                style = MaterialTheme.typography.labelLarge,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QrColorTheme.entries.forEach { theme ->
                    val isSelected = uiState.selectedColorTheme == theme
                    Surface(
                        onClick = { onSelectColorTheme(theme) },
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) Violet50 else Slate50,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) Violet500 else Slate200
                        ),
                        modifier = Modifier.testTag("color_theme_${theme.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(theme.darkColorArgb))
                            )
                            Text(
                                text = theme.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Violet600 else Slate600
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Violet600,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Gradient Generate Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onGenerateClick()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Violet600, Pink500)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .testTag("btn_generate_qr")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.btn_generate_qr),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun QrTypeSelectorButton(
    qrType: QrType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (qrType) {
        QrType.LINK -> Icons.Default.Link
        QrType.TEXT -> Icons.Default.TextFields
        QrType.EMAIL -> Icons.Default.Email
        QrType.PHONE -> Icons.Default.Phone
        QrType.SMS -> Icons.Default.Sms
        QrType.WIFI -> Icons.Default.Wifi
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Violet50 else Slate100,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) Violet500 else Color.Transparent
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier
            .height(64.dp)
            .testTag("btn_type_${qrType.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = qrType.label,
                tint = if (isSelected) Violet600 else Slate600,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = qrType.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Violet600 else Slate600
            )
        }
    }
}

@Composable
private fun EliasTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    testTag: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Slate600
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Slate400,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            minLines = minLines,
            maxLines = maxLines,
            singleLine = maxLines == 1,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Slate500
                        )
                    }
                }
            } else null,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Violet500,
                unfocusedBorderColor = Slate300,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Slate800,
                unfocusedTextColor = Slate800
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
private fun LinkInputGroup(
    value: String,
    onValueChange: (String) -> Unit
) {
    EliasTextField(
        label = stringResource(R.string.label_website_url),
        value = value,
        onValueChange = onValueChange,
        placeholder = stringResource(R.string.hint_website_url),
        keyboardType = KeyboardType.Uri,
        testTag = "val_link"
    )
}

@Composable
private fun TextInputGroup(
    value: String,
    onValueChange: (String) -> Unit
) {
    EliasTextField(
        label = stringResource(R.string.label_your_text),
        value = value,
        onValueChange = onValueChange,
        placeholder = stringResource(R.string.hint_your_text),
        minLines = 3,
        maxLines = 5,
        testTag = "val_text"
    )
}

@Composable
private fun EmailInputGroup(
    emailTo: String,
    emailSubject: String,
    emailMessage: String,
    onEmailToChange: (String) -> Unit,
    onEmailSubjectChange: (String) -> Unit,
    onEmailMessageChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EliasTextField(
            label = stringResource(R.string.label_email_address),
            value = emailTo,
            onValueChange = onEmailToChange,
            placeholder = stringResource(R.string.hint_email_address),
            keyboardType = KeyboardType.Email,
            testTag = "val_email_to"
        )
        EliasTextField(
            label = stringResource(R.string.label_email_subject),
            value = emailSubject,
            onValueChange = onEmailSubjectChange,
            placeholder = stringResource(R.string.hint_email_subject),
            testTag = "val_email_sub"
        )
        EliasTextField(
            label = stringResource(R.string.label_message),
            value = emailMessage,
            onValueChange = onEmailMessageChange,
            placeholder = stringResource(R.string.hint_email_message),
            minLines = 2,
            maxLines = 4,
            testTag = "val_email_msg"
        )
    }
}

@Composable
private fun PhoneInputGroup(
    value: String,
    onValueChange: (String) -> Unit
) {
    EliasTextField(
        label = stringResource(R.string.label_phone_number),
        value = value,
        onValueChange = onValueChange,
        placeholder = stringResource(R.string.hint_phone_number),
        keyboardType = KeyboardType.Phone,
        testTag = "val_phone"
    )
}

@Composable
private fun SmsInputGroup(
    smsPhone: String,
    smsMessage: String,
    onSmsPhoneChange: (String) -> Unit,
    onSmsMessageChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EliasTextField(
            label = stringResource(R.string.label_phone_number),
            value = smsPhone,
            onValueChange = onSmsPhoneChange,
            placeholder = stringResource(R.string.hint_phone_number),
            keyboardType = KeyboardType.Phone,
            testTag = "val_sms_to"
        )
        EliasTextField(
            label = stringResource(R.string.label_message),
            value = smsMessage,
            onValueChange = onSmsMessageChange,
            placeholder = stringResource(R.string.hint_sms_message),
            minLines = 2,
            maxLines = 4,
            testTag = "val_sms_msg"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WifiInputGroup(
    ssid: String,
    password: String,
    security: WifiSecurity,
    isHidden: Boolean,
    onSsidChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSecurityChange: (WifiSecurity) -> Unit,
    onHiddenChange: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EliasTextField(
            label = stringResource(R.string.label_wifi_ssid),
            value = ssid,
            onValueChange = onSsidChange,
            placeholder = stringResource(R.string.hint_wifi_ssid),
            testTag = "val_wifi_ssid"
        )
        EliasTextField(
            label = stringResource(R.string.label_wifi_password),
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.hint_wifi_password),
            isPassword = true,
            testTag = "val_wifi_pass"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_wifi_security),
                    style = MaterialTheme.typography.labelLarge,
                    color = Slate600
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = security.label,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Violet500,
                            unfocusedBorderColor = Slate300,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .testTag("val_wifi_type")
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        WifiSecurity.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    onSecurityChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onHiddenChange(!isHidden) }
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .testTag("val_wifi_hidden")
            ) {
                Checkbox(
                    checked = isHidden,
                    onCheckedChange = onHiddenChange,
                    colors = CheckboxDefaults.colors(checkedColor = Violet600)
                )
                Text(
                    text = stringResource(R.string.label_wifi_hidden),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Slate600
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QrResultCard(
    generatedQrBitmap: Bitmap?,
    brandedCardBitmap: Bitmap?,
    showBrandedCardInPreview: Boolean,
    generatedQrData: String?,
    onSharePngClick: () -> Unit,
    onSaveGalleryClick: () -> Unit,
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.section_result),
                style = MaterialTheme.typography.titleLarge,
                color = Slate700,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (generatedQrBitmap != null && !generatedQrData.isNullOrBlank()) {
                val activeBitmap = if (showBrandedCardInPreview && brandedCardBitmap != null) {
                    brandedCardBitmap
                } else {
                    generatedQrBitmap
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate50)
                        .border(1.dp, Slate100, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                        .testTag("qrcode_container"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .combinedClickable(
                                onClick = onSharePngClick,
                                onLongClick = onSaveGalleryClick
                            )
                    ) {
                        Image(
                            bitmap = activeBitmap.asImageBitmap(),
                            contentDescription = "Generated QR Code",
                            modifier = Modifier
                                .size(if (showBrandedCardInPreview) 240.dp else 210.dp)
                                .padding(8.dp)
                                .testTag("qrcode_image")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = generatedQrData,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = Slate500,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("qr_data_preview")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Save / Share PNG button (matches #btn-download in web app)
                Button(
                    onClick = onSharePngClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Slate800, Slate900)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("btn_download")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_save_share_png),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Direct Save to Gallery button
                OutlinedButton(
                    onClick = onSaveGalleryClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate300),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_gallery")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Slate700,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.btn_save_gallery),
                        style = MaterialTheme.typography.labelLarge,
                        color = Slate700
                    )
                }
            } else {
                // Empty state with hero artwork & QR icon
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .testTag("empty_state"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_qr_hero_banner_1791735348845),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Slate900.copy(alpha = 0.55f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.92f),
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode2,
                                        contentDescription = null,
                                        tint = Violet600,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stringResource(R.string.empty_qr_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
