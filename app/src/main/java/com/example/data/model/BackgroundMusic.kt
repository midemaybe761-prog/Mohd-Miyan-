package com.example.data.model

data class BackgroundMusic(
    val id: String,
    val title: String,
    val genre: String,
    val bpm: Int,
    val mood: String,
    val energyLevel: String
)

object MusicCatalog {
    val TRACKS = listOf(
        BackgroundMusic(
            id = "upbeat_cinematic",
            title = "Cinematic Horizon",
            genre = "Orchestral / Modern",
            bpm = 115,
            mood = "Inspiring & Epic",
            energyLevel = "High"
        ),
        BackgroundMusic(
            id = "cyber_pulse",
            title = "Neon Cyber Pulse",
            genre = "Synthwave / Cyberpunk",
            bpm = 124,
            mood = "Futuristic & Tech",
            energyLevel = "Dynamic"
        ),
        BackgroundMusic(
            id = "lofi_chill",
            title = "Midnight Coffee Lo-Fi",
            genre = "Lo-Fi Hip Hop",
            bpm = 82,
            mood = "Relaxed & Study",
            energyLevel = "Low"
        ),
        BackgroundMusic(
            id = "ambient_nature",
            title = "Whispering Pines",
            genre = "Acoustic / Ambient",
            bpm = 75,
            mood = "Peaceful & Documentary",
            energyLevel = "Calm"
        ),
        BackgroundMusic(
            id = "trailer_suspense",
            title = "Apex Thriller Hit",
            genre = "Action / Trailer",
            bpm = 130,
            mood = "Suspense & High Stakes",
            energyLevel = "Intense"
        ),
        BackgroundMusic(
            id = "corporate_motivate",
            title = "Digital Momentum",
            genre = "Corporate Pop",
            bpm = 110,
            mood = "Optimistic & Professional",
            energyLevel = "Medium"
        )
    )

    fun getTrackById(id: String): BackgroundMusic {
        return TRACKS.find { it.id == id } ?: TRACKS[0]
    }
}
