package com.bahasyim.mystoryapp.data.remote

import com.bahasyim.mystoryapp.data.api.ListStoryItem
import com.bahasyim.mystoryapp.data.api.LoginResponse
import com.bahasyim.mystoryapp.data.api.RegisterResponse
import com.bahasyim.mystoryapp.data.api.StoryResponse
import com.bahasyim.mystoryapp.data.api.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    //Sign Up
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    //Login
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ):Call<LoginResponse>

    //Stories List Data
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): StoryResponse

    //Upload Story
    @Multipart
    @POST("stories")
    suspend fun uploadContent(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): UploadStoryResponse

    @Multipart
    @POST("stories")
   suspend fun uploadContentWithLocation(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double,
        @Part("lon") lon: Double
    ):UploadStoryResponse

    @GET("stories")
    suspend fun getContentWithLocation(
        @Query("location")location: Int = 1
    ): StoryResponse
}