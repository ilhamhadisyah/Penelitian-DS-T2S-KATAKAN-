package com.example.katakan.camera_service

import android.app.Activity
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.lang.Exception

class CameraService constructor(activity: Activity, lifecycleOwner: LifecycleOwner) {
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
    private lateinit var previewView: PreviewView

    init {
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview
                )
            } catch (ex: Exception) {
                Log.d("Camera", "Use case binding failed", ex)
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    fun setSurfaceProvider(viewFinder: PreviewView) {
        previewView = viewFinder
    }
}