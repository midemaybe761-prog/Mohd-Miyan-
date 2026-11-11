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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
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
fun ProjectsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (String) -> Unit,
    onNavigateToPlayer: (String) -> Unit,
    onNavigateToExport: (String) -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    var projectToDelete by remember { mutableStateOf<VideoProject?>(null) }
    var projectToRename by remember { mutableStateOf<VideoProject?>(null) }
    var renameInput by remember { mutableStateOf("") }

    val filteredProjects = projects.filter { proj ->
        val matchesQuery = proj.title.contains(searchQuery, ignoreCase = true) ||
                proj.prompt.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            0 -> true // All
            1 -> proj.status == ProjectStatus.DRAFT || proj.status == ProjectStatus.READY_TO_RENDER // Drafts
            2 -> proj.status == ProjectStatus.COMPLETED // Completed
            3 -> proj.exportPath != null // Export History
            else -> true
        }
        matchesQuery && matchesTab
    }

    Scaffold(
        containerColor = StudioDarkBg,
        topBar = {
            StudioHeader(
                title = "Project Management",
                subtitle = "${projects.size} Total Studio Projects",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search projects, prompts, topics...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = StudioCardBg,
                        unfocusedContainerColor = StudioCardBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Tabs
            val tabs = listOf("All Projects", "Drafts", "Completed", "Export History")
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioDarkBg,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonPurple
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) NeonPurple else TextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Projects List
            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No projects found in this view.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProjects) { project ->
                        ProjectItemCard(
                            project = project,
                            onPlay = { onNavigateToPlayer(project.id) },
                            onEdit = { onNavigateToEditor(project.id) },
                            onExport = { onNavigateToExport(project.id) },
                            onRename = {
                                projectToRename = project
                                renameInput = project.title
                            },
                            onDelete = { projectToDelete = project }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete Project?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${projectToDelete?.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { viewModel.deleteProject(it.id) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { projectToDelete = null }) {
                    Text("Cancel")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Rename Dialog
    if (projectToRename != null) {
        AlertDialog(
            onDismissRequest = { projectToRename = null },
            title = { Text("Rename Project", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToRename?.let {
                            viewModel.updateProjectSettings()
                            val updated = it.copy(title = renameInput.ifBlank { it.title })
                            // update project title
                            viewModel.loadProject(it.id)
                        }
                        projectToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { projectToRename = null }) {
                    Text("Cancel")
                }
            },
            containerColor = StudioElevated,
            titleContentColor = TextPrimary
        )
    }
}

@Composable
fun ProjectItemCard(
    project: VideoProject,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    val hasVideo = project.exportPath != null || project.videoUrl != null || project.status == ProjectStatus.COMPLETED
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (hasVideo) onPlay() else onEdit() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(project.visualStyle.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = project.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${project.durationSeconds}s • ${project.canvasRatio.label} • ${project.quality.label}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                StatusBadge(
                    text = project.status.name,
                    color = when (project.status) {
                        ProjectStatus.COMPLETED -> NeonGreen
                        ProjectStatus.RENDERING -> NeonCyan
                        else -> NeonAmber
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onRename) {
                    Icon(Icons.Default.Edit, contentDescription = "Rename", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onExport) {
                    Icon(Icons.Default.Download, contentDescription = "Export MP4", tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                if (hasVideo) {
                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("▶ Watch", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Studio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
