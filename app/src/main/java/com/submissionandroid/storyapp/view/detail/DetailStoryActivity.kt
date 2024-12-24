package com.submissionandroid.storyapp.view.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.submissionandroid.storyapp.data.Story
import com.submissionandroid.storyapp.databinding.ActivityDetailStoryBinding
import com.submissionandroid.storyapp.view.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var viewModel: DetailStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        val token = intent.getStringExtra(EXTRA_TOKEN)

        viewModel = ViewModelFactory.getInstance(this).create(DetailStoryViewModel::class.java)

        if (storyId != null && token != null) {
            viewModel.getStoryDetail(token, storyId).observe(this) { story ->
                if (story != null) {
                    setupDetailView(story)
                } else {
                    showError()
                }
            }
        } else {
            showError()
        }
    }

    private fun setupDetailView(story: Story) {
        binding.progressBar.visibility = View.GONE
        binding.tvName.text = story.name
        binding.tvDescription.text = story.description
        binding.tvCreatedAt.text = story.createdAt
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivPhoto)
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_TOKEN = "extra_token"
    }
}
