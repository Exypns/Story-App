package com.sulthan.storyapp.ui.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sulthan.storyapp.data.remote.repository.StoryRepository
import com.sulthan.storyapp.di.Injection
import com.sulthan.storyapp.ui.viewmodel.StoryViewModel

class StoryViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context) : StoryViewModelFactory {
            if (INSTANCE == null){
                synchronized(StoryViewModelFactory::class.java) {
                    INSTANCE = StoryViewModelFactory(Injection.provideStoryRepository(context))
                }
            }
            return INSTANCE as StoryViewModelFactory
        }
    }
}