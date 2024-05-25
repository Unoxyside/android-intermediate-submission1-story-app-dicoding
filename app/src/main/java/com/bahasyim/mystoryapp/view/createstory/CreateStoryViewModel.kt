package com.bahasyim.mystoryapp.view.createstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bahasyim.mystoryapp.data.Output
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.data.api.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateStoryViewModel(private val repository: Repository) : ViewModel() {
   suspend fun uploadContent(
        image: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double,
    ): LiveData<Output<UploadStoryResponse>> {
        return repository.uploadContent(image, description, lat, lon)
    }
}