package com.example.katakan.data.network.retrofit

import com.example.katakan.data.model.Response
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("/")
    suspend fun getCaption(@Part("file\"; filename=\"pp.png\"") file: RequestBody): Response
}