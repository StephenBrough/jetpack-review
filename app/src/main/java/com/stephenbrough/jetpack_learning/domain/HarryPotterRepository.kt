package com.stephenbrough.jetpack_learning.domain

import androidx.compose.runtime.Immutable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

interface HarryPotterRepository {
    suspend fun getBooks(): List<Book>
}

@Singleton
class HarryPotterRepositoryImpl @Inject constructor(
    private val harryPotterService: HarryPotterService
): HarryPotterRepository {
    override suspend fun getBooks(): List<Book> {
        return harryPotterService.getBooks()
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@Immutable
@JsonIgnoreUnknownKeys
data class Book(
    val title: String,
    val description: String,
    val cover: String
)

interface HarryPotterService {
    @GET("books")
    suspend fun getBooks(): List<Book>
}