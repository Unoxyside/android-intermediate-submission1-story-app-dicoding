package com.bahasyim.mystoryapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bahasyim.mystoryapp.data.Injection
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.view.createstory.CreateStoryViewModel
import com.bahasyim.mystoryapp.view.login.LoginViewModel
import com.bahasyim.mystoryapp.view.main.MainViewModel
import com.bahasyim.mystoryapp.view.signup.SignUpViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    override fun <Type : ViewModel> create(modelClass: Class<Type>): Type {
        val viewModel = when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> SignUpViewModel(repository)
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository)
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository)
            modelClass.isAssignableFrom(CreateStoryViewModel::class.java) -> CreateStoryViewModel(repository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as Type
    }


    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun clearInstance() {
            Repository.clearInstance()
            INSTANCE = null
        }

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}