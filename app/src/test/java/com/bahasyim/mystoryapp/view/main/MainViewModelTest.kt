package com.bahasyim.mystoryapp.view.main


import android.annotation.SuppressLint
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.bahasyim.mystoryapp.DataDummy
import com.bahasyim.mystoryapp.MainDispatcherRule
import com.bahasyim.mystoryapp.adapter.StoriesAdapter
import com.bahasyim.mystoryapp.data.Repository
import com.bahasyim.mystoryapp.data.api.ListStoryItem
import com.bahasyim.mystoryapp.data.paging.StoryPagingSource
import com.bahasyim.mystoryapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@Suppress("DEPRECATION")
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutine = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: Repository
    private lateinit var mainViewModel: MainViewModel
    private lateinit var logMock: AutoCloseable

    @SuppressLint("CheckResult")
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        logMock = mockStatic(Log::class.java)
        `when`(Log.isLoggable(anyString(), anyInt())).thenReturn(true)
    }

    @After
    fun tearDown() {
        logMock.close()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dataDummy = DataDummy.generateDummyStoryResponse()
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>().apply {
            value = StoryPagingSource.snapshot(dataDummy)
        }
        `when`(storyRepository.getStory()).thenReturn(expectedStory)
        mainViewModel = MainViewModel(storyRepository)

        val actualStory = mainViewModel.story.getOrAwaitValue()
        val differ = createDiffer()
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dataDummy.size, differ.snapshot().size)
        assertEquals(dataDummy[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>().apply {
            value = PagingData.from(emptyList())
        }
        `when`(storyRepository.getStory()).thenReturn(expectedStory)
        mainViewModel = MainViewModel(storyRepository)

        val actualStory = mainViewModel.story.getOrAwaitValue()
        val differ = createDiffer()
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }

    private fun createDiffer() = AsyncPagingDataDiffer(
        diffCallback = StoriesAdapter.DIFFUTIL,
        updateCallback = noopListUpdateCallback,
        workerDispatcher = Dispatchers.Main
    )
}

private val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}