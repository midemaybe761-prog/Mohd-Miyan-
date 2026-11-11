package com.example.data.model

data class GenerationJob(
    val jobId: String,
    val projectTitle: String,
    val provider: String,
    val status: String,
    val progress: Int,
    val startedAt: Long,
    val durationSeconds: Int,
    val errorDetails: String? = null
)

data class SystemLog(
    val id: String,
    val timestamp: Long,
    val level: String, // "INFO", "WARN", "ERROR"
    val module: String,
    val message: String
)

data class AdminMetrics(
    val totalProjects: Int = 12,
    val completedVideos: Int = 8,
    val totalMinutesRendered: Double = 34.5,
    val storageUsedMb: Double = 142.8,
    val apiCallsToday: Int = 45,
    val currentTier: String = "Enterprise Pro License",
    val activeJobsCount: Int = 1,
    val recentJobs: List<GenerationJob> = listOf(
        GenerationJob(
            jobId = "job_8921a",
            projectTitle = "Future of Quantum AI 2026",
            provider = "Local Media Engine (1080p)",
            status = "Completed",
            progress = 100,
            startedAt = System.currentTimeMillis() - 120000,
            durationSeconds = 60
        ),
        GenerationJob(
            jobId = "job_8922b",
            projectTitle = "Hindi Cooking Secrets Shorts",
            provider = "Local Media Engine (9:16)",
            status = "Completed",
            progress = 100,
            startedAt = System.currentTimeMillis() - 600000,
            durationSeconds = 30
        )
    ),
    val systemLogs: List<SystemLog> = listOf(
        SystemLog(
            id = "log_1",
            timestamp = System.currentTimeMillis() - 150000,
            level = "INFO",
            module = "MediaCodec",
            message = "Hardware H.264 video encoder initialized at 1920x1080 @ 30 FPS"
        ),
        SystemLog(
            id = "log_2",
            timestamp = System.currentTimeMillis() - 145000,
            level = "INFO",
            module = "AudioTrack",
            message = "Stereo audio mixer synchronized with scene narration track"
        ),
        SystemLog(
            id = "log_3",
            timestamp = System.currentTimeMillis() - 120000,
            level = "INFO",
            module = "ExportEngine",
            message = "Successfully exported MP4 video to device gallery with verified audio-visual sync"
        )
    )
)
