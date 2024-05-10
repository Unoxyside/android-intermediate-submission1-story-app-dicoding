package com.bahasyim.mystoryapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bahasyim.mystoryapp.R
import com.bahasyim.mystoryapp.data.api.RegisterResponse
import com.bahasyim.mystoryapp.databinding.ActivitySignUpBinding
import com.bahasyim.mystoryapp.util.ViewUtil
import com.bahasyim.mystoryapp.view.ViewModelFactory
import com.bahasyim.mystoryapp.view.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewUtil.fullScreenView(this)
        showLoading(false)
        submitSignUp()
        playAnimation()
    }


    private fun submitSignUp() {
        binding.buttonSignup.setOnClickListener {
            showLoading(true)
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            if (name.isEmpty()) {
                showLoading(false)
                binding.textLayoutName.error = getString(R.string.name_error)
            } else if (email.isEmpty()) {
                showLoading(false)
                binding.textLayoutEmail.error = getString(R.string.email_error)
            } else if (password.isEmpty()) {
                showLoading(false)
                binding.textLayoutPassword.error = getString(R.string.password_error)
            } else {
                binding.textLayoutName.error = null
                binding.textLayoutEmail.error = null
                binding.textLayoutPassword.error = null

                lifecycleScope.launch {
                    try {
                        val response = viewModel.register(name, email, password)
                        showLoading(false)
                        showToast(response.message)

                        MaterialAlertDialogBuilder(this@SignUpActivity)
                            .setTitle(getString(R.string.signup_dialog_tittle))
                            .setMessage(getString(R.string.signup_dialog_messege))
                            .setPositiveButton(getString(R.string.signup_dialog_continue)) { _, _ ->
                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                startActivity(intent)
                            }
                            .show()


                    } catch (e: HttpException) {
                        showLoading(false)
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                        showToast(errorResponse.message)
                    }
                }
            }

        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarSignup.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSignup.isEnabled = !isLoading
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivSignupImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val login = ObjectAnimator.ofFloat(binding.buttonSignup, View.ALPHA, 1f).setDuration(1000)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(1000)
        val tagline = ObjectAnimator.ofFloat(binding.tvTagline, View.ALPHA, 1f).setDuration(1000)
        val inputName =
            ObjectAnimator.ofFloat(binding.textLayoutName, View.ALPHA, 1f).setDuration(1000)
        val inputEmail =
            ObjectAnimator.ofFloat(binding.textLayoutEmail, View.ALPHA, 1f).setDuration(1000)
        val inputPassword =
            ObjectAnimator.ofFloat(binding.textLayoutPassword, View.ALPHA, 1f).setDuration(1000)

            val together = AnimatorSet().apply {
                playTogether(inputName, inputEmail, inputPassword)
            }

            AnimatorSet().apply {
                playSequentially(title, tagline, together, login)
                start()
            }


    }
}