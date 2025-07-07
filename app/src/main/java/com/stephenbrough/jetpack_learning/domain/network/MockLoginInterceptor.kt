package com.stephenbrough.jetpack_learning.domain.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Interceptor that mocks the login API. Since I don't have a real backend, this will return a
 * mock response and can also return error responses for testing.
 */
class MockLoginInterceptor(private val shouldSucceed: Boolean = true) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath.contains("login")) {
            val responseBody = if (shouldSucceed) {
                """{"token": "mock_token_${System.currentTimeMillis()}"}"""
            } else {
                """{"error": "Invalid Credentials"}"""
            }

            val code = if (shouldSucceed) 200 else 401
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (shouldSucceed) "OK" else "Unauthorized")
                .body(responseBody.toResponseBody("application/json".toMediaType()))
                .build()
        }

        return chain.proceed(request)

    }
}