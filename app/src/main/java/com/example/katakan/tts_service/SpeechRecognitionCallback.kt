package com.example.katakan.tts_service

import android.os.Bundle

interface SpeechRecognitionCallback {
    fun onReadyForSpeech(params: Bundle)
    fun onEndOfSpeech()
    fun onError(error: String)
    fun onResults(results: String)
}