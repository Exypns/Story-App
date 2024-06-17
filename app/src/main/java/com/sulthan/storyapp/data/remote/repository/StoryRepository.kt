package com.sulthan.storyapp.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.sulthan.storyapp.data.ResultState
import com.sulthan.storyapp.data.StoryRemoteMediator
import com.sulthan.storyapp.data.local.database.StoryDatabase
import com.sulthan.storyapp.data.remote.response.AddStoryResponse
import com.sulthan.storyapp.data.remote.response.DetailStoryResponse
import com.sulthan.storyapp.data.remote.response.ListStoryItem
import com.sulthan.storyapp.data.remote.response.StoryResponse
import com.sulthan.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
){
    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStory()
            }
        ).liveData
    }

    fun getDetailStory(id: String) = liveData {
            emit(ResultState.Loading)
        try {
            val successResponse = apiService.getDetailStory(id)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DetailStoryResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "Unknown Error"))
        }
    }

    fun uploadStory(description: RequestBody, file: MultipartBody.Part, lat: Float?, lon: Float?) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.addStory(description, file, lat, lon)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddStoryResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "Unknown Error"))
        }
    }

    fun getStoriesWithLocation() = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.getStoriesWithLocation(1)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "Unknown Error"))
        }
    }

    companion object {
        @Volatile
        private var instance : StoryRepository? = null
        fun getInstance(storyDatabase: StoryDatabase, apiService: ApiService) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService)
            }.also { instance = it }
    }
}