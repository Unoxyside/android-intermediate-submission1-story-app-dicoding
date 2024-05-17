package com.bahasyim.mystoryapp.view.createstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.data.api.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateStoryViewModel(private val repository: Repository): ViewModel() {
    var isLoading: LiveData<Boolean> = repository.isLoading
    val uploadStatus: LiveData<Result<UploadStoryResponse>> = repository.uploadStatus
    fun uploadContent(image: MultipartBody.Part, description: RequestBody){
        return repository.uploadContent(image, description)
    }
}