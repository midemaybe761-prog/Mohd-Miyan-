package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectStatus
import com.example.data.model.StudioAppMode
import com.example.data.model.VideoProject
import com.example.ui.components.GlowingButton
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudioCard
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

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToCreate: (String?) -> Unit,
    onNavigateToScript: () -> Unit,
    onNavigateToEditor: (String) -> Unit,
    onNavigateToPlayer: (String) -> Unit,
    onNavigateToThumbnails: (String?) -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToExport: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    val apiConfig by viewModel.apiConfig.collectAsState()

    Scaffold(
        containerColor = StudioDarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreate(null) },
                containerColor = NeonPurple,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_create_video")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Video")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Top App Bar Area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Video Studio",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PRO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonPink)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Next-Gen AI Media Production Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onNavigateToAdmin,
                            modifier = Modifier.testTag("btn_admin_panel")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Admin Metrics",
                                tint = NeonCyan
                            )
                        }
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.testTag("btn_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = TextSecondary
                            )
                        }
                    }
                }
            }

            // Mode Badge Banner
            item {
                val isLocal = apiConfig.activeMode == StudioAppMode.MODE_A_LOCAL
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isLocal) StudioElevated else StudioCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSettings() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isLocal) Icons.Default.Build else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (isLocal) NeonGreen else NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isLocal) "Active: Mode A (Local Video Engine)" else "Active: Mode B (AI Cloud Integration)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isLocal) "Offline MediaCodec MP4 rendering • Zero API needed" else "Connected to ${apiConfig.selectedProvider.providerName}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        StatusBadge(
                            text = if (isLocal) "OFFLINE" else "ONLINE",
                            color = if (isLocal) NeonGreen else NeonCyan
                        )
                    }
                }
            }

            // Hero Banner: Create AI Video
            item {
                StudioCard(
                    borderBrush = PurpleGradient,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StatusBadge(text = "AI GENERATOR 2.0", color = NeonCyan)
                            Text(
                                text = "Up to 10 Mins • 4K UHD",
                                fontSize = 11.sp,
                                color = NeonAmber,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Transform Ideas into Viral Videos in Seconds",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Automatic multi-scene division, Hindi & English narration, background music sync, subtitles, and instant MP4 export.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        GlowingButton(
                            text = "🎬 GENERATE AI VIDEO NOW",
                            icon = Icons.Default.AutoAwesome,
                            onClick = { onNavigateToCreate(null) },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_hero_create_video"
                        )
                    }
                }
            }

            // Studio Feature Grid
            item {
                Text(
                    text = "Studio Modules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureTile(
                        title = "Script to Video",
                        subtitle = "Full Script Breakdown",
                        icon = Icons.Default.Movie,
                        iconTint = NeonPurple,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToScript
                    )
                    FeatureTile(
                        title = "Text to Video",
                        subtitle = "Prompt to 4K Scenes",
                        icon = Icons.Default.TextFields,
                        iconTint = NeonCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCreate(null) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureTile(
                        title = "Image to Video",
                        subtitle = "Animate Visuals",
                        icon = Icons.Default.Image,
                        iconTint = NeonAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCreate(null) }
                    )
                    FeatureTile(
                        title = "AI Thumbnail",
                        subtitle = "High CTR YouTube & Shorts",
                        icon = Icons.Default.AutoAwesome,
                        iconTint = NeonPink,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToThumbnails(null) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureTile(
                        title = "Editing Studio",
                        subtitle = "Multi-Track Timeline",
                        icon = Icons.Default.Edit,
                        iconTint = NeonGreen,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val activeId = projects.firstOrNull()?.id
                            if (activeId != null) onNavigateToEditor(activeId) else onNavigateToCreate(null)
                        }
                    )
                    FeatureTile(
                        title = "My Projects",
                        subtitle = "Drafts & Exports",
                        icon = Icons.Default.VideoLibrary,
                        iconTint = NeonPurple,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProjects
                    )
                }
            }

            // Recent Projects
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Recent Projects",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "View All (${projects.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.clickable { onNavigateToProjects() }
                    )
                }
            }

            if (projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(StudioCardBg)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No projects yet. Tap 'Create AI Video Now' to begin!",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(projects.take(4)) { project ->
                    RecentProjectCard(
                        project = project,
                        onPlay = { onNavigateToPlayer(project.id) },
                        onEdit = { onNavigateToEditor(project.id) },
                        onExport = { onNavigateToExport(project.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun FeatureTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RecentProjectCard(
    project: VideoProject,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit
) {
    val hasVideo = project.exportPath != null || project.videoUrl != null || project.status == ProjectStatus.COMPLETED
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (hasVideo) onPlay() else onEdit() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StudioElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = project.visualStyle.emoji,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${project.durationSeconds}s • ${project.canvasRatio.label.split(" ").first()} • ${project.quality.label}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPlay) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Watch Video Player",
                        tint = NeonGreen
                    )
                }
                IconButton(onClick = onExport) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Export Video",
                        tint = NeonCyan
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Video",
                        tint = NeonPurple
                    )
                }
            }
        }
    }
}
