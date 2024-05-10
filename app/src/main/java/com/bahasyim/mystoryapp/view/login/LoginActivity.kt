package com.bahasyim.mystoryapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.bahasyim.mystoryapp.data.preference.UserModel
import com.bahasyim.mystoryapp.databinding.ActivityLoginBinding
import com.bahasyim.mystoryapp.util.ViewUtil
import com.bahasyim.mystoryapp.view.ViewModelFactory
import com.bahasyim.mystoryapp.view.main.MainActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewUtil.fullScreenView(this)
        showLoading(false)
        submitLogin()
        playAnimation()
    }

    private fun submitLogin() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edtLoginPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.textLayoutEmail.error = getString(R.string.email_error)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.textLayoutPassword.error = getString(R.string.password_error)
                return@setOnClickListener
            }
            showLoading(true)
            binding.textLayoutEmail.error = null
            binding.textLayoutPassword.error = null

            try {
                viewModel.login(email, password)
            } catch (e: HttpException) {
                showLoading(false)
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                showToast(errorResponse.message)
                return@setOnClickListener
            }

        }

        viewModel.loginResult.observe(this) { result ->
            showLoading(false)
            Log.e("LoginActivity", "error: $result")
            if (result.error) {
                // Login failed, show feedback to the user
                showToast(getString(R.string.login_failed))
            } else {
                // Login successful, save session
                showLoading(true)
                showToast(getString(R.string.login_successfully))
                saveSession(
                    UserModel(
                        result.loginResult?.token.toString(),
                        result.loginResult?.name.toString(),
                        result.loginResult?.userId.toString(),
                        true
                    )
                )
            }
        }
    }



    private fun saveSession(session: UserModel) {
        lifecycleScope.launch {
            viewModel.saveSession(session)
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            ViewModelFactory.clearInstance()
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !isLoading
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLoginImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val login = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(300)
        val tagline = ObjectAnimator.ofFloat(binding.tvTagline, View.ALPHA, 1f).setDuration(300)
        val inputEmail =
            ObjectAnimator.ofFloat(binding.textLayoutEmail, View.ALPHA, 1f).setDuration(300)
        val inputPassword =
            ObjectAnimator.ofFloat(binding.textLayoutPassword, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(title, tagline, inputEmail, inputPassword, login)
            start()
        }
    }
}