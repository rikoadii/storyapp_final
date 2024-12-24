package com.submissionandroid.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submissionandroid.storyapp.data.ListStoryItem
import com.submissionandroid.storyapp.data.StoryRepository
import com.submissionandroid.storyapp.data.UserRepository
import com.submissionandroid.storyapp.data.pref.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }


    suspend fun getStories(token: String) = userRepository.getStories(token)

    fun getStoriesPaging(token: String): Flow<PagingData<ListStoryItem>> {
        return storyRepository.getStoriesPaging(token).cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}
