package com.example.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.example.data.model.AiThumbnail
import com.example.data.model.ThumbnailRatio
import com.example.data.model.ThumbnailStyle
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ThumbnailEngine {

    fun generateThumbnailBitmap(thumbnail: AiThumbnail): Bitmap {
        val width = thumbnail.ratio.width
        val height = thumbnail.ratio.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background Gradient
        val bgPaint = Paint().apply { isAntiAlias = true }
        when (thumbnail.style) {
            ThumbnailStyle.HIGH_TECH_CYBER -> {
                bgPaint.shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#0F172A"), Color.parseColor("#3B0764"), Color.parseColor("#082F49")),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            ThumbnailStyle.VIRAL_CLICKBAIT -> {
                bgPaint.shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#180326"), Color.parseColor("#090C15"), Color.parseColor("#05070D")),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            ThumbnailStyle.DOCUMENTARY_CINEMATIC -> {
                bgPaint.shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#1C1917"), Color.parseColor("#0C0A09"), Color.parseColor("#292524")),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            ThumbnailStyle.MINIMAL_MODERN -> {
                bgPaint.shader = LinearGradient(
                    0f, 0f, width.toFloat(), 0f,
                    intArrayOf(Color.parseColor("#0A0D18"), Color.parseColor("#161D36")),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Draw Decorative Ambient Glow Circles / Grid
        val glowPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        glowPaint.shader = RadialGradient(
            width * 0.75f, height * 0.35f, width * 0.5f,
            Color.parseColor("#558A55FE"), Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(width * 0.75f, height * 0.35f, width * 0.5f, glowPaint)

        glowPaint.shader = RadialGradient(
            width * 0.2f, height * 0.8f, width * 0.4f,
            Color.parseColor("#4400D4FF"), Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(width * 0.2f, height * 0.8f, width * 0.4f, glowPaint)

        // 3. Optional Vignette
        if (thumbnail.hasVignette) {
            val vignettePaint = Paint().apply {
                isAntiAlias = true
                shader = RadialGradient(
                    width * 0.5f, height * 0.5f, width * 0.7f,
                    Color.TRANSPARENT, Color.parseColor("#AA000000"),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)
        }

        // 4. Draw Glow Border
        if (thumbnail.hasGlowBorder) {
            val borderPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.012f).coerceAtLeast(8f)
                color = when (thumbnail.style) {
                    ThumbnailStyle.VIRAL_CLICKBAIT -> Color.parseColor("#FF0055")
                    ThumbnailStyle.HIGH_TECH_CYBER -> Color.parseColor("#00D4FF")
                    ThumbnailStyle.DOCUMENTARY_CINEMATIC -> Color.parseColor("#EAB308")
                    else -> Color.parseColor("#8A55FE")
                }
            }
            canvas.drawRect(
                borderPaint.strokeWidth / 2, borderPaint.strokeWidth / 2,
                width - borderPaint.strokeWidth / 2, height - borderPaint.strokeWidth / 2,
                borderPaint
            )
        }

        // 5. Draw Badge Box (Top Left or Top Center)
        if (thumbnail.badgeText.isNotBlank()) {
            val badgePaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#FF0055")
                style = Paint.Style.FILL
            }
            val badgeTextPaint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                textSize = (height * 0.045f).coerceAtLeast(24f)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
            }

            val badgeWidth = badgeTextPaint.measureText(thumbnail.badgeText) + (width * 0.04f)
            val badgeHeight = height * 0.07f
            val badgeX = width * 0.08f
            val badgeY = height * 0.12f

            val badgeRect = RectF(badgeX, badgeY, badgeX + badgeWidth, badgeY + badgeHeight)
            canvas.drawRoundRect(badgeRect, 12f, 12f, badgePaint)

            canvas.drawText(
                thumbnail.badgeText,
                badgeX + (width * 0.02f),
                badgeY + (badgeHeight * 0.7f),
                badgeTextPaint
            )
        }

        // 6. Draw Headline Text with heavy stroke / shadow
        val headlinePaint = Paint().apply {
            isAntiAlias = true
            color = runCatching { Color.parseColor(thumbnail.textColorHex) }.getOrDefault(Color.YELLOW)
            textSize = (height * 0.11f).coerceAtLeast(44f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            setShadowLayer(25f, 0f, 12f, Color.BLACK)
        }

        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            textSize = headlinePaint.textSize
            typeface = headlinePaint.typeface
            textAlign = Paint.Align.LEFT
            style = Paint.Style.STROKE
            strokeWidth = (headlinePaint.textSize * 0.12f).coerceAtLeast(6f)
        }

        val textX = width * 0.08f
        val textY = height * thumbnail.textYPositionPercent.coerceIn(0.2f, 0.85f)

        // Split headline if long into 2 lines
        val words = thumbnail.headlineText.split(" ")
        val mid = (words.size / 2).coerceAtLeast(1)
        val line1 = words.take(mid).joinToString(" ")
        val line2 = words.drop(mid).joinToString(" ")

        canvas.drawText(line1, textX, textY, strokePaint)
        canvas.drawText(line1, textX, textY, headlinePaint)

        if (line2.isNotBlank()) {
            val lineSpacing = headlinePaint.textSize * 1.15f
            canvas.drawText(line2, textX, textY + lineSpacing, strokePaint)
            canvas.drawText(line2, textX, textY + lineSpacing, headlinePaint)
        }

        // 7. Draw SubText Banner
        if (thumbnail.subText.isNotBlank()) {
            val subTextPaint = Paint().apply {
                isAntiAlias = true
                color = runCatching { Color.parseColor(thumbnail.secondaryColorHex) }.getOrDefault(Color.WHITE)
                textSize = (height * 0.055f).coerceAtLeast(28f)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
                setShadowLayer(15f, 0f, 6f, Color.BLACK)
            }
            val subY = if (line2.isNotBlank()) textY + (headlinePaint.textSize * 2.3f) else textY + (headlinePaint.textSize * 1.3f)
            canvas.drawText(thumbnail.subText, textX, subY, subTextPaint)
        }

        return bitmap
    }

    fun saveThumbnailToFile(context: Context, thumbnail: AiThumbnail): String? {
        return try {
            val bitmap = generateThumbnailBitmap(thumbnail)
            val thumbDir = File(context.getExternalFilesDir(null), "thumbnails")
            if (!thumbDir.exists()) thumbDir.mkdirs()
            val file = File(thumbDir, "thumb_${thumbnail.id}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
