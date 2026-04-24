package com.example.calenderyfront.clients

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object PhotoClient {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}