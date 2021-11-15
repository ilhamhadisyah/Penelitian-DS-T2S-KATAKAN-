package com.example.katakan.ui.dashboard

import android.content.ContentValues.TAG
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.katakan.databinding.ActivityMainBinding
import com.example.katakan.R
import com.example.katakan.tts_service.SpeechHelper
import com.example.katakan.tts_service.SpeechRecognitionCallback
import com.example.katakan.tts_service.TextConverterCallback
import com.example.katakan.tts_service.VoiceEngineFactory
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var speechHelper: SpeechHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSTT()
        startCamera()
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
                if (results.contains("ambil")) {
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

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )

            } catch (exc: Exception) {
                Log.e("Camera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }
//
//    private fun takePhoto() {
//        val imageCapture = imageCapture ?: return
//        val photoFile = File(
//            outputDirectory,
//            SimpleDateFormat(
//                FILENAME_FORMAT, Locale.US
//            ).format(System.currentTimeMillis()) + ".jpg"
//        )
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(this),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exc: ImageCaptureException) {
//                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                }
//
//                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                    val savedUri = Uri.fromFile(photoFile)
//                    val msg = "Photo capture succeeded: $savedUri"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, msg)
//                }
//            })
//    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }


}