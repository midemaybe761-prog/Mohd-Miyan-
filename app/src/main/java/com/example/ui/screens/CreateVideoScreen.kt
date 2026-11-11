package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CanvasRatio
import com.example.data.model.MusicCatalog
import com.example.data.model.VideoDuration
import com.example.data.model.VideoFps
import com.example.data.model.VideoQuality
import com.example.data.model.VisualStyle
import com.example.data.model.VoiceCatalog
import com.example.engine.ScriptGenre
import com.example.ui.components.GlowingButton
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioHeader
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CreateVideoScreen(
    viewModel: MainViewModel,
    initialPrompt: String? = null,
    onNavigateBack: () -> Unit,
    onVideoGenerated: (String) -> Unit
) {
    var prompt by remember {
        mutableStateOf(
            initialPrompt ?: "How artificial intelligence and quantum computing will change humanity in the next 10 years."
        )
    }
    var title by remember { mutableStateOf("Future of AI 2026") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedGenre by remember { mutableStateOf(ScriptGenre.EDUCATIONAL) }
    var selectedDuration by remember { mutableStateOf(VideoDuration.MIN_1) }
    var customSeconds by remember { mutableIntStateOf(45) }
    var selectedCanvas by remember { mutableStateOf(CanvasRatio.VERTICAL_9_16) }
    var selectedQuality by remember { mutableStateOf(VideoQuality.HD_720P) }
    var selectedFps by remember { mutableStateOf(VideoFps.FPS_30) }
    var selectedStyle by remember { mutableStateOf(VisualStyle.CINEMATIC) }
    var selectedVoiceId by remember { mutableStateOf("hi_male_aarav") }
    var voiceSpeed by remember { mutableFloatStateOf(1.0f) }
    var selectedMusicId by remember { mutableStateOf("upbeat_cinematic") }
    var musicVolume by remember { mutableFloatStateOf(0.35f) }
    var subtitlesEnabled by remember { mutableStateOf(true) }
    var subtitlePosition by remember { mutableStateOf("Bottom") }

    val isGenerating by viewModel.isGeneratingScript.collectAsState()
    val generationMsg by viewModel.generationMessage.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val currentVoicePlaying by viewModel.voiceEngine.currentPlayingVoiceId.collectAsState()

    Scaffold(
        containerColor = StudioDarkBg,
        topBar = {
            StudioHeader(
                title = "Create AI Video",
                subtitle = "Configure Canvas, Quality, Voice & Script",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
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

            // 1. Topic & Prompt Input
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Video Idea or Script Prompt",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            StatusBadge(text = "STEP 1", color = NeonCyan)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = prompt,
                            onValueChange = { prompt = it },
                            placeholder = { Text("Describe the video topic, story, or paste a script in Hindi or English...", color = TextMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("input_video_prompt"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = StudioCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioElevated,
                                unfocusedContainerColor = StudioElevated
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // Language Selection
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Language:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            listOf("English", "Hindi (हिंदी)", "Bilingual Mix").forEach { lang ->
                                val isSel = selectedLanguage == lang
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) NeonPurple else StudioElevated)
                                        .clickable { selectedLanguage = lang }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = lang,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Video Length Settings
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Video Duration",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            StatusBadge(text = "${selectedDuration.label}", color = NeonAmber)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val durations = listOf(
                            VideoDuration.SEC_30,
                            VideoDuration.MIN_1,
                            VideoDuration.MIN_2,
                            VideoDuration.MIN_3,
                            VideoDuration.MIN_5,
                            VideoDuration.MIN_8,
                            VideoDuration.MIN_10,
                            VideoDuration.CUSTOM
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(durations) { dur ->
                                val isSel = selectedDuration == dur
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) NeonAmber else StudioElevated)
                                        .border(1.dp, if (isSel) NeonAmber else StudioCardBorder, RoundedCornerShape(10.dp))
                                        .clickable { selectedDuration = dur }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = dur.label,
                                        color = if (isSel) Color.Black else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        if (selectedDuration == VideoDuration.CUSTOM) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Custom Seconds: $customSeconds seconds", fontSize = 12.sp, color = NeonAmber)
                            Slider(
                                value = customSeconds.toFloat(),
                                onValueChange = { customSeconds = it.toInt() },
                                valueRange = 15f..600f,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonAmber,
                                    activeTrackColor = NeonAmber
                                )
                            )
                        }
                    }
                }
            }

            // 3. Canvas Settings (Aspect Ratio)
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "Canvas Aspect Ratio",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val ratios = listOf(
                            CanvasRatio.VERTICAL_9_16,
                            CanvasRatio.LANDSCAPE_16_9,
                            CanvasRatio.SQUARE_1_1,
                            CanvasRatio.PORTRAIT_4_5
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ratios.forEach { ratio ->
                                val isSel = selectedCanvas == ratio
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSel) NeonPurple.copy(alpha = 0.2f) else StudioElevated)
                                        .border(1.dp, if (isSel) NeonPurple else StudioCardBorder, RoundedCornerShape(12.dp))
                                        .clickable { selectedCanvas = ratio }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = ratio.label.split(" ").first(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSel) NeonPurple else TextPrimary
                                        )
                                        Text(
                                            text = when (ratio) {
                                                CanvasRatio.VERTICAL_9_16 -> "Shorts/Reels"
                                                CanvasRatio.LANDSCAPE_16_9 -> "YouTube Long"
                                                CanvasRatio.SQUARE_1_1 -> "Feed Post"
                                                else -> "Social"
                                            },
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Quality & FPS
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Quality & Frame Rate",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${selectedQuality.label} • ${selectedFps.label}",
                                fontSize = 11.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // Qualities
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(VideoQuality.values().toList()) { qual ->
                                val isSel = selectedQuality == qual
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) NeonCyan else StudioElevated)
                                        .clickable { selectedQuality = qual }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = qual.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else TextSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // FPS
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            VideoFps.values().forEach { fps ->
                                val isSel = selectedFps == fps
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) NeonPurple else StudioElevated)
                                        .clickable { selectedFps = fps }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${fps.fps} FPS",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Visual Style
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "Visual Art Style",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(VisualStyle.values().toList()) { style ->
                                val isSel = selectedStyle == style
                                Card(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .clickable { selectedStyle = style },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (isSel) NeonPurple.copy(alpha = 0.2f) else StudioElevated),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonPurple else StudioCardBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(style.emoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = style.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSel) NeonPurple else TextPrimary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Voice & Narration with Preview
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "AI Voice & Narration",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            StatusBadge(text = "HINDI & ENGLISH", color = NeonPink)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        VoiceCatalog.AVAILABLE_VOICES.forEach { voice ->
                            val isSel = selectedVoiceId == voice.id
                            val isThisPlaying = isSpeaking && currentVoicePlaying == voice.id

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) StudioElevated else Color.Transparent)
                                    .border(1.dp, if (isSel) NeonPurple else StudioCardBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedVoiceId = voice.id }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = voice.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSel) NeonPurple else TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        StatusBadge(text = voice.gender, color = if (voice.gender == "Female") NeonPink else NeonCyan)
                                    }
                                    Text(
                                        text = voice.style,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (isThisPlaying) {
                                            viewModel.voiceEngine.stop()
                                        } else {
                                            viewModel.voiceEngine.previewVoice(voice, voiceSpeed)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isThisPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                        contentDescription = "Preview Voice",
                                        tint = if (isThisPlaying) NeonPink else NeonCyan
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Speaking Speed: ${"%.1f".format(voiceSpeed)}x", fontSize = 12.sp, color = TextSecondary)
                        }
                        Slider(
                            value = voiceSpeed,
                            onValueChange = { voiceSpeed = it },
                            valueRange = 0.75f..1.5f,
                            steps = 3,
                            colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                        )
                    }
                }
            }

            // 7. Background Music & Subtitles
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "Audio Mix & Subtitles",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(MusicCatalog.TRACKS) { track ->
                                val isSel = selectedMusicId == track.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) NeonCyan else StudioElevated)
                                        .clickable { selectedMusicId = track.id }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = track.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Burn Subtitles into Video", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text("Hindi / English automatic caption sync", fontSize = 11.sp, color = TextMuted)
                            }
                            Switch(
                                checked = subtitlesEnabled,
                                onCheckedChange = { subtitlesEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NeonPurple
                                )
                            )
                        }
                    }
                }
            }

            // 8. Generate CTA Button
            item {
                if (generationMsg != null) {
                    Text(
                        text = generationMsg!!,
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isGenerating) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = NeonPurple, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("AI Agent constructing multi-scene project...", color = TextPrimary, fontSize = 14.sp)
                    }
                } else {
                    GlowingButton(
                        text = "🎬 GENERATE AI VIDEO & WATCH",
                        icon = Icons.Default.AutoAwesome,
                        onClick = {
                            val durationSec = if (selectedDuration == VideoDuration.CUSTOM) customSeconds else selectedDuration.seconds
                            viewModel.generateRealVideo(
                                title = title.ifBlank { prompt.take(30).ifBlank { "AI Video Project" } },
                                prompt = prompt,
                                canvasRatio = selectedCanvas,
                                quality = selectedQuality,
                                fps = selectedFps,
                                visualStyle = selectedStyle,
                                durationSeconds = durationSec,
                                language = selectedLanguage,
                                genre = selectedGenre,
                                voiceId = selectedVoiceId,
                                voiceSpeed = voiceSpeed,
                                musicTrackId = selectedMusicId,
                                musicVolume = musicVolume,
                                subtitlesEnabled = subtitlesEnabled,
                                subtitlePosition = subtitlePosition,
                                onStarted = { projectId ->
                                    onVideoGenerated(projectId)
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_generate_real_video"
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
