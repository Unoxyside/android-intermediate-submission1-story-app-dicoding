package com.bahasyim.mystoryapp.data

import android.content.Context
import com.bahasyim.mystoryapp.data.preference.UserPreference
import com.bahasyim.mystoryapp.data.preference.dataStore
import com.bahasyim.mystoryapp.data.remote.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {   fun provideRepository(context: Context): Repository {
    val pref = UserPreference.getInstance(context.dataStore)
    val user = runBlocking { pref.getSession().first() }
    val apiService = ApiConfig.getApiService(user.token)
    return Repository.getInstance(apiService, pref)
}
}