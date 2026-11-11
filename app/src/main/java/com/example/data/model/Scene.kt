package com.example.data.model

import java.util.UUID

enum class TransitionType(val label: String, val emoji: String, val durationSec: Float = 0.5f) {
    NONE("Cut", "✂️", 0.0f),
    FADE("Fade to Black", "🌑", 0.6f),
    CROSS_DISSOLVE("Crossfade", "✨", 0.7f),
    SLIDE_LEFT("Slide Left", "⬅️", 0.5f),
    SLIDE_RIGHT("Slide Right", "➡️", 0.5f),
    SLIDE_UP("Slide Up", "⬆️", 0.5f),
    SLIDE_DOWN("Slide Down", "⬇️", 0.5f),
    ZOOM_IN("Zoom In", "🔍", 0.5f),
    FLASH("White Flash", "⚡", 0.35f),
    WIPE_RIGHT("Wipe", "📐", 0.55f),
    CIRCLE_CROP("Circle Iris", "⭕", 0.6f)
}

data class Scene(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val sceneOrder: Int,
    val durationSeconds: Int = 5,
    val title: String = "Scene $sceneOrder",
    val visualPrompt: String = "",
    val narration: String = "",
    val cameraAngle: String = "Wide Shot, Slow Pan",
    val transition: TransitionType = TransitionType.FADE,
    val textOverlay: String = "",
    val subtitleText: String = "",
    val speedMultiplier: Float = 1.0f,
    val filterEffect: String = "Normal",
    val imageUrl: String? = null,
    val videoClipUrl: String? = null,
    val isGenerated: Boolean = false
)
