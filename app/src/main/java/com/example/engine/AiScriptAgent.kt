package com.example.engine

import com.example.data.model.Scene
import com.example.data.model.TransitionType
import com.example.data.model.VisualStyle
import java.util.UUID

enum class ScriptGenre(val label: String, val emoji: String) {
    EDUCATIONAL("Educational & Tech", "🎓"),
    ENTERTAINMENT("Entertainment & Humor", "🎉"),
    STORYTELLING("Storytelling & Drama", "📖"),
    DOCUMENTARY("Documentary & History", "🏛️"),
    COOKING("Cooking & Recipes", "🍳"),
    HEALTH("Health, Fitness & Yoga", "💪"),
    MOTIVATION("Motivational & Growth", "🔥"),
    DANCE("Dance & Music Video", "💃")
}

data class GeneratedScriptResult(
    val hook: String,
    val fullScriptText: String,
    val scenes: List<Scene>,
    val suggestedTitle: String,
    val tags: List<String>
)

object AiScriptAgent {

    fun generateScript(
        topicPrompt: String,
        language: String,
        genre: ScriptGenre,
        targetDurationSeconds: Int,
        visualStyle: VisualStyle,
        projectId: String
    ): GeneratedScriptResult {
        val isHindi = language.contains("Hindi", ignoreCase = true) || topicPrompt.any { it in '\u0900'..'\u097F' }

        val sceneCount = when {
            targetDurationSeconds <= 30 -> 3
            targetDurationSeconds <= 60 -> 4
            targetDurationSeconds <= 120 -> 6
            targetDurationSeconds <= 180 -> 8
            else -> 10
        }
        val durationPerScene = (targetDurationSeconds / sceneCount).coerceAtLeast(4)

        val cleanTopic = if (topicPrompt.isBlank()) "Revolutionizing Digital Creativity" else topicPrompt.trim()

        val hook = if (isHindi) {
            when (genre) {
                ScriptGenre.EDUCATIONAL -> "क्या आप जानते हैं कि यह तकनीक दुनिया को हमेशा के लिए कैसे बदल रही है?"
                ScriptGenre.COOKING -> "सिर्फ 5 मिनट में रेस्टोरेंट जैसा ज़बरदस्त स्वाद, यह सीक्रेट रेसिपी ज़रूर देखें!"
                ScriptGenre.HEALTH -> "रोजाना केवल 10 मिनट में अपने शरीर और ऊर्जा को पूरी तरह बदलें!"
                ScriptGenre.MOTIVATION -> "अगर आपको लगता है कि आप हार रहे हैं, तो यह बात आपकी सोच बदल देगी!"
                ScriptGenre.ENTERTAINMENT -> "रुकिए! जो आप देखने वाले हैं उस पर आपको बिल्कुल यकीन नहीं होगा!"
                else -> "इस वीडियो में जानिए $cleanTopic का सबसे बड़ा रहस्य!"
            }
        } else {
            when (genre) {
                ScriptGenre.EDUCATIONAL -> "Stop scrolling! Here is the mind-blowing truth about $cleanTopic you never knew."
                ScriptGenre.COOKING -> "The one chef secret that changes everything about $cleanTopic in under 5 minutes!"
                ScriptGenre.HEALTH -> "Do this every morning to supercharge your metabolism and mental clarity."
                ScriptGenre.MOTIVATION -> "When you feel like quitting, remember why you started."
                ScriptGenre.ENTERTAINMENT -> "You will not believe what happened next with $cleanTopic!"
                else -> "Here is the ultimate breakdown of $cleanTopic that everyone is talking about."
            }
        }

        val sceneList = mutableListOf<Scene>()

        for (i in 1..sceneCount) {
            val (title, narration, visual, camera, overlay) = if (isHindi) {
                generateHindiScene(i, sceneCount, cleanTopic, genre, visualStyle)
            } else {
                generateEnglishScene(i, sceneCount, cleanTopic, genre, visualStyle)
            }

            val transition = when (i % 4) {
                1 -> TransitionType.FLASH
                2 -> TransitionType.CROSS_DISSOLVE
                3 -> TransitionType.ZOOM_IN
                else -> TransitionType.FADE
            }

            sceneList.add(
                Scene(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    sceneOrder = i,
                    durationSeconds = durationPerScene,
                    title = title,
                    visualPrompt = visual,
                    narration = narration,
                    cameraAngle = camera,
                    transition = transition,
                    textOverlay = overlay,
                    subtitleText = narration,
                    isGenerated = true
                )
            )
        }

        val fullScript = buildString {
            append("HOOK: $hook\n\n")
            sceneList.forEachIndexed { index, scene ->
                append("SCENE ${index + 1} (${scene.title}):\n")
                append("Visual: ${scene.visualPrompt}\n")
                append("Narration: ${scene.narration}\n")
                append("Overlay: ${scene.textOverlay}\n\n")
            }
        }

        val title = if (isHindi) {
            "$cleanTopic: संपूर्ण गाइड 2026"
        } else {
            "$cleanTopic | The Definitive Guide 2026"
        }

        val tags = listOf(
            cleanTopic.take(15).replace(" ", ""),
            "AIVideoStudio",
            genre.name.lowercase(),
            if (isHindi) "HindiCreator" else "TechNews",
            "Trending2026"
        )

        return GeneratedScriptResult(
            hook = hook,
            fullScriptText = fullScript,
            scenes = sceneList,
            suggestedTitle = title,
            tags = tags
        )
    }

    private fun generateHindiScene(
        order: Int,
        total: Int,
        topic: String,
        genre: ScriptGenre,
        style: VisualStyle
    ): SceneTemplate {
        return when (order) {
            1 -> SceneTemplate(
                title = "दमदार शुरुआत (Hook & Intro)",
                narration = "नमस्ते दोस्तों! आज हम बात कर रहे हैं $topic के बारे में, जो आज हर जगह छाया हुआ है।",
                visual = "अति-सुंदर और आकर्षक दृश्य जिसमें $topic को आधुनिक ${style.label} शैली में प्रस्तुत किया गया है।",
                camera = "तेज़ ज़ूम इन और सिनेमैटिक फोकस",
                overlay = "ज़रूर देखें! ⚡"
            )
            2 -> SceneTemplate(
                title = "मुख्य विषय का विश्लेषण (Core Breakdown)",
                narration = "पहली और सबसे महत्वपूर्ण बात यह है कि इसका प्रभाव हमारी रोज़मर्रा की ज़िंदगी पर बहुत गहरा है।",
                visual = "विस्तृत ग्राफ़िक्स और जीवंत वातावरण में $topic के प्रमुख बिंदुओं का प्रदर्शन।",
                camera = "पैनोरमिक पैनिंग शॉट, धीमी गति",
                overlay = "पहला नियम 🎯"
            )
            3 -> SceneTemplate(
                title = "गहन विश्लेषण और उदाहरण (In-Depth Insight)",
                narration = "विशेषज्ञों का मानना है कि सही जानकारी और सही रणनीति से आप इसमें अभूतपूर्व सफलता प्राप्त कर सकते हैं।",
                visual = "सफलता और प्रगति का प्रेरक चित्रण, ${style.promptModifier}।",
                camera = "क्लोज़-अप एंगल, ड्रामैटिक शैलो डेप्थ",
                overlay = "सफलता का राज़ 🔥"
            )
            else -> SceneTemplate(
                title = if (order == total) "निष्कर्ष और कॉल टू एक्शन (Call To Action)" else "चरण $order (Step $order)",
                narration = if (order == total)
                    "अगर आपको यह वीडियो पसंद आया तो तुरंत लाइक और सब्सक्राइब करें! मिलते हैं अगले रोमांचक वीडियो में।"
                else
                    "इसके साथ ही यह ध्यान रखना बहुत ज़रूरी है कि निरंतरता और धैर्य ही आपको आगे ले जाएंगे।",
                visual = "प्रेरक और भव्य अंतिम दृश्य, ब्रांड लोगो और सब्सक्राइब एनिमेशन के साथ।",
                camera = "स्लो मोशन पुल-बैक शॉट",
                overlay = if (order == total) "LIKE & SUBSCRIBE 🔔" else "आगे बढ़ें!"
            )
        }
    }

    private fun generateEnglishScene(
        order: Int,
        total: Int,
        topic: String,
        genre: ScriptGenre,
        style: VisualStyle
    ): SceneTemplate {
        return when (order) {
            1 -> SceneTemplate(
                title = "Viral Hook & Introduction",
                narration = "Here is everything you need to know about $topic, and why it is transforming the industry right now.",
                visual = "Breathtaking visual introduction of $topic with ${style.promptModifier}, volumetric lights.",
                camera = "Dynamic forward push-in with motion blur",
                overlay = "PAY ATTENTION ⚡"
            )
            2 -> SceneTemplate(
                title = "The Core Breakdown",
                narration = "The secret lies in the underlying architecture that most creators completely overlook.",
                visual = "Futuristic neon schematic diagram and realistic human subject interacting with $topic.",
                camera = "Cinematic 360 orbital tracking",
                overlay = "KEY STEP #1 🎯"
            )
            3 -> SceneTemplate(
                title = "Practical Demonstration",
                narration = "By applying this exact framework, you immediately multiply your output and visual impact.",
                visual = "High-energy montage sequence showing real-world application of $topic, ${style.promptModifier}.",
                camera = "Low angle hero shot with dramatic lighting",
                overlay = "PRO STRATEGY 🔥"
            )
            else -> SceneTemplate(
                title = if (order == total) "Final Verdict & Outro" else "Execution Step $order",
                narration = if (order == total)
                    "Hit like, subscribe for more daily AI video studio updates, and drop your questions in the comments below!"
                else
                    "Combine these techniques consistently, and you will stay miles ahead of the competition.",
                visual = "Glorious wide landscape finale shot with glowing golden hour backlight and studio badge.",
                camera = "Slow upward crane tilt to sky",
                overlay = if (order == total) "SUBSCRIBE FOR MORE 🚀" else "LEVEL UP NOW!"
            )
        }
    }

    private data class SceneTemplate(
        val title: String,
        val narration: String,
        val visual: String,
        val camera: String,
        val overlay: String
    )
}
