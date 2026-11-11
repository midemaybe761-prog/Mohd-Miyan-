package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AiThumbnail
import com.example.data.model.ThumbnailRatio
import com.example.data.model.ThumbnailStyle

@Entity(tableName = "thumbnails")
data class ThumbnailEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val headlineText: String,
    val subText: String,
    val ratio: String,
    val style: String,
    val textColorHex: String,
    val secondaryColorHex: String,
    val backgroundColorHex: String,
    val badgeText: String,
    val brightness: Float,
    val contrast: Float,
    val hasVignette: Boolean,
    val hasGlowBorder: Boolean,
    val textYPositionPercent: Float,
    val imageUri: String?,
    val createdAt: Long
) {
    fun toDomain(): AiThumbnail {
        return AiThumbnail(
            id = id,
            projectId = projectId,
            headlineText = headlineText,
            subText = subText,
            ratio = runCatching { ThumbnailRatio.valueOf(ratio) }.getOrDefault(ThumbnailRatio.YOUTUBE_16_9),
            style = runCatching { ThumbnailStyle.valueOf(style) }.getOrDefault(ThumbnailStyle.VIRAL_CLICKBAIT),
            textColorHex = textColorHex,
            secondaryColorHex = secondaryColorHex,
            backgroundColorHex = backgroundColorHex,
            badgeText = badgeText,
            brightness = brightness,
            contrast = contrast,
            hasVignette = hasVignette,
            hasGlowBorder = hasGlowBorder,
            textYPositionPercent = textYPositionPercent,
            imageUri = imageUri,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(thumb: AiThumbnail): ThumbnailEntity {
            return ThumbnailEntity(
                id = thumb.id,
                projectId = thumb.projectId,
                headlineText = thumb.headlineText,
                subText = thumb.subText,
                ratio = thumb.ratio.name,
                style = thumb.style.name,
                textColorHex = thumb.textColorHex,
                secondaryColorHex = thumb.secondaryColorHex,
                backgroundColorHex = thumb.backgroundColorHex,
                badgeText = thumb.badgeText,
                brightness = thumb.brightness,
                contrast = thumb.contrast,
                hasVignette = thumb.hasVignette,
                hasGlowBorder = thumb.hasGlowBorder,
                textYPositionPercent = thumb.textYPositionPercent,
                imageUri = thumb.imageUri,
                createdAt = thumb.createdAt
            )
        }
    }
}
