package com.bahasyim.mystoryapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.bahasyim.mystoryapp.R
import com.bahasyim.mystoryapp.adapter.LoadingStoryAdapter
import com.bahasyim.mystoryapp.adapter.StoriesAdapter
import com.bahasyim.mystoryapp.databinding.ActivityMainBinding
import com.bahasyim.mystoryapp.util.ViewUtil
import com.bahasyim.mystoryapp.view.ViewModelFactory
import com.bahasyim.mystoryapp.view.createstory.CreateStory
import com.bahasyim.mystoryapp.view.map.MapsActivity
import com.bahasyim.mystoryapp.view.welcome.WelcomeActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ViewUtil.fullScreenView(this)
        setupViews()
        getSession()
    }

    private fun setupViews() {
        binding.actionLogout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logOut()
                navigateToWelcomeActivity()
            }
        }

        binding.fabCreateStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, CreateStory::class.java))
        }

        binding.actionMaps.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }

    }

    private fun getSession() {
        val adapter = StoriesAdapter()
        val loadingAdapter = LoadingStoryAdapter { adapter.retry()}

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = adapter.withLoadStateFooter(footer = loadingAdapter)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToWelcomeActivity()
            } else {
                lifecycleScope.launch {
                    viewModel.story.observe(this@MainActivity) { pagingData ->
                        adapter.submitData(lifecycle, pagingData)
                    }
                }
                adapter.addLoadStateListener { loadState ->
                    val isLoading = loadState.source.refresh is LoadState.Loading
                    showLoading(isLoading)

                    val isError = loadState.source.refresh is LoadState.Error
                    if (isError) {
                        showErrorSnackbar("Failed to load data. Please try again.")
                    }
                }
            }
        }

    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("Retry") {
                val adapter = binding.rvStory.adapter as StoriesAdapter
                adapter.retry()
            }.show()
    }

    private fun navigateToWelcomeActivity() {
        startActivity(Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBarMain.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
