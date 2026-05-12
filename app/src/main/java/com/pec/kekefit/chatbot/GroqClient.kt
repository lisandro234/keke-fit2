package com.pec.kekefit.chatbot

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object GroqClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: GroqApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApiService::class.java)
    }
}
