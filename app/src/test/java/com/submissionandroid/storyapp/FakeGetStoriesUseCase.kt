package com.submissionandroid.storyapp

import androidx.paging.PagingData
import com.submissionandroid.storyapp.data.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGetStoriesUseCase {
    fun executeSuccess(): Flow<PagingData<ListStoryItem>> {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        return flowOf(PagingData.from(dummyStories))
    }

    fun executeEmpty(): Flow<PagingData<ListStoryItem>> {
        return flowOf(PagingData.from(emptyList()))
    }
}
