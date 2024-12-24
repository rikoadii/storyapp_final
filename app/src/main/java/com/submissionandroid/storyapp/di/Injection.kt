package com.submissionandroid.storyapp.di

import android.content.Context
import com.submissionandroid.storyapp.data.StoryRepository
import com.submissionandroid.storyapp.data.UserRepository
import com.submissionandroid.storyapp.data.local.StoryDatabase
import com.submissionandroid.storyapp.data.pref.UserPreference
import com.submissionandroid.storyapp.data.pref.dataStore
import com.submissionandroid.storyapp.service.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val storyDatabase = StoryDatabase.getInstance(context)
        return StoryRepository(apiService, pref, storyDatabase)
    }
}
