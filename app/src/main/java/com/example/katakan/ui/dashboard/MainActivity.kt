package com.example.katakan.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.katakan.databinding.ActivityMainBinding
import com.example.katakan.R
import com.example.katakan.camera_service.CameraService
import com.example.katakan.tts_service.SpeechHelper
import com.example.katakan.tts_service.SpeechRecognitionCallback
import com.example.katakan.tts_service.TextConverterCallback
import com.example.katakan.tts_service.VoiceEngineFactory
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var speechHelper: SpeechHelper

    private lateinit var cameraService: CameraService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSTT()
        cameraService = CameraService(this, this)
        cameraService.setSurfaceProvider(binding.viewFinder)
        speechHelper.startRecognition()
        binding.lottieAnimationView.setOnClickListener {
            speechHelper.startRecognition()
        }
    }

    private fun initSTT() {
        speechHelper = SpeechHelper(this, object : SpeechRecognitionCallback {
            override fun onReadyForSpeech(params: Bundle) {
                binding.caption.text = getString(R.string.init)
            }

            override fun onEndOfSpeech() {}
            override fun onError(error: String) {
                binding.caption.text = error
                speak("Suara tidak jelas, mohon di ulang")
            }

            override fun onResults(results: String) {
                if (results.contains("katakan")) {
                    //do capture
                    binding.caption.text = results
                    takePict()
                } else {
                    speak("Perintah tidak dikenali, mohon di ulang")
                }
            }
        })
    }

    private fun speak(text: String) {
        speechHelper.stopRecognition()
        if (text.isNotEmpty()) {
            VoiceEngineFactory.textInstance.with(object : TextConverterCallback {
                override fun onStart(result: String) {}
                override fun onDone(utteranceId: String) {
                    speechHelper.startRecognition()
                }

                override fun onError(message: String) {}
            }).initialize(text, this)
        } else {
            Snackbar.make(binding.root, "Please enter some text to speak", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun takePict() {
        val bitmap = binding.viewFinder.bitmap
        binding.previewTest.setImageBitmap(bitmap)
    }
}