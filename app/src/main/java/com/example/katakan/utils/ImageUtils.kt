package com.example.katakan.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageUtils(private val context: Context) {

    fun createTempFile(bitmap: Bitmap): File? {
        val file = File(
           context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            System.currentTimeMillis().toString() + "_image.jpeg"
        )
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos)
        val bitmapData = bos.toByteArray()
        try {
            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }
}