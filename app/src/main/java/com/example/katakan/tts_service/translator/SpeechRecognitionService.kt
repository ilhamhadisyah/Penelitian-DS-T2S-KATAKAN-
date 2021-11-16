package com.example.katakan.tts_service.translator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.katakan.tts_service.SpeechRecognitionCallback
import com.example.katakan.tts_service.VoiceEngineFactory
import java.util.*

class SpeechRecognitionService(private val speechRecognitionCallback: SpeechRecognitionCallback) :
    VoiceEngineFactory.SpeechConverterInterface {

    private val TAG = SpeechRecognitionService::class.java.name
    private lateinit var sr: SpeechRecognizer
    private var message: String = ""
    private lateinit var appContext: Activity
    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private val locale = Locale("id", "ID")

    override fun initialize(message: String, appContext: Activity): SpeechRecognitionService {
        this.message = message
        this.appContext = appContext
        sr = SpeechRecognizer.createSpeechRecognizer(appContext)
        val listener = CustomRecognitionListener()
        sr.setRecognitionListener(listener)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            message
        )
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        intent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            appContext.packageName
        )
        return this
    }

    override fun startRecognition() {
        sr.startListening(intent)
    }

    internal inner class CustomRecognitionListener : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle) {
            Log.d(TAG, "onReadyForSpeech")
            speechRecognitionCallback.onReadyForSpeech(params)
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d(TAG, "onRmsChanged")
        }

        override fun onBufferReceived(buffer: ByteArray) {
            Log.d(TAG, "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech")
            speechRecognitionCallback.onEndOfSpeech()
        }

        override fun onError(error: Int) {
            Log.e(TAG, "error $error")
            speechRecognitionCallback.onError(getErrorMessage(error))
        }

        override fun onResults(results: Bundle) {
            var translateResults = String()
            val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (data != null) {
                for (result in data) {
                    translateResults += result + "\n"
                }
            }
            speechRecognitionCallback.onResults(translateResults)

        }

        override fun onPartialResults(partialResults: Bundle) {
            Log.d(TAG, "onPartialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            Log.d(TAG, "onEvent $eventType")
        }
    }

    override fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
    }
}
