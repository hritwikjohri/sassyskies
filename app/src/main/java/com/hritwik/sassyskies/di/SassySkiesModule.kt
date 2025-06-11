package com.hritwik.sassyskies.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hritwik.sassyskies.repository.AuthRepository
import com.hritwik.sassyskies.service.WeatherApiService
import com.hritwik.sassyskies.service.GeminiService
import com.hritwik.sassyskies.repositoryImpl.WeatherRepositoryImpl
import com.hritwik.sassyskies.repositoryImpl.ForecastRepositoryImpl
import com.hritwik.sassyskies.repository.WeatherRepository
import com.hritwik.sassyskies.repository.ForecastRepository
import com.hritwik.sassyskies.repository.ApiKeyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SassySkiesModule {

    // Firebase Dependencies
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        database: FirebaseDatabase
    ): AuthRepository = AuthRepository(firebaseAuth, database)

    // API Key Repository for dynamic key retrieval
    @Provides
    @Singleton
    fun provideApiKeyRepository(
        authRepository: AuthRepository
    ): ApiKeyRepository = ApiKeyRepository(authRepository)

    // Network Dependencies
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    // Fallback API keys (only used when user hasn't set their own)
    @Provides
    @Named("fallback_weather_api_key")
    fun provideFallbackWeatherApiKey(): String {
        return "957ba85e586914d997dbcd3842c1fbb8" // Your fallback key
    }

    @Provides
    @Named("fallback_gemini_api_key")
    fun provideFallbackGeminiApiKey(): String {
        return "AIzaSyAQW9wkqhG303viIk8G7bjKjQz4rkTQz8k" // Your fallback key
    }

    // Dynamic Services that get API keys from user data
    @Provides
    @Singleton
    fun provideGeminiService(
        apiKeyRepository: ApiKeyRepository,
        @Named("fallback_gemini_api_key") fallbackApiKey: String
    ): GeminiService {
        return GeminiService(apiKeyRepository, fallbackApiKey)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        apiKeyRepository: ApiKeyRepository,
        @Named("fallback_weather_api_key") fallbackApiKey: String
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiService, apiKeyRepository, fallbackApiKey)
    }

    @Provides
    @Singleton
    fun provideForecastRepository(
        apiService: WeatherApiService,
        apiKeyRepository: ApiKeyRepository,
        @Named("fallback_weather_api_key") fallbackApiKey: String
    ): ForecastRepository {
        return ForecastRepositoryImpl(apiService, apiKeyRepository, fallbackApiKey)
    }
}