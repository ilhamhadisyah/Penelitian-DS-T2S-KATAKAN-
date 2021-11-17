package com.example.katakan.ui.welcome

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.katakan.R
import com.example.katakan.databinding.ActivityWelcomeBinding
import com.example.katakan.tts_service.TextConverterCallback
import com.example.katakan.tts_service.VoiceEngineFactory
import com.example.katakan.ui.dashboard.MainActivity
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList
import java.util.HashMap

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            if (Build.VERSION.SDK_INT >= 23) {
                marshmallowOrAbove()
            } else {
                setUpView()
            }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun marshmallowOrAbove() {

        val permissionsList = ArrayList<String>()

        if (!isPermissionGranted(permissionsList, Manifest.permission.RECORD_AUDIO))

            if (permissionsList.size > 0) {

                requestPermissions(
                    permissionsList.toTypedArray(),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                )
                return
            }
        setUpView()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun isPermissionGranted(
        permissionsList: MutableList<String>,
        permission: String
    ): Boolean {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            if (!shouldShowRequestPermissionRationale(permission))
                return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.RECORD_AUDIO] = PackageManager.PERMISSION_GRANTED

                for (i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]
                if (perms[Manifest.permission.RECORD_AUDIO] == PackageManager.PERMISSION_GRANTED) {
                    setUpView()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Some Permissions are Denied Exiting App",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
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

    companion object {
        const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }
}