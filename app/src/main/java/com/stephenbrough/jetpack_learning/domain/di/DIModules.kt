package com.stephenbrough.jetpack_learning.domain.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.apollographql.apollo.ApolloClient
import com.stephenbrough.jetpack_learning.domain.AmiiboRepository
import com.stephenbrough.jetpack_learning.domain.AmiiboRepositoryImpl
import com.stephenbrough.jetpack_learning.domain.AmiiboService
import com.stephenbrough.jetpack_learning.domain.AuthRepository
import com.stephenbrough.jetpack_learning.domain.AuthRepositoryImpl
import com.stephenbrough.jetpack_learning.domain.HarryPotterRepository
import com.stephenbrough.jetpack_learning.domain.HarryPotterRepositoryImpl
import com.stephenbrough.jetpack_learning.domain.HarryPotterService
import com.stephenbrough.jetpack_learning.domain.LoginApiService
import com.stephenbrough.jetpack_learning.domain.StarWarsRepository
import com.stephenbrough.jetpack_learning.domain.StarWarsRepositoryImpl
import com.stephenbrough.jetpack_learning.domain.network.MockLoginInterceptor
import com.stephenbrough.jetpack_learning.domain.prefs.AuthPrefs
import com.stephenbrough.jetpack_learning.domain.prefs.DataStoreAuthPrefs
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesApolloClient(): ApolloClient {
        return ApolloClient
            .Builder()
            .serverUrl("https://graphql.org/swapi-graphql/graphql/")
            .build()
    }

    @Provides
    @Singleton
    fun providesOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(MockLoginInterceptor(true)).build()
    }

    @Provides
    @Singleton
    @Named("regular")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://example.com/api/").client(okHttpClient)
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType()
                )
            ).build()
    }

    @Provides
    @Singleton
    @Named("harryPotter")
    fun provideHarryPotterRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://potterapi-fedeperin.vercel.app/en/")
            .client(okHttpClient).addConverterFactory(
                Json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType()
                )
            ).build()
    }

    @Provides
    @Singleton
    @Named("amiibo")
    fun provideAmiiboRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://amiiboapi.com/api/")
            .client(okHttpClient).addConverterFactory(
                Json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType()
                )
            ).build()
    }

    @Provides
    @Singleton
    fun providesLoginService(@Named("regular") retrofit: Retrofit): LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesHarryPotterService(@Named("harryPotter") retrofit: Retrofit): HarryPotterService {
        return retrofit.create(HarryPotterService::class.java)
    }

    @Provides
    @Singleton
    fun providesAmiiboService(@Named("amiibo") retrofit: Retrofit): AmiiboService {
        return retrofit.create(AmiiboService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHarryPotterRepository(harryPotterRepositoryImpl: HarryPotterRepositoryImpl): HarryPotterRepository

    @Binds
    @Singleton
    abstract fun bindAmiiboRepository(amiiboRepositoryImpl: AmiiboRepositoryImpl): AmiiboRepository

    @Binds
    @Singleton
    abstract fun bindStarWarsRepository(starWarsRepositoryImpl: StarWarsRepositoryImpl): StarWarsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    val prefsFilename: String = "prefs"

    @Provides
    @Singleton
    fun provideDataStorePreferences(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(prefsFilename) }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthPrefsModule {
    @Binds
    @Singleton
    abstract fun bindAuthPrefs(authPrefsImpl: DataStoreAuthPrefs): AuthPrefs
}