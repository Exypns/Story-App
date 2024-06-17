package com.sulthan.storyapp.di

import android.content.Context
import com.sulthan.storyapp.data.local.database.StoryDatabase
import com.sulthan.storyapp.data.pref.UserPreference
import com.sulthan.storyapp.data.pref.dataStore
import com.sulthan.storyapp.data.remote.repository.StoryRepository
import com.sulthan.storyapp.data.remote.repository.UserRepository
import com.sulthan.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return UserRepository.getInstance(apiService, pref)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(storyDatabase, apiService)
    }
}