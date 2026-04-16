package com.example.calenderyfront

import android.content.Context
import com.example.calenderyfront.Apis.PublicacionApiService
import com.example.calenderyfront.Apis.UsuarioApiService
import com.example.calenderyfront.userAuth.BasicInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://calenderyback.onrender.com/"

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun getContext(): Context = applicationContext

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(BasicInterceptor()) //Usamos el interceptor que hara la cabecera con los datos con cada peticion
            .connectTimeout(30, TimeUnit.SECONDS) //Ponemos un tiempo de espera de 30 segundos
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val usuarioApi: UsuarioApiService by lazy {
        retrofit.create(UsuarioApiService::class.java)
    }

    val publicacionApi: PublicacionApiService by lazy {
        retrofit.create(PublicacionApiService::class.java)
    }

}