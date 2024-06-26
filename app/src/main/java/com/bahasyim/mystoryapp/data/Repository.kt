package com.bahasyim.mystoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bahasyim.mystoryapp.data.api.ListStoryItem
import com.bahasyim.mystoryapp.data.api.LoginResponse
import com.bahasyim.mystoryapp.data.api.RegisterResponse
import com.bahasyim.mystoryapp.data.api.StoryResponse
import com.bahasyim.mystoryapp.data.api.UploadStoryResponse
import com.bahasyim.mystoryapp.data.database.StoryDatabase
import com.bahasyim.mystoryapp.data.database.mediator.StoryRemoteMediator
import com.bahasyim.mystoryapp.data.preference.UserModel
import com.bahasyim.mystoryapp.data.preference.UserPreference
import com.bahasyim.mystoryapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase,

    ) {
    private var _loginResult = MutableLiveData<LoginResponse>()
    var loginResult: MutableLiveData<LoginResponse> = _loginResult

    private var _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    private var _listStory = MutableLiveData<List<ListStoryItem>>()
    var listStory: MutableLiveData<List<ListStoryItem>> = _listStory

    //status upload
    private val _uploadStatus = MutableLiveData<Result<UploadStoryResponse>>()
    val uploadStatus: LiveData<Result<UploadStoryResponse>> = _uploadStatus


    //paging
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _loginResult.value = response.body()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Repository", "error: ${t.message}")
            }

        })
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }


    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logOut() {
        userPreference.logOut()
    }

suspend fun getContentWithLocation(): LiveData<Output<StoryResponse>> = liveData {
    emit(Output.Loading)
    try {
        val response = apiService.getContentWithLocation()
        if (response.error == false){
            emit(Output.Success(response))
        } else {
            emit(Output.Error(response.message.toString()))
        }
    } catch (e: Exception){
        emit(Output.Error(e.message.toString()))
    }
}

    suspend fun uploadContent(
        image: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double,
    ): LiveData<Output<UploadStoryResponse>> = liveData {
        emit(Output.Loading)
        val response = if (lat != 0.0 && lon != 0.0){
            apiService.uploadContentWithLocation(image, description, lat, lon)
        } else {
            apiService.uploadContent(image, description)
        }
        if (response.error == false) {
            emit(Output.Success(response))
        } else {
            emit(Output.Error(response.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: Repository? = null

        fun clearInstance() {
            instance = null
        }

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            database: StoryDatabase,
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, userPreference, database)
            }.also { instance = it }
    }
}
