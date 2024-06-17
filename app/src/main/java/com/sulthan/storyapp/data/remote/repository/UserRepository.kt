package com.sulthan.storyapp.data.remote.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.sulthan.storyapp.data.ResultState
import com.sulthan.storyapp.data.pref.UserPreference
import com.sulthan.storyapp.data.remote.response.LoginResponse
import com.sulthan.storyapp.data.remote.response.RegisterResponse
import com.sulthan.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){
    fun register(name: String, email: String, password: String) = liveData {
            emit(ResultState.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "Unknown Error"))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown Error"))
        }
    }

    fun login(email: String, password: String) = liveData {
            emit(ResultState.Loading)
        try {
            val successResponse = apiService.login(email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultState.Error(errorMessage ?: "Unknown Error"))
        }
    }

    suspend fun saveSession(token: String) {
        userPreference.saveSession(token)
    }

    fun getSession() : Flow<String?> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance : UserRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference) : UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}