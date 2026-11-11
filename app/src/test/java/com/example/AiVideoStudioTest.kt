package com.example

import com.example.data.model.CanvasRatio
import com.example.data.model.StudioAppMode
import com.example.data.model.ThumbnailRatio
import com.example.data.model.VideoDuration
import com.example.data.model.VideoFps
import com.example.data.model.VideoQuality
import com.example.data.model.VisualStyle
import com.example.data.model.VoiceCatalog
import com.example.engine.AiScriptAgent
import com.example.engine.ScriptGenre
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiVideoStudioTest {

    @Test
    fun testScriptAgentGeneratesEnglishScenes() {
        val result = AiScriptAgent.generateScript(
            topicPrompt = "Quantum Computing Breakthroughs",
            language = "English",
            genre = ScriptGenre.EDUCATIONAL,
            targetDurationSeconds = 60,
            visualStyle = VisualStyle.CYBERPUNK,
            projectId = "test_proj_1"
        )

        assertNotNull(result)
        assertTrue(result.scenes.isNotEmpty())
        assertTrue(result.hook.isNotBlank())
        assertTrue(result.fullScriptText.contains("HOOK"))
        assertEquals("test_proj_1", result.scenes[0].projectId)
    }

    @Test
    fun testScriptAgentGeneratesHindiScenes() {
        val result = AiScriptAgent.generateScript(
            topicPrompt = "भारतीय इतिहास और विरासत",
            language = "Hindi",
            genre = ScriptGenre.DOCUMENTARY,
            targetDurationSeconds = 30,
            visualStyle = VisualStyle.DOCUMENTARY,
            projectId = "test_proj_hi"
        )

        assertNotNull(result)
        assertTrue(result.scenes.isNotEmpty())
        assertTrue(result.hook.contains("रहस्य") || result.hook.contains("वीडियो"))
    }

    @Test
    fun testVoiceCatalogContainsHindiAndEnglish() {
        val voices = VoiceCatalog.AVAILABLE_VOICES
        assertTrue(voices.any { it.language == "Hindi" && it.gender == "Male" })
        assertTrue(voices.any { it.language == "Hindi" && it.gender == "Female" })
        assertTrue(voices.any { it.language == "English" && it.gender == "Male" })
        assertTrue(voices.any { it.language == "English" && it.gender == "Female" })
    }

    @Test
    fun testCanvasAspectRatios() {
        assertEquals(9, CanvasRatio.VERTICAL_9_16.widthRatio)
        assertEquals(16, CanvasRatio.VERTICAL_9_16.heightRatio)
        assertEquals(16, CanvasRatio.LANDSCAPE_16_9.widthRatio)
        assertEquals(9, CanvasRatio.LANDSCAPE_16_9.heightRatio)
        assertEquals(1, CanvasRatio.SQUARE_1_1.widthRatio)
        assertEquals(1, CanvasRatio.SQUARE_1_1.heightRatio)
    }

    @Test
    fun testDualEngineModes() {
        assertEquals("OFFLINE READY", StudioAppMode.MODE_A_LOCAL.badge)
        assertEquals("AI POWERED", StudioAppMode.MODE_B_AI_CLOUD.badge)
    }
}
