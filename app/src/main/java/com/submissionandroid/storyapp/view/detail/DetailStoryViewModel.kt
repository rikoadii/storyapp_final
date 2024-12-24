package com.submissionandroid.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.submissionandroid.storyapp.data.Story
import com.submissionandroid.storyapp.data.UserRepository

class DetailStoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getStoryDetail(token: String, storyId: String): LiveData<Story?> = liveData {
        val response = userRepository.getStoryDetail(token, storyId)
        if (response != null && !response.error!!) {
            emit(response.story)
        } else {
            emit(null)
        }
    }
}
