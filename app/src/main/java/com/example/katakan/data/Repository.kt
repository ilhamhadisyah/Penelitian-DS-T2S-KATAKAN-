package com.example.katakan.data

import com.example.katakan.data.network.retrofit.ApiHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(private val apiHelper: ApiHelper) {
    suspend fun getCaption(file: RequestBody) = apiHelper.getCaption(file)
}