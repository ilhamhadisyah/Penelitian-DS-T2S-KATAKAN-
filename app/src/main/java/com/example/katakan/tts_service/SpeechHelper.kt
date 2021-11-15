package com.example.katakan.tts_service

import android.app.Activity
import android.os.Handler

class SpeechHelper(activity: Activity, callback: SpeechRecognitionCallback) {
    private var speechRecognizerHandler: Handler = Handler(activity.mainLooper)
    private val speechUtil =
        VoiceEngineFactory.speechInstance.with(callback).initialize("", activity)

    private var speechEngine = Runnable {
        kotlin.run { speechUtil.startRecognition() }
    }

    fun startRecognition() {
        speechRecognizerHandler.post(speechEngine)
    }

    fun stopRecognition() {
        speechRecognizerHandler.removeCallbacks(speechEngine)
    }
}