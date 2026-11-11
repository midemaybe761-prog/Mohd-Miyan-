package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import com.example.data.model.CanvasRatio
import com.example.data.model.VideoFps
import com.example.data.model.VideoQuality
import com.example.data.model.VisualStyle
import com.example.engine.AiScriptAgent
import com.example.engine.GeneratedScriptResult
import com.example.engine.ScriptGenre
import com.example.ui.components.GlowingButton
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioHeader
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
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
fun ScriptWriterScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onTransferToStudio: (String, String) -> Unit,
    onVideoGenerated: (String) -> Unit = {}
) {
    var topic by remember { mutableStateOf("Top 5 Artificial Intelligence Innovations of 2026") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedGenre by remember { mutableStateOf(ScriptGenre.EDUCATIONAL) }
    var targetDurationSeconds by remember { mutableStateOf(60) }
    var generatedResult by remember { mutableStateOf<GeneratedScriptResult?>(null) }
    var isWriting by remember { mutableStateOf(false) }
    var editableScript by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        containerColor = StudioDarkBg,
        topBar = {
            StudioHeader(
                title = "AI Script Writer",
                subtitle = "Multilingual Viral Hooks & Scene Breakdown",
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

            // Topic prompt
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "What is your video about?",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            placeholder = { Text("e.g. 5 Morning habits of successful billionaires, or Indian street food guide...", color = TextMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("input_script_topic"),
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
                    }
                }
            }

            // Genre Selector
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "Content Category & Tone",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ScriptGenre.values().toList()) { genre ->
                                val isSel = selectedGenre == genre
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) NeonPurple else StudioElevated)
                                        .border(1.dp, if (isSel) NeonPurple else StudioCardBorder, RoundedCornerShape(10.dp))
                                        .clickable { selectedGenre = genre }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(genre.emoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = genre.label,
                                            color = if (isSel) Color.White else TextSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        // Language
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
                                        .background(if (isSel) NeonCyan else StudioElevated)
                                        .clickable { selectedLanguage = lang }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = lang,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Generate Button
            item {
                GlowingButton(
                    text = if (isWriting) "Generating Script..." else "Generate Scene-by-Scene Script",
                    icon = Icons.Default.AutoAwesome,
                    onClick = {
                        isWriting = true
                        val res = AiScriptAgent.generateScript(
                            topicPrompt = topic,
                            language = selectedLanguage,
                            genre = selectedGenre,
                            targetDurationSeconds = targetDurationSeconds,
                            visualStyle = VisualStyle.CINEMATIC,
                            projectId = "temp"
                        )
                        generatedResult = res
                        editableScript = res.fullScriptText
                        isWriting = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_generate_script"
                )
            }

            // Generated Output Area
            if (generatedResult != null) {
                item {
                    StudioCard(
                        borderBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(NeonPurple, NeonCyan)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatusBadge(text = "AI HOOK GENERATED", color = NeonAmber)
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(generatedResult!!.hook))
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy Hook",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "\"${generatedResult!!.hook}\"",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber
                            )
                        }
                    }
                }

                item {
                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Editable Script Breakdown (${generatedResult!!.scenes.size} Scenes)",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                                StatusBadge(text = "FULL SCRIPT", color = NeonGreen)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = editableScript,
                                onValueChange = { editableScript = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp),
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
                            Spacer(modifier = Modifier.height(14.dp))
                            GlowingButton(
                                text = "🎬 GENERATE AI VIDEO & WATCH",
                                icon = Icons.Default.AutoAwesome,
                                onClick = {
                                    viewModel.generateRealVideo(
                                        title = topic.take(30).ifBlank { "AI Video Project" },
                                        prompt = editableScript.ifBlank { topic },
                                        canvasRatio = CanvasRatio.VERTICAL_9_16,
                                        quality = VideoQuality.HD_720P,
                                        fps = VideoFps.FPS_30,
                                        visualStyle = VisualStyle.CINEMATIC,
                                        durationSeconds = targetDurationSeconds,
                                        language = selectedLanguage,
                                        genre = selectedGenre,
                                        voiceId = "hi_male_aarav",
                                        voiceSpeed = 1.0f,
                                        musicTrackId = "upbeat_cinematic",
                                        musicVolume = 0.35f,
                                        subtitlesEnabled = true,
                                        subtitlePosition = "Bottom",
                                        onStarted = { projId ->
                                            onVideoGenerated(projId)
                                        }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_script_generate_video"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    onTransferToStudio(topic, editableScript)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_transfer_script_to_studio"
                            ) {
                                Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open in Video Editing Studio Timeline")
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}
