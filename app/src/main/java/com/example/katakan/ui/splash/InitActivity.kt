package com.example.katakan.ui.splash

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.katakan.databinding.ActivityInitBinding
import com.example.katakan.ui.welcome.WelcomeActivity
import com.example.katakan.utils.PermissionListener
import com.example.katakan.utils.PermissionManager

class InitActivity : AppCompatActivity(), PermissionListener {

    private lateinit var binding: ActivityInitBinding
    private lateinit var alertDialog: AlertDialog
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionManager =
            PermissionManager(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE, this)
        permissionManager.checkPermissions()

    }

    private fun changeLanguageDialog() {
        alertDialog = AlertDialog
            .Builder(this)
            .setTitle("Ganti pengaturan bahasa?")
            .setMessage("Aplikasi ini membutuhkan support Bahasa Indonesia untuk berjalan dengan lancar")
            .setPositiveButton("Buka pengaturan", DialogInterface.OnClickListener { _, _ ->
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivityForResult(intent,1001)

            })
            .setNegativeButton("Batal", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            })
            .setOnDismissListener { finishAffinity() }
            .show()
    }


    private fun goToIntro() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finishAffinity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            1001->{
                recreate()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                val isPermissionsGranted = permissionManager
                    .processPermissionsResult(requestCode, permissions, grantResults)

                if (isPermissionsGranted) {
                    if (resources.configuration.locales.toString().contains("in_ID")) {
                        goToIntro()
                    } else {
                        changeLanguageDialog()
                    }
                } else {
                    finishAffinity()
                }
                return
            }
        }
    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val TAG = "Camera"
        private val REQUIRED_PERMISSIONS =
            listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    }

    override fun onPermissionsAlreadyGranted() {
        goToIntro()
    }

    override fun onPermissionNeeded() {
        toast("Need to give permissions")
    }


}