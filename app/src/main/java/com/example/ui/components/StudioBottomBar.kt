package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * 5 Navigation Destinations required:
 * 1. Home 🏠
 * 2. AI Video Generate 🎬
 * 3. My Projects 📁
 * 4. Thumbnail 🎨
 * 5. Settings ⚙️
 */
enum class BottomNavItem(
    val route: String,
    val title: String,
    val emoji: String,
    val icon: ImageVector,
    val activeColor: Color,
    val testTag: String
) {
    HOME(
        route = "dashboard",
        title = "Home",
        emoji = "🏠",
        icon = Icons.Default.Home,
        activeColor = NeonCyan,
        testTag = "nav_item_home"
    ),
    GENERATE(
        route = "create_video",
        title = "AI Video",
        emoji = "🎬",
        icon = Icons.Default.Movie,
        activeColor = NeonPurple,
        testTag = "nav_item_generate"
    ),
    PROJECTS(
        route = "projects_list",
        title = "Projects",
        emoji = "📁",
        icon = Icons.Default.Folder,
        activeColor = NeonPink,
        testTag = "nav_item_projects"
    ),
    THUMBNAIL(
        route = "thumbnail_generator/default",
        title = "Thumbnail",
        emoji = "🎨",
        icon = Icons.Default.AutoAwesome,
        activeColor = NeonAmber,
        testTag = "nav_item_thumbnail"
    ),
    SETTINGS(
        route = "settings",
        title = "Settings",
        emoji = "⚙️",
        icon = Icons.Default.Settings,
        activeColor = NeonCyan,
        testTag = "nav_item_settings"
    )
}

@Composable
fun StudioBottomBar(
    currentRoute: String?,
    onNavigate: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = StudioDarkBg.copy(alpha = 0.96f),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, ambientColor = NeonPurple.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Subtle neon glowing top divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                StudioCardBorder.copy(alpha = 0.2f),
                                NeonPurple.copy(alpha = 0.6f),
                                NeonCyan.copy(alpha = 0.6f),
                                StudioCardBorder.copy(alpha = 0.2f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem.values().forEach { item ->
                    val isSelected = when (item) {
                        BottomNavItem.HOME -> currentRoute == "dashboard"
                        BottomNavItem.GENERATE -> currentRoute?.startsWith("create_video") == true || currentRoute?.startsWith("script_writer") == true
                        BottomNavItem.PROJECTS -> currentRoute?.startsWith("projects_list") == true
                        BottomNavItem.THUMBNAIL -> currentRoute?.startsWith("thumbnail_generator") == true
                        BottomNavItem.SETTINGS -> currentRoute?.startsWith("settings") == true || currentRoute?.startsWith("admin_panel") == true
                    }

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) item.activeColor else TextMuted,
                        label = "iconTint"
                    )

                    val bgAlpha = if (isSelected) 0.16f else 0f
                    val containerBg by animateColorAsState(
                        targetValue = item.activeColor.copy(alpha = bgAlpha),
                        label = "containerBg"
                    )

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(containerBg)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onNavigate(item)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag(item.testTag),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = iconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
