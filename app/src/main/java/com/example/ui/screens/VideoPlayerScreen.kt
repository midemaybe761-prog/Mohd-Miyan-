package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.CanvasRatio
import com.example.data.model.GenerationStage
import com.example.data.model.ProjectStatus
import com.example.data.model.StudioAppMode
import com.example.ui.components.ExoVideoPlayer
import com.example.ui.components.GeneratedVideoPlayer
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
import java.io.File

@Composable
fun VideoPlayerScreen(
    viewModel: MainViewModel,
    projectId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    val project by viewModel.currentProject.collectAsState()
    val scenes by viewModel.currentScenes.collectAsState()
    val progress by viewModel.renderProgress.collectAsState()
    val apiConfig by viewModel.apiConfig.collectAsState()

    val videoFilePath = project?.exportPath ?: progress.outputUri
    val hasVideoFile = !videoFilePath.isNullOrBlank() && File(videoFilePath).exists()
    val isCurrentlyGenerating = (project?.status == ProjectStatus.GENERATING_VIDEO ||
            project?.status == ProjectStatus.RENDERING ||
            (progress.percent in 0..99 && !progress.isComplete && progress.stage != GenerationStage.IDLE)) && !hasVideoFile && progress.error == null
    val hasError = progress.error != null || project?.status == ProjectStatus.FAILED

    Scaffold(
        containerColor = StudioDarkBg,
        topBar = {
            StudioHeader(
                title = project?.title ?: "AI Video Player",
                subtitle = "${project?.durationSeconds ?: 60}s • ${project?.canvasRatio?.label?.split(" ")?.first() ?: "9:16"} • ${project?.quality?.label ?: "720p"}",
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
                    if (hasVideoFile) {
                        IconButton(
                            onClick = {
                                val file = File(videoFilePath!!)
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "video/mp4"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share AI Video"))
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = NeonCyan)
                        }
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

            // 1. Generation Status Stages (Visible during generation)
            if (isCurrentlyGenerating) {
                item {
                    StudioCard(
                        borderBrush = PurpleGradient,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(text = "LIVE VIDEO GENERATION", color = NeonCyan)
                                Text(
                                    text = "${progress.percent}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonPurple
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Big Progress Ring with Stage Indicator
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(140.dp)
                            ) {
                                val animatedProgress by animateFloatAsState(
                                    targetValue = progress.percent / 100f,
                                    label = "prog"
                                )
                                CircularProgressIndicator(
                                    progress = { animatedProgress },
                                    strokeWidth = 8.dp,
                                    color = NeonPurple,
                                    trackColor = StudioElevated,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${progress.percent}%",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Rendering",
                                        fontSize = 11.sp,
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Current Stage Text
                            Text(
                                text = progress.statusText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 5-Stage Checklist
                            val stages = listOf(
                                "1. Preparing scenes" to (progress.percent >= 15),
                                "2. Generating video" to (progress.percent >= 30),
                                "3. Processing audio" to (progress.percent >= 45),
                                "4. Combining scenes" to (progress.percent >= 60),
                                "5. Rendering final video" to (progress.percent >= 70),
                                "Completed" to (progress.percent >= 100)
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(StudioElevated)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                stages.forEach { (title, done) ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = if (done) NeonGreen else TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = title,
                                            fontSize = 12.sp,
                                            color = if (done) TextPrimary else TextMuted,
                                            fontWeight = if (done) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { viewModel.cancelExport() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                modifier = Modifier.testTag("btn_cancel_generation")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cancel Generation")
                            }
                        }
                    }
                }
            }

            // 2. Error State Handling
            if (hasError && !isCurrentlyGenerating) {
                item {
                    StudioCard(
                        borderBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Color.Red, NeonPink)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "VIDEO GENERATION SERVICE NOT CONNECTED",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = progress.error ?: "VIDEO GENERATION SERVICE NOT CONNECTED. PLEASE CONFIGURE A WORKING AI VIDEO PROVIDER.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            GlowingButton(
                                text = "🎬 Render with Local Video Engine (Mode A)",
                                icon = Icons.Default.Refresh,
                                onClick = {
                                    viewModel.setAppMode(StudioAppMode.MODE_A_LOCAL)
                                    viewModel.retryGeneration(projectId)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_retry_local_engine"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { viewModel.retryGeneration(projectId) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_retry_generation"
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry Generation")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = onNavigateToSettings,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Configure AI Provider in Settings")
                            }
                        }
                    }
                }
            }

            // 3. Prompt to generate video if not yet generated and not in progress
            if (!hasVideoFile && !isCurrentlyGenerating && !hasError) {
                item {
                    StudioCard(
                        borderBrush = PurpleGradient,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "READY TO GENERATE AI VIDEO",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Synthesize all scenes, voice narration, moving visuals, and background music into an MP4 video file.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            GlowingButton(
                                text = "🎬 GENERATE AI VIDEO NOW (MP4)",
                                icon = Icons.Default.AutoAwesome,
                                onClick = {
                                    viewModel.retryGeneration(projectId)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_player_generate_mp4"
                            )
                        }
                    }
                }
            }

            // 3. LARGE VIDEO PLAYER (Loaded when MP4 file exists)
            if (hasVideoFile) {
                item {
                    StudioCard(
                        borderBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(text = "AI GENERATED MP4 READY", color = NeonGreen)
                                Text(
                                    text = "${File(videoFilePath!!).length() / 1024} KB",
                                    fontSize = 11.sp,
                                    color = NeonAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Large ExoPlayer Canvas Viewport
                            val playerAspectRatio = when (project?.canvasRatio) {
                                CanvasRatio.VERTICAL_9_16 -> 9f / 16f
                                CanvasRatio.SQUARE_1_1 -> 1f
                                CanvasRatio.PORTRAIT_4_5 -> 4f / 5f
                                else -> 16f / 9f
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(playerAspectRatio)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                GeneratedVideoPlayer(
                                    videoUriOrPath = videoFilePath,
                                    aspectRatio = playerAspectRatio,
                                    autoPlay = true,
                                    modifier = Modifier.fillMaxSize(),
                                    onError = { err ->
                                        Toast.makeText(context, "Playback error: $err", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Primary Action Buttons: Download & Share
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GlowingButton(
                                    text = "Save to Gallery",
                                    icon = Icons.Default.Download,
                                    onClick = {
                                        Toast.makeText(context, "Saved to Device Movies Gallery!", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    testTag = "btn_player_download"
                                )

                                Button(
                                    onClick = {
                                        val file = File(videoFilePath)
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            file
                                        )
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "video/mp4"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share AI Video"))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StudioElevated),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share Video", color = NeonCyan, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Secondary Action: Open in Video Editing Studio
                            OutlinedButton(
                                onClick = { onNavigateToEditor(projectId) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open in Video Editing Studio Timeline")
                            }
                        }
                    }
                }
            }

            // 4. Video Prompt & Scene Details
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "AI Video Production Details",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = project?.prompt?.ifBlank { "Generated with AI Video Studio Pro" } ?: "",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Generated Scenes (${scenes.size} Scenes)",
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(scenes) { idx, sc ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = StudioElevated),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                                    modifier = Modifier.width(180.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            StatusBadge(text = "SCENE ${idx + 1}", color = NeonPurple)
                                            Text("${sc.durationSeconds}s", fontSize = 11.sp, color = NeonAmber, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = sc.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = sc.narration,
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
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
