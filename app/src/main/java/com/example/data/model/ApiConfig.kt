package com.example.data.model

enum class StudioAppMode(val label: String, val badge: String, val description: String) {
    MODE_A_LOCAL(
        label = "Mode A: Local Engine (Offline / No API)",
        badge = "OFFLINE READY",
        description = "Uses Android on-device media synthesis, Canvas rendering, native TTS voices, and local MP4 generation. 100% operational with zero keys required."
    ),
    MODE_B_AI_CLOUD(
        label = "Mode B: AI Cloud Engine (API Connected)",
        badge = "AI POWERED",
        description = "Uses external AI models (Gemini / OpenAI / Runway / ElevenLabs / Node.js backend) for advanced video generation, multi-scene video synthesis, and cloud processing."
    )
}

enum class AiProviderType(val providerName: String, val category: String) {
    NODE_BACKEND("Local / Remote Node.js Express Backend", "All-in-One"),
    GEMINI_API("Google Gemini Pro / Flash", "LLM & Scriptwriting"),
    OPENAI_GPT("OpenAI GPT-4o / Sora", "Script & Video"),
    RUNWAY_GEN3("Runway Gen-3 Alpha", "Video Generation"),
    PIKA_LABS("Pika 2.0", "Video Generation"),
    ELEVEN_LABS("ElevenLabs AI Voices", "Neural Voice Over")
}

data class ApiConfig(
    val activeMode: StudioAppMode = StudioAppMode.MODE_A_LOCAL,
    val selectedProvider: AiProviderType = AiProviderType.NODE_BACKEND,
    val backendBaseUrl: String = "http://10.0.2.2:5000",
    val apiKey: String = "",
    val isConnected: Boolean = false,
    val lastTestResult: String = "Local mode active: Full rendering & editing enabled",
    val maxDurationLimitSeconds: Int = 600,
    val allowedResolutions: List<String> = listOf("480p", "720p", "1080p", "2K", "4K"),
    val subscriptionTier: String = "Pro Studio (Unlimited Local + AI Ready)"
)
