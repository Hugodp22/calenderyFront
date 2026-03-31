package com.example.calenderyfront

import com.example.calenderyfront.Apis.PublicacionApiService
import com.example.calenderyfront.Apis.UsuarioApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://calenderyback.onrender.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarioApi: UsuarioApiService by lazy {
        retrofit.create(UsuarioApiService::class.java)
    }

    val publicacionApi: PublicacionApiService by lazy {
        retrofit.create(PublicacionApiService::class.java)
    }

}