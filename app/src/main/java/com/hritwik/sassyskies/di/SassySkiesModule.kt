package com.hritwik.sassyskies.di

import com.hritwik.sassyskies.service.WeatherApiService
import com.hritwik.sassyskies.service.GeminiService
import com.hritwik.sassyskies.repositoryImpl.WeatherRepositoryImpl
import com.hritwik.sassyskies.repositoryImpl.ForecastRepositoryImpl
import com.hritwik.sassyskies.repository.WeatherRepository
import com.hritwik.sassyskies.repository.ForecastRepository
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

    @Provides
    @Named("weather_api_key")
    fun provideWeatherApiKey(): String {
        return "957ba85e586914d997dbcd3842c1fbb8"
    }

    @Provides
    @Named("gemini_api_key")
    fun provideGeminiApiKey(): String {
        return "AIzaSyAQW9wkqhG303viIk8G7bjKjQz4rkTQz8k"
    }

    @Provides
    @Singleton
    fun provideGeminiService(
        @Named("gemini_api_key") apiKey: String
    ): GeminiService {
        return GeminiService(apiKey)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        @Named("weather_api_key") apiKey: String
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiService, apiKey)
    }

    @Provides
    @Singleton
    fun provideForecastRepository(
        apiService: WeatherApiService,
        @Named("weather_api_key") apiKey: String
    ): ForecastRepository {
        return ForecastRepositoryImpl(apiService, apiKey)
    }
}