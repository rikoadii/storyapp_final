package com.submissionandroid.storyapp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object FakeFlowDelegate {
    fun <T> createFlow(data: T): Flow<T> = flow {
        emit(data)
    }

    fun <T> createEmptyFlow(): Flow<T> = flow {}
}
