package com.stephenbrough.jetpack_learning.domain

import com.apollographql.apollo.ApolloClient
import com.stephenbrough.starwars.MovieQuery
import com.stephenbrough.starwars.MoviesQuery
import javax.inject.Inject
import javax.inject.Singleton

typealias StarWarsMovie = MoviesQuery.Film?
typealias StarWarsMovieDetail = MovieQuery.Film?

interface StarWarsRepository {
    suspend fun getMovies(): List<StarWarsMovie>?
    suspend fun getMovie(id: String): Result<StarWarsMovieDetail?>
}

@Singleton
class StarWarsRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : StarWarsRepository {
    override suspend fun getMovies(): List<StarWarsMovie>? {
        return apolloClient.query(MoviesQuery()).execute().data?.allFilms?.films
    }

    override suspend fun getMovie(id: String): Result<StarWarsMovieDetail?> {
        val response = apolloClient.query(MovieQuery(id)).execute()
        if (response.hasErrors()) {
            return Result.failure(Exception(response.errors.toString()))
        } else {
            return Result.success(response.data?.film)
        }
    }
}

//@OptIn(ExperimentalSerializationApi::class)
//@Serializable
//@Immutable
//@JsonIgnoreUnknownKeys
//data class StarWarsMovie(
//    val created: String,
//    val director: String,
//    val id: String,
//    val title: String
//)
