package com.bahasyim.mystoryapp.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bahasyim.mystoryapp.data.Output
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.data.api.StoryResponse

class MapsViewModel(private val repository: Repository): ViewModel() {
    suspend fun getContentWithLocation(): LiveData<Output<StoryResponse>> {
     return repository.getContentWithLocation()
    }
}