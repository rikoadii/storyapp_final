package com.submissionandroid.storyapp.data

import com.submissionandroid.storyapp.data.pref.UserModel
import com.submissionandroid.storyapp.data.pref.UserPreference
import com.submissionandroid.storyapp.service.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getStories(token: String): StoryResponse {
        return apiService.getStories("Bearer $token")
    }

    suspend fun getStoryDetail(token: String, storyId: String): DetailStoryResponse? {
        return try {
            val response = apiService.getStoryDetail("Bearer $token", storyId)
            if (!response.error!! && response.story != null) {
                response
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }


}