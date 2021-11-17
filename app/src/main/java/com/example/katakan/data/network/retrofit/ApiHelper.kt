package com.example.katakan.data.network.retrofit

import okhttp3.RequestBody

class ApiHelper(private val apiService: ApiService) {
    suspend fun getCaption(file: RequestBody) = apiService.getCaption(file)
}