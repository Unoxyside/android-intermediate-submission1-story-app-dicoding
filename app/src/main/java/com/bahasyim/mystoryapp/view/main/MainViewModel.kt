package com.bahasyim.mystoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.data.api.ListStoryItem
import com.bahasyim.mystoryapp.data.preference.UserModel

class MainViewModel(private val repository: Repository) : ViewModel() {

    //    fun getStories(): LiveData<List<ListStoryItem>?> {
//        repository.getStories()
//        return repository.listStory
//    }
    //add paging
    val story: LiveData<PagingData<ListStoryItem>> = repository.getStory().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    suspend fun logOut() {
        repository.logOut()
    }

}