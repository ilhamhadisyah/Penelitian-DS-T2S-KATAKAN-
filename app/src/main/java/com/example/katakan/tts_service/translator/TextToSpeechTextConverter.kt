package com.example.katakan.tts_service.translator

import android.app.Activity
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.katakan.tts_service.TextConverterCallback
import com.example.katakan.tts_service.VoiceEngineFactory
import java.util.*

class TextToSpeechTextConverter(private val textConverterCallback: TextConverterCallback) :
    VoiceEngineFactory.TextConverterInterface {

    private val TAG = "TTS"
    private var textToSpeech: TextToSpeech? = null
    override fun initialize(
        message: String,
        appContext: Activity
    ): VoiceEngineFactory.TextConverterInterface {

        textToSpeech = TextToSpeech(appContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                val locale = Locale("id", "ID")
                textToSpeech!!.language = locale
                textToSpeech!!.setPitch(1.3f)
                textToSpeech!!.setSpeechRate(1f)
                textToSpeech!!.language = locale

                startSTT(message)

            } else {
                textConverterCallback.onError("Failed to initialize TTS engine")
            }
        })
        return this
    }

    private fun startSTT(text: String) {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"

        textToSpeech!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String) {
                Log.e(TAG, "started speaking root")
                textConverterCallback.onStart(utteranceId)
            }

            override fun onError(utteranceId: String) {
                Log.e(TAG, "error speaking root")
                textConverterCallback.onError("Some Error Occurred $utteranceId")
            }

            override fun onDone(utteranceId: String) {
                Log.e(TAG, "done speaking root")
                textConverterCallback.onDone(utteranceId)
                finish()
            }
        })
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    private fun finish() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
    }

    override fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            TextToSpeech.ERROR -> "Generic error"
            TextToSpeech.ERROR_INVALID_REQUEST -> "Invalid user request"
            TextToSpeech.ERROR_NOT_INSTALLED_YET -> "Insufficient download of the voice data"
            TextToSpeech.ERROR_NETWORK -> "Network error"
            TextToSpeech.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            TextToSpeech.ERROR_OUTPUT -> "Failure in to the output (audio device or a file)"
            TextToSpeech.ERROR_SYNTHESIS -> "Failure of a TTS engine to synthesize the given input."
            TextToSpeech.ERROR_SERVICE -> "error from server"
            else -> "Didn't understand, please try again."
        }
    }


}
