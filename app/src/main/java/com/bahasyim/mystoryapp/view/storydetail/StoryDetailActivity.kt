package com.bahasyim.mystoryapp.view.storydetail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bahasyim.mystoryapp.R
import com.bahasyim.mystoryapp.data.api.ListStoryItem
import com.bahasyim.mystoryapp.databinding.ActivityStoryDetailBinding
import com.bahasyim.mystoryapp.util.ViewUtil
import com.bumptech.glide.Glide

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewUtil.fullScreenView(this)
        val content = intent.getParcelableExtra<ListStoryItem>(DETAIL_CONTENT) as ListStoryItem
        bindData(content)
    }

    private fun bindData(content: ListStoryItem) {
        Glide.with(applicationContext)
            .load(content.photoUrl)
            .into(binding.ivDetailPhoto)
        binding.detailName.text = content.name
        binding.detailDescription.text = content.description
    }

    companion object {
        const val DETAIL_CONTENT ="detail_content"
    }
}