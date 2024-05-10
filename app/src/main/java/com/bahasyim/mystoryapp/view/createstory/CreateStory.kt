package com.bahasyim.mystoryapp.view.createstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bahasyim.mystoryapp.R
import com.bahasyim.mystoryapp.data.api.UploadStoryResponse
import com.bahasyim.mystoryapp.databinding.ActivityCreateStoryBinding
import com.bahasyim.mystoryapp.util.ViewUtil
import com.bahasyim.mystoryapp.util.getImageUri
import com.bahasyim.mystoryapp.util.reduceImageFile
import com.bahasyim.mystoryapp.util.uriToFile
import com.bahasyim.mystoryapp.view.ViewModelFactory
import com.bahasyim.mystoryapp.view.main.MainActivity
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class CreateStory : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding
    private val viewModel by viewModels<CreateStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewUtil.fullScreenView(this)
        binding.btnGallery.setOnClickListener{ startGallery()}
        binding.btnCamera.setOnClickListener { startCamera()}
        binding.buttonUpload.setOnClickListener { uploadContent() }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun startGallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess){
            showImage()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image uri", "showImage: $it ")
            binding.previewImageView.setImageURI(it)
        }
    }



    private fun uploadContent() {
        currentImageUri?.let { uri ->
            val description = binding.edAddDescription.text.toString()
            if (description.isNotEmpty()) {
                val fileImage = uriToFile(uri, this).reduceImageFile()
                Log.d("File Image", "image: ${fileImage.path}")

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestFileImage = fileImage.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData("photo", fileImage.name, requestFileImage)

                try {
                    viewModel.uploadContent(multipartBody, requestBody)
                    backToMainActivity()
                } catch (e: HttpException) {
                    val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), UploadStoryResponse::class.java)
                    showToast(errorResponse.message.toString())
                }
            } else {
                showToast(getString(R.string.invalid_content))
            }
        }
    }

    private fun backToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}