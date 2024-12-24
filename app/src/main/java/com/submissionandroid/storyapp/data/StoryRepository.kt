package com.submissionandroid.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.submissionandroid.storyapp.data.local.StoryDatabase
import com.submissionandroid.storyapp.data.pref.UserPreference
import com.submissionandroid.storyapp.service.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val database: StoryDatabase
) {

    fun uploadStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody
    ) = apiService.uploadStory(token, photo, description)

    fun uploadStoryWithLocation(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ) = apiService.uploadStoryWithLocation(token, photo, description, lat, lon)


    fun getToken(): String {
        return runBlocking {
            val user = userPreference.getSession().first()
            user.token ?: ""
        }
    }

    fun getApiService(): ApiService {
        return apiService
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesPaging(token: String): Flow<PagingData<ListStoryItem>> {
        val pagingSourceFactory = { database.storyDao().getStories() }

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, prefetchDistance = 5),
            remoteMediator = StoryRemoteMediator(database, apiService, token),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}
