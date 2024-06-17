package com.sulthan.storyapp.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>){

    private val TOKEN = stringPreferencesKey("token")

    suspend fun saveSession(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    fun getSession(): Flow<String?> {
        return dataStore.data.map { preferences ->
                preferences[TOKEN] ?: ""
        }
    }

    val token: Flow<String?> = dataStore.data.map { preference ->
        preference[TOKEN]
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}