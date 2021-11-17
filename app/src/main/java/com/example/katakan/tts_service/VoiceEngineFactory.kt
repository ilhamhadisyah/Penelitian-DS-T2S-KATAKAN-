package com.example.katakan.tts_service

import android.app.Activity
import com.example.katakan.tts_service.translator.SpeechRecognitionService
import com.example.katakan.tts_service.translator.TextToSpeechTextConverter

class VoiceEngineFactory private constructor() {
    interface TextConverterInterface {
        fun initialize(message: String, appContext: Activity): TextConverterInterface
        fun getErrorMessage(errorCode: Int): String
    }

    interface SpeechConverterInterface {
        fun initialize(message: String, appContext: Activity): SpeechConverterInterface
        fun getErrorMessage(errorCode: Int): String
        fun startRecognition()
    }

    fun with(textConverterCallback: TextConverterCallback): TextConverterInterface {
        return TextToSpeechTextConverter(textConverterCallback)
    }

    fun with(speechRecognitionCallback: SpeechRecognitionCallback): SpeechConverterInterface {
        return SpeechRecognitionService(speechRecognitionCallback)
    }

    companion object {
        val textInstance = VoiceEngineFactory()
        val speechInstance = VoiceEngineFactory()
    }
}