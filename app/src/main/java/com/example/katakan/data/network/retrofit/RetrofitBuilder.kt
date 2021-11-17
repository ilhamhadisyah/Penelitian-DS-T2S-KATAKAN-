package com.example.katakan.data.network.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private const val BASE_URL = "http://34.135.153.210"

    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(100,TimeUnit.SECONDS)
        .readTimeout(100,TimeUnit.SECONDS)
        .writeTimeout(100,TimeUnit.SECONDS)
        .addInterceptor(logging)

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}