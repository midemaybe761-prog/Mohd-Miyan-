package com.example.engine

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.AiVoice
import com.example.data.model.VoiceCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.UUID

class VoiceSynthesisEngine(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentPlayingVoiceId = MutableStateFlow<String?>(null)
    val currentPlayingVoiceId: StateFlow<String?> = _currentPlayingVoiceId.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentPlayingVoiceId.value = null
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentPlayingVoiceId.value = null
                }
            })
        } else {
            Log.e("VoiceEngine", "TTS initialization failed: status=$status")
        }
    }

    fun previewVoice(voice: AiVoice, speed: Float = 1.0f) {
        if (!isInitialized || tts == null) return

        tts?.stop()
        tts?.language = voice.locale
        tts?.setPitch(voice.pitch)
        tts?.setSpeechRate(speed.coerceIn(0.5f, 2.0f))

        _currentPlayingVoiceId.value = voice.id
        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        tts?.speak(voice.sampleText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun speakText(text: String, voiceId: String, speed: Float = 1.0f) {
        if (!isInitialized || tts == null || text.isBlank()) return

        val voice = VoiceCatalog.getVoiceById(voiceId)
        tts?.stop()
        tts?.language = voice.locale
        tts?.setPitch(voice.pitch)
        tts?.setSpeechRate(speed.coerceIn(0.5f, 2.0f))

        _currentPlayingVoiceId.value = voice.id
        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _currentPlayingVoiceId.value = null
    }

    fun synthesizeToFile(
        text: String,
        voiceId: String,
        speed: Float,
        outputFile: File,
        onComplete: (Boolean) -> Unit
    ) {
        if (!isInitialized || tts == null || text.isBlank()) {
            onComplete(false)
            return
        }

        val voice = VoiceCatalog.getVoiceById(voiceId)
        tts?.language = voice.locale
        tts?.setPitch(voice.pitch)
        tts?.setSpeechRate(speed.coerceIn(0.5f, 2.0f))

        val utteranceId = "synth_" + UUID.randomUUID().toString()
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        val tempListener = object : UtteranceProgressListener() {
            override fun onStart(id: String?) {}
            override fun onDone(id: String?) {
                if (id == utteranceId) {
                    onComplete(true)
                }
            }
            @Deprecated("Deprecated in Java")
            override fun onError(id: String?) {
                if (id == utteranceId) {
                    onComplete(false)
                }
            }
        }
        tts?.setOnUtteranceProgressListener(tempListener)

        val result = tts?.synthesizeToFile(text, params, outputFile, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            onComplete(false)
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
