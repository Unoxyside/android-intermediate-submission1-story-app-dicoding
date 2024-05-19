package com.bahasyim.mystoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.data.api.ListStoryItem
import com.bahasyim.mystoryapp.data.preference.UserModel

class MainViewModel(private val repository: Repository): ViewModel() {
    var isLoading: LiveData<Boolean> = repository.isLoading

    fun getStories(): LiveData<List<ListStoryItem>?> {
        repository.getStories()
        return repository.listStory
    }

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }

    suspend fun logOut(){
        repository.logOut()
    }

}