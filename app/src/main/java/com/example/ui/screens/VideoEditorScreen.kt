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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CanvasRatio
import com.example.data.model.Scene
import com.example.data.model.TransitionType
import com.example.ui.components.GlowingButton
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioHeader
import com.example.ui.components.VideoEditingInterface
import com.example.ui.theme.DarkSurfaceGradient
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
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
fun VideoEditorScreen(
    viewModel: MainViewModel,
    projectId: String,
    onNavigateBack: () -> Unit,
    onNavigateToExport: (String) -> Unit
) {
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val project by viewModel.currentProject.collectAsState()
    val scenes by viewModel.currentScenes.collectAsState()
    val fastPreviewUri by viewModel.fastPreviewVideoPath.collectAsState()
    val isFastPreviewGenerating by viewModel.isGeneratingFastPreview.collectAsState()

    var selectedSceneIndex by remember { mutableIntStateOf(0) }
    var isPlayingPreview by remember { mutableStateOf(false) }
    var previewCurrentSec by remember { mutableFloatStateOf(0f) }

    val activeScene = scenes.getOrNull(selectedSceneIndex) ?: scenes.firstOrNull()

    Scaffold(
        containerColor = StudioDarkBg,
        topBar = {
            StudioHeader(
                title = project?.title ?: "Video Studio Editor",
                subtitle = "${scenes.size} Scenes • ${project?.canvasRatio?.label?.split(" ")?.first()}",
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
                    IconButton(
                        onClick = { viewModel.undo() },
                        modifier = Modifier.testTag("btn_undo")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = TextSecondary)
                    }
                    IconButton(
                        onClick = { viewModel.redo() },
                        modifier = Modifier.testTag("btn_redo")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = TextSecondary)
                    }
                    Button(
                        onClick = {
                            viewModel.generateFastPreview {
                                Toast.makeText(context, "⚡ Fast Preview Ready!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isFastPreviewGenerating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFastPreviewGenerating) StudioElevated else NeonAmber.copy(alpha = 0.25f),
                            contentColor = NeonAmber
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_top_fast_preview")
                    ) {
                        if (isFastPreviewGenerating) {
                            CircularProgressIndicator(color = NeonAmber, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                        } else {
                            Text("⚡ Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonAmber)
                        }
                    }
                    GlowingButton(
                        text = "Export",
                        icon = Icons.Default.Download,
                        onClick = { onNavigateToExport(projectId) },
                        modifier = Modifier.padding(start = 4.dp),
                        testTag = "btn_editor_export"
                    )
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

            // 1. Core Video Editing Interface (Media3 ExoPlayer, Media Import, Trimming & Subtitles)
            item {
                VideoEditingInterface(
                    scenes = scenes,
                    activeSceneIndex = selectedSceneIndex,
                    previewVideoUri = fastPreviewUri ?: project?.exportPath ?: project?.videoUrl,
                    isGeneratingFastPreview = isFastPreviewGenerating,
                    onGenerateFastPreview = {
                        viewModel.generateFastPreview { path ->
                            if (path != null) {
                                Toast.makeText(context, "⚡ Fast Preview generated! Playing low-res draft...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Could not generate preview", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onSceneSelect = { selectedSceneIndex = it },
                    onSceneUpdated = { updatedScene ->
                        viewModel.updateScene(updatedScene)
                    },
                    onMediaImported = { uri, isVideo ->
                        val active = activeScene
                        if (active != null) {
                            val updated = if (isVideo) {
                                active.copy(videoClipUrl = uri.toString(), isGenerated = true)
                            } else {
                                active.copy(imageUrl = uri.toString(), isGenerated = true)
                            }
                            viewModel.updateScene(updated)
                        }
                    },
                    onAddNewScene = {
                        viewModel.addScene(scenes.size)
                        selectedSceneIndex = scenes.size
                    },
                    onDeleteScene = { sceneId ->
                        viewModel.deleteScene(sceneId)
                        selectedSceneIndex = (selectedSceneIndex - 1).coerceAtLeast(0)
                    }
                )
            }

            // 2. Multi-Track Timeline
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Multi-Track Timeline",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            IconButton(
                                onClick = { viewModel.addScene(scenes.size) }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Scene", tint = NeonCyan)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Video Scene Track
                        Text("Track 1: Video Scenes", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(scenes) { index, sc ->
                                val isSelected = index == selectedSceneIndex
                                Card(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .clickable { selectedSceneIndex = index },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) NeonPurple.copy(alpha = 0.25f) else StudioElevated
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) NeonPurple else StudioCardBorder
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "S${sc.sceneOrder}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) NeonPurple else TextSecondary
                                            )
                                            Text(
                                                text = "${sc.durationSeconds}s",
                                                fontSize = 10.sp,
                                                color = NeonAmber
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = sc.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = sc.transition.label,
                                            fontSize = 9.sp,
                                            color = NeonCyan
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        // Voice Track
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Track 2: Voice Narration (${project?.voiceId ?: "Hindi/English"})", fontSize = 11.sp, color = TextMuted)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        // Music Track
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Track 3: BG Music (${project?.musicTrackId ?: "Cinematic"})", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }

            // 3. Active Scene Detailed Editor
            if (activeScene != null) {
                item {
                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Editing Scene ${activeScene.sceneOrder}: ${activeScene.title}",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                                IconButton(
                                    onClick = { viewModel.deleteScene(activeScene.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Scene", tint = Color.Red.copy(alpha = 0.8f))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Narration Input
                            Text("Scene Narration / Dialogue:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = activeScene.narration,
                                onValueChange = {
                                    viewModel.updateScene(activeScene.copy(narration = it, subtitleText = it))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(85.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = StudioCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = StudioElevated,
                                    unfocusedContainerColor = StudioElevated
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Text Overlay Input
                            Text("On-Screen Text Overlay:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = activeScene.textOverlay,
                                onValueChange = {
                                    viewModel.updateScene(activeScene.copy(textOverlay = it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = StudioCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = StudioElevated,
                                    unfocusedContainerColor = StudioElevated
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Duration & Speed Adjustment
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Duration: ${activeScene.durationSeconds}s", fontSize = 12.sp, color = NeonAmber, fontWeight = FontWeight.Bold)
                                Row {
                                    IconButton(
                                        onClick = {
                                            if (activeScene.durationSeconds > 2) {
                                                viewModel.updateScene(activeScene.copy(durationSeconds = activeScene.durationSeconds - 1))
                                            }
                                        }
                                    ) {
                                        Text("-1s", color = TextSecondary, fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.updateScene(activeScene.copy(durationSeconds = activeScene.durationSeconds + 1))
                                        }
                                    ) {
                                        Text("+1s", color = NeonCyan, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Transition Selector
                            Text("Transition to this scene:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(TransitionType.values().toList()) { trans ->
                                    val isSel = activeScene.transition == trans
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) NeonPurple else StudioElevated)
                                            .clickable {
                                                viewModel.updateScene(activeScene.copy(transition = trans))
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = trans.label,
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
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}
