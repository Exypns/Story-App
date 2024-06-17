package com.sulthan.storyapp.ui.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sulthan.storyapp.data.remote.repository.UserRepository
import com.sulthan.storyapp.di.Injection
import com.sulthan.storyapp.ui.viewmodel.UserViewModel
import com.sulthan.storyapp.ui.viewmodel.LoginViewModel

class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context) : UserViewModelFactory {
            if (INSTANCE == null) {
                synchronized(UserViewModelFactory::class.java) {
                    INSTANCE = UserViewModelFactory(Injection.provideUserRepository(context))
                }
            }
            return INSTANCE as UserViewModelFactory
        }
    }
}