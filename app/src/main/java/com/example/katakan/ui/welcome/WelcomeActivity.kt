package com.example.katakan.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.katakan.R
import com.example.katakan.databinding.ActivityWelcomeBinding
import com.example.katakan.tts_service.TextConverterCallback
import com.example.katakan.tts_service.VoiceEngineFactory
import com.example.katakan.ui.dashboard.MainActivity
import com.google.android.material.snackbar.Snackbar

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpView()
    }


    private fun setUpView() {
        speak(getString(R.string.halo_katakan_ambil_untuk_mengetahui_objek_di_depanmu))
    }

    private fun speak(text: String) {
        if (text.isNotEmpty()) {
            VoiceEngineFactory.textInstance.with(object : TextConverterCallback {
                override fun onStart(result: String) {}
                override fun onDone(utteranceId: String) {
                    startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                    finishAffinity()
                }

                override fun onError(message: String) {}
            }).initialize(text, this@WelcomeActivity)
        } else {
            Snackbar.make(binding.root, "Please enter some text to speak", Snackbar.LENGTH_LONG)
                .show()
        }
    }

}