package com.bahasyim.mystoryapp.data.api

import com.google.gson.annotations.SerializedName

data class UploadStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
