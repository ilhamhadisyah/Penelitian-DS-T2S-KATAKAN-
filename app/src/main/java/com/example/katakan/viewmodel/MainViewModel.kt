package com.example.katakan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.katakan.data.Repository
import com.example.katakan.data.network.Resources
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class MainViewModel(private val repository: Repository) : ViewModel() {

    fun getCaption(file: RequestBody) = liveData(Dispatchers.IO) {
        emit(Resources.loading(data = null))
        try {
            emit(Resources.success(data = repository.getCaption(file)))
        } catch (e: Exception) {
            emit(Resources.error(data = null, message = e.message ?: "Error Occurred"))
        }
    }
}