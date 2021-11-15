package com.example.katakan.tts_service

interface TextConverterCallback {
    fun onStart(result: String)
    fun onDone(utteranceId: String)
    fun onError(message: String)
}