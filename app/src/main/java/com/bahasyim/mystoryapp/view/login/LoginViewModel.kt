package com.bahasyim.mystoryapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.data.api.LoginResponse
import com.bahasyim.mystoryapp.data.preference.UserModel

class LoginViewModel(private val repository: Repository) : ViewModel() {
    val loginResult: MutableLiveData<LoginResponse> = repository.loginResult

    fun login(email: String, password: String) {
        repository.login(email, password)
    }

    suspend fun saveSession(user: UserModel){
        repository.saveSession(user)
    }
}