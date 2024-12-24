package com.submissionandroid.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.submissionandroid.storyapp.DataDummy
import com.submissionandroid.storyapp.MainDispatcherRule
import com.submissionandroid.storyapp.data.ListStoryItem
import com.submissionandroid.storyapp.data.StoryRepository
import com.submissionandroid.storyapp.data.UserRepository
import com.submissionandroid.storyapp.view.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mainViewModel: MainViewModel
    private val storyRepository = Mockito.mock(StoryRepository::class.java)
    private val dummyStories = DataDummy.generateDummyStoryResponse()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mainViewModel = MainViewModel(Mockito.mock(UserRepository::class.java), storyRepository)
    }

    @Test
    fun `when getStoriesPaging success, data is not null and returns correct data`() = runTest {
        // Given
        val data = MainPagingSource.snapshot(dummyStories)
        Mockito.`when`(storyRepository.getStoriesPaging(Mockito.anyString()))
            .thenReturn(flowOf(data))

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        // When
        val flowPagingData = mainViewModel.getStoriesPaging("fake_token")
        val pagingData = flowPagingData.first() // Kumpulkan PagingData dari Flow
        differ.submitData(pagingData)

        // Then
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories.first(), differ.snapshot().first())
    }

    @Test
    fun `when getStoriesPaging empty, data count is zero`() = runTest {
        // Given
        val data = PagingData.empty<ListStoryItem>()
        Mockito.`when`(storyRepository.getStoriesPaging(Mockito.anyString()))
            .thenReturn(flowOf(data))

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        // When
        val flowPagingData = mainViewModel.getStoriesPaging("fake_token")
        val pagingData = flowPagingData.first() // Kumpulkan PagingData dari Flow
        differ.submitData(pagingData)

        // Then
        assertEquals(0, differ.snapshot().size)
    }
}

class MainPagingSource : PagingSource<Int, ListStoryItem>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
