package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CanvasRatio
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoDuration
import com.example.data.model.VideoFps
import com.example.data.model.VideoProject
import com.example.data.model.VideoQuality
import com.example.data.model.VisualStyle

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val prompt: String,
    val fullScript: String,
    val scriptLanguage: String,
    val durationSeconds: Int,
    val canvasRatio: String,
    val quality: String,
    val fps: String,
    val visualStyle: String,
    val voiceId: String,
    val voiceSpeed: Float,
    val voiceVolume: Float,
    val musicTrackId: String,
    val musicVolume: Float,
    val subtitlesEnabled: Boolean,
    val subtitleLanguage: String,
    val subtitlePosition: String,
    val status: String,
    val progressPercent: Int,
    val exportPath: String?,
    val thumbnailUri: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): VideoProject {
        return VideoProject(
            id = id,
            title = title,
            prompt = prompt,
            fullScript = fullScript,
            scriptLanguage = scriptLanguage,
            durationSeconds = durationSeconds,
            canvasRatio = runCatching { CanvasRatio.valueOf(canvasRatio) }.getOrDefault(CanvasRatio.VERTICAL_9_16),
            quality = runCatching { VideoQuality.valueOf(quality) }.getOrDefault(VideoQuality.HD_720P),
            fps = runCatching { VideoFps.valueOf(fps) }.getOrDefault(VideoFps.FPS_30),
            visualStyle = runCatching { VisualStyle.valueOf(visualStyle) }.getOrDefault(VisualStyle.CINEMATIC),
            voiceId = voiceId,
            voiceSpeed = voiceSpeed,
            voiceVolume = voiceVolume,
            musicTrackId = musicTrackId,
            musicVolume = musicVolume,
            subtitlesEnabled = subtitlesEnabled,
            subtitleLanguage = subtitleLanguage,
            subtitlePosition = subtitlePosition,
            status = runCatching { ProjectStatus.valueOf(status) }.getOrDefault(ProjectStatus.DRAFT),
            progressPercent = progressPercent,
            exportPath = exportPath,
            thumbnailUri = thumbnailUri,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(project: VideoProject): ProjectEntity {
            return ProjectEntity(
                id = project.id,
                title = project.title,
                prompt = project.prompt,
                fullScript = project.fullScript,
                scriptLanguage = project.scriptLanguage,
                durationSeconds = project.durationSeconds,
                canvasRatio = project.canvasRatio.name,
                quality = project.quality.name,
                fps = project.fps.name,
                visualStyle = project.visualStyle.name,
                voiceId = project.voiceId,
                voiceSpeed = project.voiceSpeed,
                voiceVolume = project.voiceVolume,
                musicTrackId = project.musicTrackId,
                musicVolume = project.musicVolume,
                subtitlesEnabled = project.subtitlesEnabled,
                subtitleLanguage = project.subtitleLanguage,
                subtitlePosition = project.subtitlePosition,
                status = project.status.name,
                progressPercent = project.progressPercent,
                exportPath = project.exportPath,
                thumbnailUri = project.thumbnailUri,
                createdAt = project.createdAt,
                updatedAt = project.updatedAt
            )
        }
    }
}
