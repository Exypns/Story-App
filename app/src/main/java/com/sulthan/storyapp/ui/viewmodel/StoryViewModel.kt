package com.sulthan.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sulthan.storyapp.data.remote.repository.StoryRepository
import com.sulthan.storyapp.data.remote.response.ListStoryItem
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> = storyRepository.getStories().cachedIn(viewModelScope)

    fun getDetailStory(id: String) = storyRepository.getDetailStory(id)

    fun uploadStory(description: RequestBody, file: MultipartBody.Part, lat: Float?, lon: Float?) =
        storyRepository.uploadStory(description, file, lat, lon)

    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()

}