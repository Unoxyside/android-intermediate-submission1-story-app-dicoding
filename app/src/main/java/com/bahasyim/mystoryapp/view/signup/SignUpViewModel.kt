package com.bahasyim.mystoryapp.view.signup

import androidx.lifecycle.ViewModel
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.data.api.RegisterResponse

class SignUpViewModel(private var repository: Repository): ViewModel() {
    suspend fun register(
        name: String,
        email: String,
        password: String
    ) : RegisterResponse {
        return repository.register(name, email, password)
    }
}