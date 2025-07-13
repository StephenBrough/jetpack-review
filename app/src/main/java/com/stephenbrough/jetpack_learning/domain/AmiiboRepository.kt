package com.stephenbrough.jetpack_learning.domain

import androidx.compose.runtime.Immutable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import retrofit2.http.GET
import javax.inject.Inject

interface AmiiboRepository {
    suspend fun getAmiiboList(): AmiiboWrapper

}

class AmiiboRepositoryImpl @Inject constructor(
    private val amiiboService: AmiiboService
) : AmiiboRepository {
    override suspend fun getAmiiboList(): AmiiboWrapper {
        return amiiboService.getAmiiboList()
    }
}

interface AmiiboService {
    @GET("amiibo/")
    suspend fun getAmiiboList(): AmiiboWrapper
}


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@Immutable
@JsonIgnoreUnknownKeys
data class Amiibo(
    val name: String,
    val image: String,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@Immutable
@JsonIgnoreUnknownKeys
data class AmiiboWrapper(
    val amiibo: List<Amiibo>
)