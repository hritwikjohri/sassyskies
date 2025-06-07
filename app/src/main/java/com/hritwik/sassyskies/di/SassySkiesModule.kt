package com.hritwik.sassyskies.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hritwik.sassyskies.repository.AuthRepository
import com.hritwik.sassyskies.repository.SecureApiKeyRepository
import com.hritwik.sassyskies.service.WeatherApiService
import com.hritwik.sassyskies.service.GeminiService
import com.hritwik.sassyskies.repositoryImpl.WeatherRepositoryImpl
import com.hritwik.sassyskies.repositoryImpl.ForecastRepositoryImpl
import com.hritwik.sassyskies.repository.WeatherRepository
import com.hritwik.sassyskies.repository.ForecastRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepository(firebaseAuth, firestore)

    @Provides
    @Singleton
    fun provideSecureApiKeyRepository(
        @ApplicationContext context: Context
    ): SecureApiKeyRepository = SecureApiKeyRepository(context)

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
    @Named("default_weather_api_key")
    fun provideDefaultWeatherApiKey(): String {
        return "your_fallback_key_here"
    }

    @Provides
    @Named("default_gemini_api_key")
    fun provideDefaultGeminiApiKey(): String {
        return "your_fallback_key_here"
    }

    @Provides
    @Singleton
    fun provideGeminiService(
        secureApiKeyRepository: SecureApiKeyRepository,
        @Named("default_gemini_api_key") defaultApiKey: String
    ): GeminiService {
        val apiKey = secureApiKeyRepository.getGeminiApiKey() ?: defaultApiKey
        return GeminiService(apiKey)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        secureApiKeyRepository: SecureApiKeyRepository,
        @Named("default_weather_api_key") defaultApiKey: String
    ): WeatherRepository {
        val apiKey = secureApiKeyRepository.getWeatherApiKey() ?: defaultApiKey
        return WeatherRepositoryImpl(apiService, apiKey)
    }

    @Provides
    @Singleton
    fun provideForecastRepository(
        apiService: WeatherApiService,
        secureApiKeyRepository: SecureApiKeyRepository,
        @Named("default_weather_api_key") defaultApiKey: String
    ): ForecastRepository {
        val apiKey = secureApiKeyRepository.getWeatherApiKey() ?: defaultApiKey
        return ForecastRepositoryImpl(apiService, apiKey)
    }
}