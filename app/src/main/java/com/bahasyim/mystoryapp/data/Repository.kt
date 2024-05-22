package com.bahasyim.mystoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bahasyim.mystoryapp.data.api.ListStoryItem
import com.bahasyim.mystoryapp.data.api.LoginResponse
import com.bahasyim.mystoryapp.data.api.RegisterResponse
import com.bahasyim.mystoryapp.data.api.UploadStoryResponse
import com.bahasyim.mystoryapp.data.database.StoryDatabase
import com.bahasyim.mystoryapp.data.database.mediator.StoryRemoteMediator
import com.bahasyim.mystoryapp.data.preference.UserModel
import com.bahasyim.mystoryapp.data.preference.UserPreference
import com.bahasyim.mystoryapp.data.remote.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase

){
    private var _loginResult = MutableLiveData<LoginResponse>()
    var loginResult: MutableLiveData<LoginResponse> = _loginResult

    private var _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> =_isLoading

    private var _listStory = MutableLiveData<List<ListStoryItem>>()
    var listStory: MutableLiveData<List<ListStoryItem>> = _listStory

    //status upload
    private val _uploadStatus = MutableLiveData<Result<UploadStoryResponse>>()
    val uploadStatus: LiveData<Result<UploadStoryResponse>> = _uploadStatus


    //paging
    @OptIn(ExperimentalPagingApi::class)
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
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

    fun login(email: String, password: String){
        _isLoading.value = true
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    _isLoading.value = false
                    _loginResult.value = response.body()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Repository", "error: ${t.message}")
            }

        })
    }

    suspend fun saveSession(user: UserModel){
        userPreference.saveSession(user)
    }

//    fun getStories(){
//        _isLoading.value = true
//        val client = apiService.getStories()
//        client.enqueue(object : Callback<StoryResponse> {
//            override fun onResponse(
//                call: Call<StoryResponse>,
//                response: Response<StoryResponse>
//            ) {
//                if (response.isSuccessful){
//                    _isLoading.value = false
//                    _listStory.value = response.body()?.listStory
//                }
//            }
//
//            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e("Repository", "error: ${t.message}" )
//            }
//
//        })
//    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logOut(){
        userPreference.logOut()
    }

    fun uploadContent(image: MultipartBody.Part, description: RequestBody){
        _isLoading.value = true
        val client = apiService.uploadContent(image, description)
        client.enqueue(object : Callback<UploadStoryResponse>{
            override fun onResponse(
                call: Call<UploadStoryResponse>,
                response: Response<UploadStoryResponse>,
            ) {
                if (response.isSuccessful){
                    _isLoading.value = false
                    _uploadStatus.value = Result.success(response.body()!!)
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), UploadStoryResponse::class.java)
                    _uploadStatus.value = Result.failure(Exception(errorResponse.message))
                }
            }

            override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _uploadStatus.value = Result.failure(t)
                Log.e("Repository", "error: ${t.message}" )
            }

        })
    }




    companion object {
        @Volatile
        private var instance: Repository? = null

        fun clearInstance() {
            instance = null
        }

        fun getInstance(apiService: ApiService, userPreference: UserPreference, database: StoryDatabase): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, userPreference, database)
            }.also { instance = it }
    }
}
