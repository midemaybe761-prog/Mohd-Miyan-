package com.example.data.model

import java.util.UUID

enum class CanvasRatio(val label: String, val widthRatio: Int, val heightRatio: Int, val description: String) {
    VERTICAL_9_16("9:16 Vertical", 9, 16, "YouTube Shorts, TikTok, Instagram Reels"),
    LANDSCAPE_16_9("16:9 Landscape", 16, 9, "YouTube Long Videos, TV, Presentation"),
    SQUARE_1_1("1:1 Square", 1, 1, "Instagram & Facebook Feed Posts"),
    PORTRAIT_4_5("4:5 Portrait", 4, 5, "Instagram Carousel & Social Feed"),
    CUSTOM("Custom", 16, 9, "Configurable Width & Height")
}

enum class VideoQuality(val label: String, val width: Int, val height: Int) {
    SD_480P("480p SD", 854, 480),
    HD_720P("720p HD", 1280, 720),
    FHD_1080P("1080p Full HD", 1920, 1080),
    QHD_2K("2K QHD", 2560, 1440),
    UHD_4K("4K Ultra HD", 3840, 2160)
}

enum class VideoFps(val label: String, val fps: Int) {
    FPS_24("24 FPS (Cinematic)", 24),
    FPS_30("30 FPS (Standard)", 30),
    FPS_60("60 FPS (Ultra Smooth)", 60)
}

enum class VideoDuration(val label: String, val seconds: Int) {
    SEC_30("30 Seconds", 30),
    MIN_1("1 Minute", 60),
    MIN_2("2 Minutes", 120),
    MIN_3("3 Minutes", 180),
    MIN_5("5 Minutes", 300),
    MIN_8("8 Minutes", 480),
    MIN_10("10 Minutes", 600),
    CUSTOM("Custom", 45)
}

enum class VisualStyle(val label: String, val emoji: String, val promptModifier: String) {
    CINEMATIC("Cinematic 8K", "🎬", "hyper-detailed cinematic 8K, photorealistic, anamorphic lens, dramatic lighting"),
    ANIME("Anime / Manga", "🎨", "modern vibrant anime studio art, makoto shinkai aesthetic, gorgeous colors"),
    PIXAR_3D("3D Animation", "🧸", "cute stylized 3D animation, Pixar and Disney render style, soft volumetrics"),
    CYBERPUNK("Cyberpunk Sci-Fi", "⚡", "neon cyberpunk night city, futuristic holographic glow, high-tech reflections"),
    DOCUMENTARY("Documentary", "🌍", "National Geographic authentic documentary footage, natural lighting, high dynamic range"),
    VINTAGE("Vintage Film", "🎞️", "retro 35mm film grain, nostalgic 1980s color grade, organic analog textures"),
    MINIMALIST("Motion Graphics", "📐", "clean modern minimalist motion graphics, sleek vector gradients, typography focus")
}

enum class ProjectStatus {
    DRAFT,
    GENERATING_SCRIPT,
    GENERATING_VIDEO,
    READY_TO_RENDER,
    RENDERING,
    COMPLETED,
    FAILED
}

enum class GenerationStage(val label: String, val stepIndex: Int, val totalSteps: Int) {
    IDLE("Ready", 0, 5),
    PREPARING_SCENES("Preparing scenes & script breakdown...", 1, 5),
    GENERATING_VIDEO("Generating visual scenes & moving AI footage...", 2, 5),
    PROCESSING_AUDIO("Processing voice narration & background audio...", 3, 5),
    COMBINING_SCENES("Combining scenes & motion transitions...", 4, 5),
    RENDERING_FINAL_VIDEO("Rendering final hardware-accelerated MP4...", 5, 5),
    COMPLETED("Completed! Video ready to play.", 5, 5),
    ERROR("Video generation failed.", 0, 5)
}

data class VideoProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Untitled AI Video",
    val prompt: String = "",
    val fullScript: String = "",
    val scriptLanguage: String = "English",
    val durationSeconds: Int = 60,
    val canvasRatio: CanvasRatio = CanvasRatio.VERTICAL_9_16,
    val quality: VideoQuality = VideoQuality.HD_720P,
    val fps: VideoFps = VideoFps.FPS_30,
    val visualStyle: VisualStyle = VisualStyle.CINEMATIC,
    val voiceId: String = "hi_male_aarav",
    val voiceSpeed: Float = 1.0f,
    val voiceVolume: Float = 1.0f,
    val musicTrackId: String = "upbeat_cinematic",
    val musicVolume: Float = 0.35f,
    val subtitlesEnabled: Boolean = true,
    val subtitleLanguage: String = "English",
    val subtitlePosition: String = "Bottom",
    val status: ProjectStatus = ProjectStatus.DRAFT,
    val generationStage: GenerationStage = GenerationStage.IDLE,
    val progressPercent: Int = 0,
    val exportPath: String? = null,
    val videoUrl: String? = null,
    val thumbnailUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
