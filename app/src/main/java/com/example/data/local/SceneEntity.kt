package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.Scene
import com.example.data.model.TransitionType

@Entity(
    tableName = "scenes",
    indices = [Index(value = ["projectId"])]
)
data class SceneEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val sceneOrder: Int,
    val durationSeconds: Int,
    val title: String,
    val visualPrompt: String,
    val narration: String,
    val cameraAngle: String,
    val transition: String,
    val textOverlay: String,
    val subtitleText: String,
    val speedMultiplier: Float,
    val filterEffect: String,
    val imageUrl: String?,
    val videoClipUrl: String?,
    val isGenerated: Boolean
) {
    fun toDomain(): Scene {
        return Scene(
            id = id,
            projectId = projectId,
            sceneOrder = sceneOrder,
            durationSeconds = durationSeconds,
            title = title,
            visualPrompt = visualPrompt,
            narration = narration,
            cameraAngle = cameraAngle,
            transition = runCatching { TransitionType.valueOf(transition) }.getOrDefault(TransitionType.FADE),
            textOverlay = textOverlay,
            subtitleText = subtitleText,
            speedMultiplier = speedMultiplier,
            filterEffect = filterEffect,
            imageUrl = imageUrl,
            videoClipUrl = videoClipUrl,
            isGenerated = isGenerated
        )
    }

    companion object {
        fun fromDomain(scene: Scene): SceneEntity {
            return SceneEntity(
                id = scene.id,
                projectId = scene.projectId,
                sceneOrder = scene.sceneOrder,
                durationSeconds = scene.durationSeconds,
                title = scene.title,
                visualPrompt = scene.visualPrompt,
                narration = scene.narration,
                cameraAngle = scene.cameraAngle,
                transition = scene.transition.name,
                textOverlay = scene.textOverlay,
                subtitleText = scene.subtitleText,
                speedMultiplier = scene.speedMultiplier,
                filterEffect = scene.filterEffect,
                imageUrl = scene.imageUrl,
                videoClipUrl = scene.videoClipUrl,
                isGenerated = scene.isGenerated
            )
        }
    }
}
