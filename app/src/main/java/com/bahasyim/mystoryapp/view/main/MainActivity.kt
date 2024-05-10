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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bahasyim.mystoryapp.R
import com.bahasyim.mystoryapp.adapter.StoriesAdapter
import com.bahasyim.mystoryapp.data.api.ListStoryItem
import com.bahasyim.mystoryapp.databinding.ActivityMainBinding
import com.bahasyim.mystoryapp.util.ViewUtil
import com.bahasyim.mystoryapp.view.ViewModelFactory
import com.bahasyim.mystoryapp.view.createstory.CreateStory
import com.bahasyim.mystoryapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() =_binding!!
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

        setupRecyclerView()
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToWelcomeActivity()
            } else {
                viewModel.isLoading.observe(this) { state ->
                    showLoading(state)
                }
                viewModel.getStories().observe(this) { stories ->
                    setStoryList(stories)
                }
            }
        }
    }

    private fun navigateToWelcomeActivity() {
        startActivity(Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }


    private fun setupRecyclerView() {
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun showLoading(state: Boolean) {
     if (state){
         binding.progressBarMain.visibility = View.VISIBLE
     } else {
         binding.progressBarMain.visibility = View.GONE
     }
    }

    private fun setStoryList(stories: List<ListStoryItem>?) {
        val adapter = StoriesAdapter()
        adapter.submitList(stories)
        binding.rvStory.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
