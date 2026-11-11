package com.example.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.Scene
import com.example.data.model.TransitionType
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
import kotlinx.coroutines.delay
import java.io.File

/**
 * Subtitle styling configuration for live video overlay
 */
data class SubtitleOverlayStyle(
    val textColor: Color = Color(0xFFFFDE00), // Vibrant yellow standard
    val backgroundColor: Color = Color.Black.copy(alpha = 0.75f),
    val fontSizeSp: Int = 16,
    val positionBottomDp: Int = 20,
    val bold: Boolean = true
)

/**
 * VideoEditingInterface
 *
 * Core interactive video editing interface component built with Media3 ExoPlayer.
 *
 * Capabilities:
 * 1. Media Import: Zero-permission Android Photo Picker for video & image selection.
 * 2. Real-Time Media3 ExoPlayer Preview: Play, Pause, Scrubbing Seekbar, Live Timecodes, Rewind/Fast-Forward 10s.
 * 3. Trimming Clips: Interactive range slider for start and end trim boundaries with live loop playback within the trimmed window.
 * 4. Subtitle Overlay: Live subtitle text preview on the video canvas with configurable font size, text color, background box, and custom dialogue editing.
 * 5. Multi-Scene Sequence: Timeline track showing trimmed scenes, transitions, and duration badges.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoEditingInterface(
    scenes: List<Scene>,
    activeSceneIndex: Int,
    previewVideoUri: String?,
    isGeneratingFastPreview: Boolean = false,
    onGenerateFastPreview: () -> Unit = {},
    onSceneSelect: (Int) -> Unit,
    onSceneUpdated: (Scene) -> Unit,
    onMediaImported: (Uri, isVideo: Boolean) -> Unit,
    onAddNewScene: () -> Unit,
    onDeleteScene: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Media3 ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = false
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var volume by remember { mutableFloatStateOf(1f) }
    var isMuted by remember { mutableStateOf(false) }
    var areControlsVisible by remember { mutableStateOf(true) }

    val activeScene = scenes.getOrNull(activeSceneIndex) ?: scenes.firstOrNull()

    // Trimming State: Start & End in seconds
    var trimStartSec by remember(activeScene?.id) {
        mutableFloatStateOf(0f)
    }
    var trimEndSec by remember(activeScene?.id, activeScene?.durationSeconds) {
        mutableFloatStateOf((activeScene?.durationSeconds ?: 5).toFloat())
    }

    // Subtitle Customization State
    var subtitleText by remember(activeScene?.subtitleText) {
        mutableStateOf(activeScene?.subtitleText ?: activeScene?.narration ?: "")
    }
    var subtitleStyle by remember {
        mutableStateOf(SubtitleOverlayStyle())
    }

    // Tab state: 0 = Trim & Clip, 1 = Subtitles & Overlay, 2 = Transitions & Effects
    var selectedToolTab by remember { mutableIntStateOf(0) }

    // Android Photo/Media Picker (Zero-permission Play Policy compliant)
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val isVideo = context.contentResolver.getType(uri)?.startsWith("video") == true
            onMediaImported(uri, isVideo)
        }
    }

    // Load active scene media into ExoPlayer
    LaunchedEffect(previewVideoUri, activeScene?.videoClipUrl) {
        val targetUriStr = activeScene?.videoClipUrl ?: previewVideoUri
        if (!targetUriStr.isNullOrBlank()) {
            try {
                val uri = parseVideoUri(targetUriStr)
                val mediaItem = MediaItem.fromUri(uri)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                // Seek to trimmed start
                exoPlayer.seekTo((trimStartSec * 1000).toLong())
            } catch (e: Exception) {
                // Handled gracefully
            }
        }
    }

    // Player event listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> isBuffering = true
                    Player.STATE_READY -> {
                        isBuffering = false
                        totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
                        if (trimEndSec == 0f || trimEndSec > totalDurationMs / 1000f) {
                            trimEndSec = (totalDurationMs / 1000f).coerceAtLeast(1f)
                        }
                    }
                    Player.STATE_ENDED -> {
                        isBuffering = false
                        isPlaying = false
                        // Loop back to trim start for seamless editing review
                        exoPlayer.seekTo((trimStartSec * 1000).toLong())
                    }
                    Player.STATE_IDLE -> isBuffering = false
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Poll current playback position and enforce trim boundary looping
    LaunchedEffect(isPlaying, trimStartSec, trimEndSec) {
        while (isPlaying) {
            val pos = exoPlayer.currentPosition
            currentPositionMs = pos
            totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)

            val trimEndMs = (trimEndSec * 1000).toLong()
            if (trimEndMs > 0 && pos >= trimEndMs) {
                // Loop back to trim start
                exoPlayer.seekTo((trimStartSec * 1000).toLong())
            }
            delay(150)
        }
    }

    // Auto-hide controls
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying) {
            delay(3500)
            areControlsVisible = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioDarkBg),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. VIDEO CANVAS PREVIEW & SUBTITLE OVERLAY
        // ==========================================
        StudioCard(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            areControlsVisible = !areControlsVisible
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Media3 ExoPlayer Viewport
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false // Custom responsive M3 overlay
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Buffering spinner
                    if (isBuffering) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    // Fallback visual scene backdrop if no raw video clip loaded yet
                    val hasVideoMedia = !activeScene?.videoClipUrl.isNullOrBlank() || !previewVideoUri.isNullOrBlank()
                    if (!hasVideoMedia) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            NeonPurple.copy(alpha = 0.45f),
                                            StudioDarkBg.copy(alpha = 0.95f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Movie,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = activeScene?.title ?: "Scene Visual Preview",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                text = activeScene?.visualPrompt?.ifBlank { "Tap 'Import Media' to insert custom footage" } ?: "",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }

                    // ==========================================
                    // LIVE SUBTITLE OVERLAY LAYER
                    // ==========================================
                    if (subtitleText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = subtitleStyle.positionBottomDp.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(subtitleStyle.backgroundColor)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("live_subtitle_overlay")
                        ) {
                            Text(
                                text = subtitleText,
                                color = subtitleStyle.textColor,
                                fontSize = subtitleStyle.fontSizeSp.sp,
                                fontWeight = if (subtitleStyle.bold) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }

                    // ==========================================
                    // PLAYER OVERLAY CONTROLS
                    // ==========================================
                    if (areControlsVisible || !isPlaying) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.6f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                                .padding(10.dp)
                        ) {
                            // Top Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(
                                    text = "SCENE ${activeScene?.sceneOrder ?: 1} / ${scenes.size}",
                                    color = NeonCyan
                                )
                                IconButton(
                                    onClick = {
                                        isMuted = !isMuted
                                        exoPlayer.volume = if (isMuted) 0f else volume
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                        contentDescription = "Mute/Unmute",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Center Action Buttons (Rewind 10s | Play/Pause | Forward 10s)
                            Row(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        val newPos = (exoPlayer.currentPosition - 10_000).coerceAtLeast(0L)
                                        exoPlayer.seekTo(newPos)
                                        currentPositionMs = newPos
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Replay10,
                                        contentDescription = "Rewind 10s",
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(NeonPurple.copy(alpha = 0.9f))
                                        .clickable {
                                            if (isPlaying) {
                                                exoPlayer.pause()
                                            } else {
                                                exoPlayer.play()
                                            }
                                        }
                                        .testTag("btn_editor_play_pause"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val newPos = (exoPlayer.currentPosition + 10_000).coerceAtMost(totalDurationMs)
                                        exoPlayer.seekTo(newPos)
                                        currentPositionMs = newPos
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Forward10,
                                        contentDescription = "Forward 10s",
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            // Bottom Seekbar & Current Position Timecode
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = formatVideoTimecode(currentPositionMs),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Trim: ${trimStartSec.toInt()}s - ${trimEndSec.toInt()}s (${(trimEndSec - trimStartSec).toInt()}s)",
                                        color = NeonAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                val progressRatio = if (totalDurationMs > 0) currentPositionMs.toFloat() / totalDurationMs else 0f
                                Slider(
                                    value = progressRatio.coerceIn(0f, 1f),
                                    onValueChange = { ratio ->
                                        val seekTarget = (ratio * totalDurationMs).toLong()
                                        exoPlayer.seekTo(seekTarget)
                                        currentPositionMs = seekTarget
                                    },
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonPurple,
                                        activeTrackColor = NeonPurple,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. TIMELINE TRACK & CLIP SELECTOR
        // ==========================================
        StudioCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clips Timeline", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Low-Resolution Fast Preview Button
                        Button(
                            onClick = onGenerateFastPreview,
                            enabled = !isGeneratingFastPreview,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isGeneratingFastPreview) StudioElevated else NeonAmber.copy(alpha = 0.2f),
                                contentColor = NeonAmber
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_fast_preview")
                        ) {
                            if (isGeneratingFastPreview) {
                                CircularProgressIndicator(color = NeonAmber, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rendering...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonAmber)
                            } else {
                                Text("⚡ Fast Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonAmber)
                            }
                        }

                        // Import Media Button
                        Button(
                            onClick = {
                                mediaPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_import_media")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Media", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Add Scene Button
                        Button(
                            onClick = onAddNewScene,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_add_scene")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Clip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal Timeline Strip
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(scenes) { index, sc ->
                        val isSelected = index == activeSceneIndex
                        Card(
                            modifier = Modifier
                                .width(135.dp)
                                .clickable { onSceneSelect(index) }
                                .testTag("timeline_clip_$index"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NeonPurple.copy(alpha = 0.22f) else StudioElevated
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) NeonPurple else StudioCardBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Clip ${sc.sceneOrder}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) NeonPurple else TextSecondary
                                    )
                                    Text(
                                        text = "${sc.durationSeconds}s",
                                        fontSize = 10.sp,
                                        color = NeonAmber,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sc.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (sc.subtitleText.isNotBlank()) "📝 Subtitled" else "✂️ ${sc.transition.label}",
                                    fontSize = 10.sp,
                                    color = if (sc.subtitleText.isNotBlank()) NeonGreen else NeonCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. EDITING TOOL PANELS (TRIM & SUBTITLES)
        // ==========================================
        StudioCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                // Tool Switcher Tabs: Trim, Subtitles, Transitions, Style
                val tabs = listOf("✂️ Trim Clip", "💬 Subtitles Overlay", "✨ Transitions", "🎨 Style & Color")
                TabRow(
                    selectedTabIndex = selectedToolTab,
                    containerColor = Color.Transparent,
                    contentColor = NeonCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedToolTab]),
                            color = NeonPurple
                        )
                    }
                ) {
                    tabs.forEachIndexed { idx, title ->
                        Tab(
                            selected = selectedToolTab == idx,
                            onClick = { selectedToolTab = idx },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedToolTab == idx) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedToolTab == idx) NeonPurple else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedToolTab) {
                    // TAB 0: TRIM CLIP
                    0 -> {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Trim Boundaries", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Text(
                                    text = "Start: ${"%.1f".format(trimStartSec)}s • End: ${"%.1f".format(trimEndSec)}s",
                                    fontSize = 12.sp,
                                    color = NeonAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val maxRange = (activeScene?.durationSeconds?.coerceAtLeast(10) ?: 15).toFloat()
                            RangeSlider(
                                value = trimStartSec..trimEndSec.coerceAtMost(maxRange),
                                onValueChange = { range ->
                                    trimStartSec = range.start
                                    trimEndSec = range.endInclusive
                                    val newDuration = (range.endInclusive - range.start).toInt().coerceAtLeast(1)
                                    if (activeScene != null) {
                                        onSceneUpdated(activeScene.copy(durationSeconds = newDuration))
                                    }
                                },
                                valueRange = 0f..maxRange,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = NeonCyan,
                                    inactiveTrackColor = StudioElevated
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("trim_range_slider")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        // Set trim start to current playhead
                                        val currentSec = currentPositionMs / 1000f
                                        if (currentSec < trimEndSec) {
                                            trimStartSec = currentSec
                                            val newDur = (trimEndSec - trimStartSec).toInt().coerceAtLeast(1)
                                            activeScene?.let { onSceneUpdated(it.copy(durationSeconds = newDur)) }
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Set Start Here", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        // Set trim end to current playhead
                                        val currentSec = currentPositionMs / 1000f
                                        if (currentSec > trimStartSec) {
                                            trimEndSec = currentSec
                                            val newDur = (trimEndSec - trimStartSec).toInt().coerceAtLeast(1)
                                            activeScene?.let { onSceneUpdated(it.copy(durationSeconds = newDur)) }
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Set End Here", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        // Preview trimmed loop
                                        exoPlayer.seekTo((trimStartSec * 1000).toLong())
                                        exoPlayer.play()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Test Loop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // TAB 1: SUBTITLES OVERLAY
                    1 -> {
                        Column {
                            Text("Subtitle Dialogue Text:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = subtitleText,
                                onValueChange = {
                                    subtitleText = it
                                    activeScene?.let { sc ->
                                        onSceneUpdated(sc.copy(subtitleText = it, textOverlay = it))
                                    }
                                },
                                placeholder = { Text("Type subtitles to overlay on video...", color = TextMuted) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .testTag("input_subtitle_text"),
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

                            // Subtitle Position & Size
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Font Size: ${subtitleStyle.fontSizeSp}sp", fontSize = 12.sp, color = TextPrimary)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(14, 16, 18, 22).forEach { size ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (subtitleStyle.fontSizeSp == size) NeonPurple else StudioElevated)
                                                .clickable { subtitleStyle = subtitleStyle.copy(fontSizeSp = size) }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${size}sp",
                                                fontSize = 11.sp,
                                                color = if (subtitleStyle.fontSizeSp == size) Color.White else TextSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB 2: TRANSITION EFFECTS (Fade, Slide, Crossfade, Flash, Wipe, Iris)
                    2 -> {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Transition to this Clip:", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${activeScene?.transition?.emoji ?: ""} ${activeScene?.transition?.label ?: "None"}",
                                    fontSize = 12.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Applies visual transition effect at the boundary entering into Scene ${activeScene?.sceneOrder ?: 1}.",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Transitions Grid
                            val allTransitions = TransitionType.values().toList()
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                allTransitions.chunked(3).forEach { rowTransitions ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowTransitions.forEach { trans ->
                                            val isSelected = activeScene?.transition == trans
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        activeScene?.let { sc ->
                                                            onSceneUpdated(sc.copy(transition = trans))
                                                        }
                                                    }
                                                    .testTag("transition_select_${trans.name}"),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelected) NeonPurple.copy(alpha = 0.25f) else StudioElevated
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (isSelected) NeonPurple else StudioCardBorder
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 10.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(text = trans.emoji, fontSize = 20.sp)
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = trans.label,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) NeonPurple else TextPrimary,
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        text = "${trans.durationSec}s",
                                                        fontSize = 9.sp,
                                                        color = NeonAmber
                                                    )
                                                }
                                            }
                                        }
                                        // Fill remaining slots if row has fewer than 3 items
                                        repeat(3 - rowTransitions.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB 3: STYLE & COLOR
                    3 -> {
                        Column {
                            Text("Subtitle Text Color:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            val colorOptions = listOf(
                                "Yellow" to Color(0xFFFFDE00),
                                "White" to Color.White,
                                "Cyan" to NeonCyan,
                                "Green" to NeonGreen,
                                "Pink" to NeonPink
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                colorOptions.forEach { (name, col) ->
                                    val isSelected = subtitleStyle.textColor == col
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(col)
                                            .border(
                                                2.dp,
                                                if (isSelected) Color.White else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable { subtitleStyle = subtitleStyle.copy(textColor = col) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Background Box Opacity:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(
                                    "Transparent" to Color.Transparent,
                                    "Dark 50%" to Color.Black.copy(alpha = 0.5f),
                                    "Solid Black" to Color.Black.copy(alpha = 0.85f),
                                    "Neon Purple" to NeonPurple.copy(alpha = 0.7f)
                                ).forEach { (label, bgCol) ->
                                    val isSel = subtitleStyle.backgroundColor == bgCol
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) NeonCyan.copy(alpha = 0.25f) else StudioElevated)
                                            .border(1.dp, if (isSel) NeonCyan else StudioCardBorder, RoundedCornerShape(8.dp))
                                            .clickable { subtitleStyle = subtitleStyle.copy(backgroundColor = bgCol) }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(label, fontSize = 11.sp, color = if (isSel) NeonCyan else TextSecondary, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
