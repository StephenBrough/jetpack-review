package com.stephenbrough.jetpack_learning.domain

import com.stephenbrough.jetpack_learning.domain.prefs.AuthPrefs
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Singleton


interface AuthRepository {
    suspend fun login(username: String, password: String): Result<String>
    fun isLoggedIn(): Flow<Boolean>
    suspend fun logout()
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val loginApiService: LoginApiService,
    private val authPrefs: AuthPrefs
) : AuthRepository {
    override suspend fun login(
        username: String,
        password: String
    ): Result<String> {
        // Simulate network call
        return try {
            delay(1000L)

            // TODO: Test failure
            // Fake delay to simulate network call
            val result = loginApiService.login(LoginRequest(username, password))
            if (result.isSuccessful) {
                val loginResponse = result.body()
                val token = loginResponse?.token ?: ""
                authPrefs.setAuthToken(token)

                Result.success(token)
            } else {
                val errorMessage = result.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Used to determine whether the user is logged in or not at all times
    override fun isLoggedIn(): Flow<Boolean> {
        return authPrefs.getAuthToken().map { token -> !token.isNullOrEmpty() }
            .distinctUntilChanged()
    }

    override suspend fun logout() {
        authPrefs.clearAuthToken()
    }
}


