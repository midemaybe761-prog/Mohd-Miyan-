package com.example.data.model

import java.util.Locale

data class AiVoice(
    val id: String,
    val name: String,
    val language: String,
    val locale: Locale,
    val gender: String,
    val style: String,
    val pitch: Float = 1.0f,
    val sampleText: String
)

object VoiceCatalog {
    val AVAILABLE_VOICES = listOf(
        AiVoice(
            id = "hi_male_aarav",
            name = "Aarav (आरव)",
            language = "Hindi",
            locale = Locale("hi", "IN"),
            gender = "Male",
            style = "Deep & Authoritative Narrator",
            pitch = 0.95f,
            sampleText = "नमस्ते, एआई वीडियो स्टूडियो प्रो में आपका स्वागत है। चलिए एक शानदार वीडियो बनाते हैं।"
        ),
        AiVoice(
            id = "hi_female_priya",
            name = "Priya (प्रिया)",
            language = "Hindi",
            locale = Locale("hi", "IN"),
            gender = "Female",
            style = "Warm & Engaging Storyteller",
            pitch = 1.15f,
            sampleText = "हेलो दोस्तों! इस वीडियो में आज हम कुछ बहुत ही रोमांचक और नया सीखने वाले हैं।"
        ),
        AiVoice(
            id = "en_male_marcus",
            name = "Marcus",
            language = "English",
            locale = Locale.US,
            gender = "Male",
            style = "Cinematic Documentary & Tech",
            pitch = 0.92f,
            sampleText = "Welcome to the future of content creation with AI Video Studio Pro."
        ),
        AiVoice(
            id = "en_female_emma",
            name = "Emma",
            language = "English",
            locale = Locale.US,
            gender = "Female",
            style = "Friendly, Energetic & Crystal Clear",
            pitch = 1.1f,
            sampleText = "Hey everyone! Today we are exploring the top secrets to viral video production."
        ),
        AiVoice(
            id = "en_male_james",
            name = "James (British)",
            language = "English",
            locale = Locale.UK,
            gender = "Male",
            style = "Sophisticated & Calm Narrative",
            pitch = 0.88f,
            sampleText = "In today's documentary, we examine how artificial intelligence is shaping our world."
        ),
        AiVoice(
            id = "hi_male_vikram",
            name = "Vikram (विक्रम)",
            language = "Hindi",
            locale = Locale("hi", "IN"),
            gender = "Male",
            style = "High Energy & Motivational",
            pitch = 1.02f,
            sampleText = "अगर आप अपनी ज़िंदगी में सफलता पाना चाहते हैं, तो यह बात हमेशा याद रखें।"
        )
    )

    fun getVoiceById(id: String): AiVoice {
        return AVAILABLE_VOICES.find { it.id == id } ?: AVAILABLE_VOICES[0]
    }
}
