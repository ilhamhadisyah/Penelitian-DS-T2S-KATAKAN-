package com.example.katakan.ui.dashboard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.katakan.databinding.ActivityMainBinding
import com.example.katakan.R
import com.example.katakan.camera_service.CameraService
import com.example.katakan.data.network.retrofit.ApiHelper
import com.example.katakan.data.network.retrofit.RetrofitBuilder
import com.example.katakan.tts_service.SpeechHelper
import com.example.katakan.tts_service.SpeechRecognitionCallback
import com.example.katakan.tts_service.TextConverterCallback
import com.example.katakan.tts_service.VoiceEngineFactory
import com.example.katakan.viewmodel.MainViewModel
import com.example.katakan.viewmodel.viewmodelfactory.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.io.*
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.example.katakan.data.network.Status
import com.example.katakan.utils.ImageUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var speechHelper: SpeechHelper
    private lateinit var cameraService: CameraService
    private lateinit var mainViewModel: MainViewModel
    private lateinit var imageUtils: ImageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setUpViewModel()
        initSTT()
        speechHelper.startRecognition()
        binding.lottieAnimationView.setOnClickListener {
            speechHelper.startRecognition()
        }
    }
    private fun initView() {
        binding.processing.visibility = View.GONE
        imageUtils = ImageUtils(this)
        cameraService = CameraService(this, this)
        cameraService.setSurfaceProvider(binding.viewFinder)
    }


    private fun setUpViewModel() {
        mainViewModel =
            ViewModelProviders.of(this, ViewModelFactory(ApiHelper(RetrofitBuilder.apiService)))
                .get(MainViewModel::class.java)
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
                    capture()
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

    private fun capture() {
        val bitmap = binding.viewFinder.bitmap
        val out = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, out)
        val scaledBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
        binding.previewTest.setImageBitmap(scaledBitmap)
        val requestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), imageUtils.createTempFile(scaledBitmap!!)!!)
        fetchCaption(requestBody)
    }

    private fun fetchCaption(body: RequestBody) {
        mainViewModel.getCaption(body).observe(this, Observer {
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        binding.processing.visibility = View.GONE
                        binding.lottieAnimationView.visibility = View.VISIBLE
                        val capt = resources.data?.result.toString()
                        Log.d("success", capt)
                        binding.caption.text = capt
                        speak(capt)

                    }
                    Status.ERROR -> {
                        Log.e("error", resources.message!!)
                        binding.caption.text = resources.message
                    }
                    Status.LOADING -> {
                        binding.processing.visibility = View.VISIBLE
                        binding.lottieAnimationView.visibility = View.GONE
                    }
                }
            }
        })
    }
}