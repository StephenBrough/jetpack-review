package com.stephenbrough.jetpack_learning.domain.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AuthPrefs {
    suspend fun setAuthToken(token: String)
    fun getAuthToken(): Flow<String?>
    suspend fun clearAuthToken()

    companion object {
        const val AUTH_TOKEN_KEY = "auth_token"
    }
}

class DataStoreAuthPrefs @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthPrefs {
    override suspend fun setAuthToken(token: String) {
        println("Setting auth token: $token")
        dataStore.edit { prefs ->
            prefs[DS_AUTH_TOKEN_KEY] = token
        }
    }

    override fun getAuthToken(): Flow<String?> {
        println("Getting auth token")
        return dataStore.data.map { prefs -> prefs[DS_AUTH_TOKEN_KEY] }
    }

    override suspend fun clearAuthToken() {
        println("Clearing auth token")
        dataStore.edit { prefs ->
            prefs.clear()
//            prefs[DS_AUTH_TOKEN_KEY].
        }
    }

    companion object {
        val DS_AUTH_TOKEN_KEY =
            stringPreferencesKey(AuthPrefs.Companion.AUTH_TOKEN_KEY)
    }

}