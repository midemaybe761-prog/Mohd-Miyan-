package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GenerationJob
import com.example.data.model.SystemLog
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioHeader
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val metrics by viewModel.adminMetrics.collectAsState()
    val apiConfig by viewModel.apiConfig.collectAsState()
    val projects by viewModel.projects.collectAsState()

    Scaffold(
        containerColor = StudioDarkBg,
        topBar = {
            StudioHeader(
                title = "Admin Console & Telemetry",
                subtitle = "Infrastructure, Job Queue & Security Logs",
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

            // Overview Metric Tiles
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Total Projects",
                        value = "${projects.size}",
                        icon = Icons.Default.VideoFile,
                        color = NeonPurple,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Storage Used",
                        value = "${"%.1f".format(metrics.storageUsedMb)} MB",
                        icon = Icons.Default.Storage,
                        color = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Render Minutes",
                        value = "${metrics.totalMinutesRendered}m",
                        icon = Icons.Default.Speed,
                        color = NeonAmber,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Active Jobs",
                        value = "${metrics.activeJobsCount}",
                        icon = Icons.Default.CloudDone,
                        color = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Subscription & User Management Status
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("License & Subscription", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            }
                            StatusBadge(text = "ACTIVE ENTERPRISE", color = NeonGreen)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tier: ${metrics.currentTier}",
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Video Length Quota: 10 Minutes per video • Quality: 4K UHD 60FPS • Subtitles: Unlimited Hindi & English",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Generation Job Queue Monitoring
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Render & Generation Job Queue",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            StatusBadge(text = "${metrics.recentJobs.size} JOBS", color = NeonCyan)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        metrics.recentJobs.forEach { job ->
                            JobItemRow(job)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // System Audit & Error Logs
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BugReport, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("System Audit & Error Logs", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            }
                            StatusBadge(text = "LIVE", color = NeonGreen)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        metrics.systemLogs.forEach { log ->
                            SystemLogRow(log)
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        }
    }
}

@Composable
fun JobItemRow(job: GenerationJob) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(StudioElevated)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(job.projectTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary, maxLines = 1)
                Text("${job.provider} • ${job.durationSeconds}s", fontSize = 11.sp, color = TextMuted)
            }
            StatusBadge(text = "${job.status} (${job.progress}%)", color = if (job.progress == 100) NeonGreen else NeonCyan)
        }
    }
}

@Composable
fun SystemLogRow(log: SystemLog) {
    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF070910))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = "[$timeStr]",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${log.module}:",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = log.message,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
