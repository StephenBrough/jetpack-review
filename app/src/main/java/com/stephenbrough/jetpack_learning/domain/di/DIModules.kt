package com.stephenbrough.jetpack_learning.domain.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.stephenbrough.jetpack_learning.domain.AuthRepository
import com.stephenbrough.jetpack_learning.domain.AuthRepositoryImpl
import com.stephenbrough.jetpack_learning.domain.LoginApiService
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(MockLoginInterceptor(true))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://example.com/api/")
            .client(okHttpClient)
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType()
                )
            )
            .build()
    }

    @Provides
    @Singleton
    fun providesLoginService(retrofit: Retrofit): LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
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