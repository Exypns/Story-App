package com.sulthan.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sulthan.storyapp.data.remote.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun saveSession(token: String) {
        viewModelScope.launch {
            userRepository.saveSession(token)
        }
    }

}