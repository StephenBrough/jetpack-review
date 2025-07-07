package com.stephenbrough.jetpack_learning.domain

import jakarta.inject.Inject
import kotlinx.coroutines.delay
import javax.inject.Singleton


interface AuthRepository {
    suspend fun login(username: String, password: String): Result<String>
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val loginApiService: LoginApiService
): AuthRepository{
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
                Result.success(token)
            } else {
                val errorMessage = result.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Login failed"))
            }
        } catch(e: Exception) {
            Result.failure(e)
        }
    }
}


