package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiThumbnail
import com.example.data.model.ThumbnailRatio
import com.example.data.model.ThumbnailStyle
import com.example.engine.ThumbnailEngine
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
fun ThumbnailScreen(
    viewModel: MainViewModel,
    projectId: String?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentThumb by viewModel.currentThumbnail.collectAsState()
    val project by viewModel.currentProject.collectAsState()

    var headline by remember { mutableStateOf(currentThumb?.headlineText ?: (project?.title?.take(25)?.uppercase() ?: "NEW AI SECRETS 2026")) }
    var subText by remember { mutableStateOf(currentThumb?.subText ?: "Complete Breakdown 🔥") }
    var badgeText by remember { mutableStateOf(currentThumb?.badgeText ?: "MUST WATCH ⚡") }
    var selectedRatio by remember { mutableStateOf(currentThumb?.ratio ?: ThumbnailRatio.YOUTUBE_16_9) }
    var selectedStyle by remember { mutableStateOf(currentThumb?.style ?: ThumbnailStyle.VIRAL_CLICKBAIT) }
    var textColorHex by remember { mutableStateOf(currentThumb?.textColorHex ?: "#FFDE00") }
    var yPosPercent by remember { mutableFloatStateOf(currentThumb?.textYPositionPercent ?: 0.5f) }
    var hasVignette by remember { mutableStateOf(currentThumb?.hasVignette ?: true) }
    var hasGlowBorder by remember { mutableStateOf(currentThumb?.hasGlowBorder ?: true) }

    Scaffold(
        containerColor = StudioDarkBg,
        topBar = {
            StudioHeader(
                title = "AI Thumbnail Studio",
                subtitle = "High CTR YouTube & Shorts Graphics",
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

            // 1. Live Interactive Thumbnail Preview Canvas
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val ratioModifier = when (selectedRatio) {
                            ThumbnailRatio.SHORTS_9_16 -> Modifier
                                .width(200.dp)
                                .aspectRatio(9f / 16f)
                            ThumbnailRatio.INSTAGRAM_1_1 -> Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                            else -> Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        }

                        // Preview Box
                        Box(
                            modifier = ratioModifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when (selectedStyle) {
                                        ThumbnailStyle.HIGH_TECH_CYBER -> Color(0xFF16103A)
                                        ThumbnailStyle.VIRAL_CLICKBAIT -> Color(0xFF15041E)
                                        ThumbnailStyle.DOCUMENTARY_CINEMATIC -> Color(0xFF14120E)
                                        else -> Color(0xFF0F172A)
                                    }
                                )
                                .border(
                                    if (hasGlowBorder) 3.dp else 1.dp,
                                    if (hasGlowBorder) NeonCyan else StudioCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Badge
                                if (badgeText.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFF0055))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = badgeText,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(1.dp))
                                }

                                // Headline text
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = headline,
                                        color = runCatching { Color(android.graphics.Color.parseColor(textColorHex)) }.getOrDefault(Color.Yellow),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        lineHeight = 24.sp
                                    )
                                    if (subText.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = subText,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(1.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        // AI Auto-Analyze Button
                        GlowingButton(
                            text = "AI Auto-Generate Headline & Concept",
                            icon = Icons.Default.AutoAwesome,
                            onClick = {
                                val currentTitle = project?.title ?: "AI Video Studio"
                                headline = "${currentTitle.take(18).uppercase()} REVEALED!"
                                subText = "DON'T MISS THIS 🚀"
                                badgeText = "VIRAL 2026"
                                textColorHex = "#FFDE00"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_ai_auto_thumbnail"
                        )
                    }
                }
            }

            // 2. Aspect Ratio Selector
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "Thumbnail Dimensions",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                ThumbnailRatio.YOUTUBE_16_9,
                                ThumbnailRatio.SHORTS_9_16,
                                ThumbnailRatio.INSTAGRAM_1_1
                            ).forEach { ratio ->
                                val isSel = selectedRatio == ratio
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) NeonPurple else StudioElevated)
                                        .clickable { selectedRatio = ratio }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (ratio) {
                                            ThumbnailRatio.YOUTUBE_16_9 -> "YouTube 16:9"
                                            ThumbnailRatio.SHORTS_9_16 -> "Shorts 9:16"
                                            else -> "Insta 1:1"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSel) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Text and Badge Editing
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Headline Text:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = headline,
                            onValueChange = { headline = it },
                            modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Sub-Text / Callout:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = subText,
                            onValueChange = { subText = it },
                            modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Badge Label (e.g. VIRAL, NEW ⚡, HINDI):", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = badgeText,
                            onValueChange = { badgeText = it },
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
                    }
                }
            }

            // 4. Color & Visual Style
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Text Color Palette", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        val colors = listOf("#FFDE00", "#00D4FF", "#FFFFFF", "#FF0055", "#10E79D", "#FF9900")
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            colors.forEach { hex ->
                                val isSel = textColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(hex)))
                                        .border(if (isSel) 3.dp else 1.dp, if (isSel) Color.White else Color.Transparent, CircleShape)
                                        .clickable { textColorHex = hex }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Glow Border", color = TextPrimary, fontSize = 13.sp)
                            Switch(
                                checked = hasGlowBorder,
                                onCheckedChange = { hasGlowBorder = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                            )
                        }
                    }
                }
            }

            // 5. Save & Export Action
            item {
                GlowingButton(
                    text = "Save & Export High-Res Thumbnail",
                    icon = Icons.Default.Download,
                    onClick = {
                        val thumb = AiThumbnail(
                            projectId = projectId ?: "generic",
                            headlineText = headline,
                            subText = subText,
                            badgeText = badgeText,
                            ratio = selectedRatio,
                            style = selectedStyle,
                            textColorHex = textColorHex,
                            hasGlowBorder = hasGlowBorder,
                            hasVignette = hasVignette
                        )
                        viewModel.updateThumbnail(thumb)
                        val path = ThumbnailEngine.saveThumbnailToFile(context, thumb)
                        Toast.makeText(context, "Thumbnail exported to app storage!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_save_thumbnail"
                )
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}
