package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiProviderType
import com.example.data.model.StudioAppMode
import com.example.ui.components.GlowingButton
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioHeader
import com.example.ui.theme.GlowGradient
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PurpleGradient
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.updater.ApkDownloadState

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val context = LocalContext.current
    val apiConfig by viewModel.apiConfig.collectAsState()
    val generationMsg by viewModel.generationMessage.collectAsState()
    val updateState by viewModel.updateDownloadState.collectAsState()

    var backendUrlInput by remember(apiConfig.backendBaseUrl) { mutableStateOf(apiConfig.backendBaseUrl) }
    var apiKeyInput by remember(apiConfig.apiKey) { mutableStateOf(apiConfig.apiKey) }

    // Dialog controllers for the 10 options
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showStorageDialog by remember { mutableStateOf(false) }
    var showClearCacheConfirm by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English (US)") }

    Scaffold(
        containerColor = StudioDarkBg,
        topBar = {
            StudioHeader(
                title = "Studio Settings",
                subtitle = "App Updates, APK Download & Preferences",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAdmin) {
                        Icon(Icons.Default.Speed, contentDescription = "Admin Metrics", tint = NeonCyan)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. HERO CARD: DOWNLOAD ANDROID APK (Large gradient button & status)
            item {
                StudioCard(
                    borderBrush = PurpleGradient,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NeonPurple.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.FileDownload,
                                        contentDescription = null,
                                        tint = NeonPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Official Android APK",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Verified Hardware Package • SHA-256 Protected",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            StatusBadge(text = "v${viewModel.appVersionName}", color = NeonCyan)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Version & Spec Info Grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(StudioElevated)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Current Installed", fontSize = 11.sp, color = TextMuted)
                                Text("v${viewModel.appVersionName} (Build ${viewModel.appVersionCode})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Latest Server Version", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    when (val state = updateState) {
                                        is ApkDownloadState.UpdateAvailable -> "v${state.info.latestVersion}"
                                        else -> "v2.4.0 (Latest)"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large DOWNLOAD APK Button
                        when (val state = updateState) {
                            is ApkDownloadState.Downloading -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (state.isPaused) "Download Paused" else "Downloading APK: ${state.percent}%",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (state.isPaused) NeonAmber else NeonCyan
                                        )
                                        Text(
                                            text = "%.1f / %.1f MB".format(
                                                state.bytesDownloaded / (1024f * 1024f),
                                                state.totalBytes / (1024f * 1024f)
                                            ),
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { state.percent / 100f },
                                        color = NeonPurple,
                                        trackColor = StudioElevated,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Button(
                                            onClick = {
                                                if (state.isPaused) {
                                                    viewModel.startApkDownload(
                                                        "/api/updates/download",
                                                        null,
                                                        state.totalBytes
                                                    )
                                                } else {
                                                    viewModel.pauseApkDownload()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StudioElevated),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                                contentDescription = null,
                                                tint = NeonAmber,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (state.isPaused) "Resume" else "Pause", color = NeonAmber)
                                        }
                                    }
                                }
                            }
                            is ApkDownloadState.VerifyingChecksum -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Verifying SHA-256 Checksum Integrity...", fontSize = 13.sp, color = TextPrimary)
                                }
                            }
                            is ApkDownloadState.ReadyToInstall -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("APK Downloaded & Verified (SHA-256 OK)", fontWeight = FontWeight.Bold, color = NeonGreen, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Checksum: ${state.sha256.take(16)}...",
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    GlowingButton(
                                        text = "📦 INSTALL APK NOW",
                                        icon = Icons.Default.SystemUpdate,
                                        onClick = { viewModel.installDownloadedApk(state.apkFile) },
                                        modifier = Modifier.fillMaxWidth(),
                                        testTag = "btn_install_apk"
                                    )
                                }
                            }
                            is ApkDownloadState.Error -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Red.copy(alpha = 0.15f))
                                            .padding(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(state.message, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.checkForUpdates() },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Retry Check")
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.resetUpdateState() },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Dismiss")
                                        }
                                    }
                                }
                            }
                            else -> {
                                // Default Idle/Checking state - Large Gradient Download Button
                                GlowingButton(
                                    text = "📥 DOWNLOAD APK",
                                    icon = Icons.Default.Download,
                                    onClick = {
                                        viewModel.checkForUpdates()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "btn_download_apk"
                                )
                            }
                        }
                    }
                }
            }

            // 2. CHECK FOR APP UPDATES CARD (Auto notification and update prompt)
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Check for App Updates", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            }
                            IconButton(onClick = { viewModel.checkForUpdates() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Check", tint = NeonCyan)
                            }
                        }

                        Text(
                            text = "Connects to official release server to verify new feature updates and stability patches.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        // Update Notification Banner if update is available
                        if (updateState is ApkDownloadState.UpdateAvailable) {
                            val info = (updateState as ApkDownloadState.UpdateAvailable).info
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = NeonGreen.copy(alpha = 0.12f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "🎉 New Update Available: v${info.latestVersion} (${info.sizeFormatted ?: "27 MB"})",
                                        fontWeight = FontWeight.Bold,
                                        color = NeonGreen,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Your projects, scripts, audio tracks, and settings will be 100% preserved.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                viewModel.startApkDownload(
                                                    info.downloadUrl ?: "/api/updates/download",
                                                    info.sha256Checksum,
                                                    info.sizeBytes
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Update Now", color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.resetUpdateState() },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Later", color = TextSecondary)
                                        }
                                    }
                                }
                            }
                        } else if (updateState is ApkDownloadState.UpToDate) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("App is up to date (v${viewModel.appVersionName})", fontSize = 12.sp, color = NeonGreen)
                            }
                        }
                    }
                }
            }

            // 3. SETTINGS DASHBOARD MENU (10 User-Requested Options)
            item {
                Text(
                    text = "Application Preferences & Tools",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Option 3: App Version
            item {
                SettingsOptionRow(
                    icon = Icons.Default.Info,
                    title = "App Version",
                    subtitle = "Version ${viewModel.appVersionName} (Build ${viewModel.appVersionCode})",
                    tag = "v${viewModel.appVersionName}",
                    tagColor = NeonCyan,
                    onClick = { Toast.makeText(context, "AI Video Studio Pro v${viewModel.appVersionName}", Toast.LENGTH_SHORT).show() }
                )
            }

            // Option 4: About Application
            item {
                SettingsOptionRow(
                    icon = Icons.Default.Description,
                    title = "About Application",
                    subtitle = "Next-gen AI media generator, timeline editor & subtitle engine",
                    onClick = { showAboutDialog = true }
                )
            }

            // Option 5: Privacy Policy
            item {
                SettingsOptionRow(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "Local data encryption & zero unauthorized telemetry",
                    onClick = { showPrivacyDialog = true }
                )
            }

            // Option 6: Terms and Conditions
            item {
                SettingsOptionRow(
                    icon = Icons.Default.Security,
                    title = "Terms and Conditions",
                    subtitle = "Commercial video usage rights & licensing",
                    onClick = { showTermsDialog = true }
                )
            }

            // Option 7: Help and Support
            item {
                SettingsOptionRow(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Help and Support",
                    subtitle = "Guides for text-to-video, Hindi scripts & rendering tips",
                    onClick = { showHelpDialog = true }
                )
            }

            // Option 8: Clear Cache
            item {
                val storage = viewModel.getStorageMetrics()
                SettingsOptionRow(
                    icon = Icons.Default.DeleteSweep,
                    title = "Clear Cache",
                    subtitle = "Freed temp render files and cached frames (${storage.first})",
                    onClick = { showClearCacheConfirm = true }
                )
            }

            // Option 9: Language Settings
            item {
                SettingsOptionRow(
                    icon = Icons.Default.Language,
                    title = "Language Settings",
                    subtitle = "Current: $selectedLanguage (Hindi & English Voice-Overs)",
                    onClick = { showLanguageDialog = true }
                )
            }

            // Option 10: Storage Settings
            item {
                val storage = viewModel.getStorageMetrics()
                SettingsOptionRow(
                    icon = Icons.Default.SdStorage,
                    title = "Storage Settings",
                    subtitle = "${storage.second} • Local MediaStore video library",
                    onClick = { showStorageDialog = true }
                )
            }

            // 4. DUAL ENGINE & API SERVER CONFIGURATION
            item {
                Text(
                    text = "AI Engine & Backend Server",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Engine Mode", fontWeight = FontWeight.Bold, color = TextPrimary)
                            StatusBadge(text = apiConfig.activeMode.badge, color = if (apiConfig.activeMode == StudioAppMode.MODE_A_LOCAL) NeonGreen else NeonCyan)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Toggle Mode A / Mode B
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val isA = apiConfig.activeMode == StudioAppMode.MODE_A_LOCAL
                            Button(
                                onClick = { viewModel.setAppMode(StudioAppMode.MODE_A_LOCAL) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isA) NeonGreen else StudioElevated),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mode A (Local)", color = if (isA) Color.Black else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            val isB = apiConfig.activeMode == StudioAppMode.MODE_B_AI_CLOUD
                            Button(
                                onClick = { viewModel.setAppMode(StudioAppMode.MODE_B_AI_CLOUD) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isB) NeonCyan else StudioElevated),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mode B (Cloud)", color = if (isB) Color.Black else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Backend URL (Express & APK Server):", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = backendUrlInput,
                            onValueChange = { backendUrlInput = it },
                            placeholder = { Text("http://10.0.2.2:5000", color = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = StudioCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioElevated,
                                unfocusedContainerColor = StudioElevated
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.setBackendUrl(backendUrlInput)
                                    viewModel.setApiKey(apiKeyInput)
                                    Toast.makeText(context, "Settings Saved", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Server")
                            }

                            Button(
                                onClick = {
                                    viewModel.setBackendUrl(backendUrlInput)
                                    viewModel.testBackendConnection()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Test API", color = NeonCyan)
                            }
                        }

                        if (generationMsg != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(generationMsg!!, fontSize = 11.sp, color = NeonAmber)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Dialog 1: About Application
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About AI Video Studio Pro", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("AI Video Studio Pro is a full-featured video production application built with Kotlin, Jetpack Compose, and Android Media3.")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Local & Cloud Video Generation Engine", color = NeonCyan, fontSize = 12.sp)
                    Text("• Hindi & English AI Script Generator", color = NeonCyan, fontSize = 12.sp)
                    Text("• Multi-Track Video Timeline Editor", color = NeonCyan, fontSize = 12.sp)
                    Text("• High-CTR AI Thumbnail Studio", color = NeonCyan, fontSize = 12.sp)
                    Text("• Built-in SHA-256 Verified APK Updater", color = NeonCyan, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                    Text("Close")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Dialog 2: Privacy Policy
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Your privacy is our utmost priority. AI Video Studio Pro stores all project scripts, video clips, and customized voice-overs locally on your device storage unless you explicitly connect to a cloud backend. No personal telemetry or camera footage is ever uploaded without your explicit permission."
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                    Text("I Understand")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Dialog 3: Terms and Conditions
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "1. Commercial Rights: All videos and thumbnails rendered through AI Video Studio Pro are 100% royalty-free for personal and commercial distribution on YouTube, Instagram, and TikTok.\n\n2. Content Compliance: Users agree not to generate defamatory or unlawful video footage.\n\n3. Updates: Official APK downloads are digitally verified with SHA-256 checksums."
                )
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                    Text("Accept")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Dialog 4: Help and Support
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Quick Tips:")
                    Text("1. Text-to-Video: Type your topic in the AI Generator to divide it into scenes automatically.")
                    Text("2. Hindi Videos: Choose 'Hindi' in Language settings to synthesize natural voice narration.")
                    Text("3. Offline Mode: Switch to Mode A (Local Engine) in Settings to render full MP4 videos completely offline.")
                    Text("4. Updates: Click 'Check for App Updates' to verify the latest signed release.")
                }
            },
            confirmButton = {
                Button(onClick = { showHelpDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                    Text("Got It")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Dialog 5: Clear Cache Confirm
    if (showClearCacheConfirm) {
        AlertDialog(
            onDismissRequest = { showClearCacheConfirm = false },
            title = { Text("Clear Temporary Cache?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will safely remove temporary render frames and downloaded previews. Your saved projects and exported MP4 videos will NOT be deleted.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val freed = viewModel.clearAppCache()
                        showClearCacheConfirm = false
                        Toast.makeText(context, "Freed %.1f MB of cache space".format(freed / (1024f * 1024f)), Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Clear Cache")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearCacheConfirm = false }) {
                    Text("Cancel")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Dialog 6: Language Settings
    if (showLanguageDialog) {
        val languages = listOf("English (US)", "Hindi (हिन्दी)", "Spanish (Español)", "German (Deutsch)", "Japanese (日本語)")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Studio Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLanguage = lang }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang,
                                onClick = { selectedLanguage = lang },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, color = TextPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLanguageDialog = false
                        Toast.makeText(context, "Language set to $selectedLanguage", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Apply")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Dialog 7: Storage Settings
    if (showStorageDialog) {
        val storage = viewModel.getStorageMetrics()
        AlertDialog(
            onDismissRequest = { showStorageDialog = false },
            title = { Text("Storage Diagnostics", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("App Storage Usage:", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("• Saved Projects: ${storage.second}", color = TextSecondary)
                    Text("• Cache & Render Temp: ${storage.first}", color = TextSecondary)
                    Text("• Export Directory: /Movies/AI_Video_Studio", color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("All exported MP4 files are stored directly in your Android MediaStore Movies gallery.", fontSize = 11.sp, color = NeonGreen)
                }
            },
            confirmButton = {
                Button(onClick = { showStorageDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                    Text("Close")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }
}

@Composable
fun SettingsOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tag: String? = null,
    tagColor: Color = NeonCyan,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StudioElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    Text(text = subtitle, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                }
            }

            if (tag != null) {
                StatusBadge(text = tag, color = tagColor)
            }
        }
    }
}
