package com.example.data.model

import java.util.UUID

enum class ThumbnailRatio(val label: String, val width: Int, val height: Int) {
    YOUTUBE_16_9("YouTube Thumbnail (16:9)", 1280, 720),
    SHORTS_9_16("Shorts / Reels (9:16)", 720, 1280),
    INSTAGRAM_1_1("Instagram Post (1:1)", 1080, 1080),
    CUSTOM("Custom", 1280, 720)
}

enum class ThumbnailStyle(val label: String) {
    VIRAL_CLICKBAIT("Viral Creator (Bold Neon Yellow + Red Border)"),
    HIGH_TECH_CYBER("Cyberpunk Glow (Cyan & Violet Gradient)"),
    MINIMAL_MODERN("Apple Minimal (Clean Serif & Dark Slate)"),
    DOCUMENTARY_CINEMATIC("Cinematic Letterbox (Gold Accents & Vignette)")
}

data class AiThumbnail(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val headlineText: String = "DON'T MISS THIS!",
    val subText: String = "Complete Guide 2026",
    val ratio: ThumbnailRatio = ThumbnailRatio.YOUTUBE_16_9,
    val style: ThumbnailStyle = ThumbnailStyle.VIRAL_CLICKBAIT,
    val textColorHex: String = "#FFDE00",
    val secondaryColorHex: String = "#FFFFFF",
    val backgroundColorHex: String = "#0A0D18",
    val badgeText: String = "NEW ⚡",
    val brightness: Float = 1.0f,
    val contrast: Float = 1.1f,
    val hasVignette: Boolean = true,
    val hasGlowBorder: Boolean = true,
    val textYPositionPercent: Float = 0.5f,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
