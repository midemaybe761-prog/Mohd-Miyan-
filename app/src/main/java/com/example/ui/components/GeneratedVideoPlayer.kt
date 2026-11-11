package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.io.File

/**
 * Helper to resolve an Activity from a given Context.
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Formats a duration in milliseconds to mm:ss or hh:mm:ss.
 */
fun formatVideoTimecode(timeMs: Long): String {
    val totalSeconds = (timeMs.coerceAtLeast(0L) / 1000).toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

/**
 * Resolves local file paths, file:// URIs, content:// URIs, or remote http/https URLs
 * into a valid Android [Uri] for ExoPlayer.
 */
fun parseVideoUri(pathOrUriString: String): Uri {
    val trimmed = pathOrUriString.trim()
    return when {
        trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) ||
                trimmed.startsWith("content://", ignoreCase = true) -> {
            Uri.parse(trimmed)
        }
        trimmed.startsWith("file://", ignoreCase = true) -> {
            Uri.parse(trimmed)
        }
        else -> {
            // Local file path (e.g. from app external storage or MediaStore)
            Uri.fromFile(File(trimmed))
        }
    }
}

/**
 * GeneratedVideoPlayer
 *
 * Production-ready Video Player composable implemented with Media3 ExoPlayer and Media3 UI.
 * Handles Play, Pause, Seekbar scrubbing, Quick Seek (-10s / +10s), Volume control,
 * Fullscreen toggling (with landscape activity orientation support), Replay, and robust URI loading
 * for downloaded or locally rendered MP4 files from backend/storage.
 */
@OptIn(UnstableApi::class)
@Composable
fun GeneratedVideoPlayer(
    videoUriOrPath: String,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16f / 9f,
    autoPlay: Boolean = true,
    showCustomControls: Boolean = true,
    useMedia3DefaultControls: Boolean = false,
    onPlaybackStateChanged: (isPlaying: Boolean, isEnded: Boolean) -> Unit = { _, _ -> },
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    // Media3 ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = autoPlay
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    var volume by remember { mutableFloatStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }
    var areControlsVisible by remember { mutableStateOf(true) }
    var isPlaybackEnded by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    // Toggle screen orientation on fullscreen
    LaunchedEffect(isFullscreen) {
        activity?.let { act ->
            act.requestedOrientation = if (isFullscreen) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    // Reset orientation on dispose
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Load media whenever videoUriOrPath changes
    LaunchedEffect(videoUriOrPath) {
        if (videoUriOrPath.isNotBlank()) {
            try {
                val uri = parseVideoUri(videoUriOrPath)
                val mediaItem = MediaItem.fromUri(uri)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                if (autoPlay) {
                    exoPlayer.play()
                }
            } catch (e: Exception) {
                onError("Failed to load video URI: ${e.localizedMessage}")
            }
        }
    }

    // Player event listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                onPlaybackStateChanged(playing, isPlaybackEnded)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isBuffering = true
                        isPlaybackEnded = false
                    }
                    Player.STATE_READY -> {
                        isBuffering = false
                        isPlaybackEnded = false
                        totalDuration = exoPlayer.duration.coerceAtLeast(0L)
                    }
                    Player.STATE_ENDED -> {
                        isBuffering = false
                        isPlaybackEnded = true
                        isPlaying = false
                        onPlaybackStateChanged(false, true)
                    }
                    Player.STATE_IDLE -> {
                        isBuffering = false
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                onError(error.localizedMessage ?: "ExoPlayer playback error (Code: ${error.errorCodeName})")
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Periodic timecode poll
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
            totalDuration = exoPlayer.duration.coerceAtLeast(0L)
            delay(200)
        }
    }

    // Auto-hide custom controls after 3.5s of playback
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying && !useMedia3DefaultControls) {
            delay(3500)
            areControlsVisible = false
        }
    }

    val finalBoxModifier = if (isFullscreen) {
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    } else {
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
    }

    Box(
        modifier = finalBoxModifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            if (!useMedia3DefaultControls) {
                areControlsVisible = !areControlsVisible
            }
        },
        contentAlignment = Alignment.Center
    ) {
        // Media3 PlayerView Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = useMedia3DefaultControls
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
                playerView.useController = useMedia3DefaultControls
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering Spinner
        if (isBuffering) {
            CircularProgressIndicator(
                color = NeonCyan,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("player_buffering_indicator")
            )
        }

        // Custom Overlay Controls
        if (showCustomControls && !useMedia3DefaultControls) {
            AnimatedVisibility(
                visible = areControlsVisible || !isPlaying || isPlaybackEnded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.65f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(12.dp)
                ) {
                    // Top Bar: Fullscreen & Volume Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    isMuted = !isMuted
                                    exoPlayer.volume = if (isMuted) 0f else volume
                                },
                                modifier = Modifier.testTag("btn_player_mute_toggle")
                            ) {
                                Icon(
                                    imageVector = if (isMuted || volume == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                    contentDescription = "Mute/Unmute",
                                    tint = Color.White
                                )
                            }

                            if (!isMuted) {
                                Slider(
                                    value = volume,
                                    onValueChange = {
                                        volume = it
                                        exoPlayer.volume = it
                                    },
                                    valueRange = 0f..1f,
                                    modifier = Modifier
                                        .width(90.dp)
                                        .testTag("player_volume_slider"),
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonCyan,
                                        activeTrackColor = NeonCyan
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { isFullscreen = !isFullscreen },
                            modifier = Modifier.testTag("btn_player_fullscreen_toggle")
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isFullscreen) "Exit Fullscreen" else "Enter Fullscreen",
                                tint = Color.White
                            )
                        }
                    }

                    // Center Control Area: Rewind 10s | Play/Pause/Replay | Forward 10s
                    Box(
                        modifier = Modifier.align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(22.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rewind 10s
                            IconButton(
                                onClick = {
                                    val newPos = (exoPlayer.currentPosition - 10_000).coerceAtLeast(0L)
                                    exoPlayer.seekTo(newPos)
                                    currentPosition = newPos
                                },
                                modifier = Modifier.testTag("btn_player_rewind_10")
                            ) {
                                Icon(
                                    Icons.Default.Replay10,
                                    contentDescription = "Rewind 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }

                            // Center Action Button (Play / Pause / Replay)
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple.copy(alpha = 0.92f))
                                    .clickable {
                                        if (isPlaybackEnded) {
                                            exoPlayer.seekTo(0)
                                            exoPlayer.play()
                                            isPlaybackEnded = false
                                        } else if (isPlaying) {
                                            exoPlayer.pause()
                                        } else {
                                            exoPlayer.play()
                                        }
                                    }
                                    .testTag("btn_player_play_pause"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isPlaybackEnded -> Icons.Default.Replay
                                        isPlaying -> Icons.Default.Pause
                                        else -> Icons.Default.PlayArrow
                                    },
                                    contentDescription = if (isPlaybackEnded) "Replay" else if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            // Forward 10s
                            IconButton(
                                onClick = {
                                    val newPos = (exoPlayer.currentPosition + 10_000).coerceAtMost(totalDuration)
                                    exoPlayer.seekTo(newPos)
                                    currentPosition = newPos
                                },
                                modifier = Modifier.testTag("btn_player_forward_10")
                            ) {
                                Icon(
                                    Icons.Default.Forward10,
                                    contentDescription = "Forward 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }

                    // Bottom Bar: Timecodes and Scrubbing Seekbar
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
                                text = formatVideoTimecode(currentPosition),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("text_current_time")
                            )
                            Text(
                                text = formatVideoTimecode(totalDuration),
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("text_total_time")
                            )
                        }

                        // Interactive Seekbar
                        val progressRatio = if (totalDuration > 0) currentPosition.toFloat() / totalDuration else 0f
                        Slider(
                            value = progressRatio.coerceIn(0f, 1f),
                            onValueChange = { newRatio ->
                                val seekTarget = (newRatio * totalDuration).toLong()
                                exoPlayer.seekTo(seekTarget)
                                currentPosition = seekTarget
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = NeonPurple,
                                activeTrackColor = NeonPurple,
                                inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .testTag("player_seek_bar")
                        )
                    }
                }
            }
        }
    }
}
