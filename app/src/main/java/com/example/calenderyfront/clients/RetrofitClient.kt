package com.example.calenderyfront.clients

import android.content.Context
import com.example.calenderyfront.Apis.ChatApiService
import com.example.calenderyfront.Apis.PublicacionApiService
import com.example.calenderyfront.Apis.UsuarioApiService
import com.example.calenderyfront.userAuth.BasicInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://calenderyback.onrender.com/"

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun getContext(): Context = applicationContext

     val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(BasicInterceptor()) //Usamos el interceptor que hara la cabecera con los datos con cada peticion
            .connectTimeout(10, TimeUnit.SECONDS) //Ponemos un tiempo de espera de 30 segundos
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //Para transformar en JSON automaticamente
            .client(okHttpClient)
            .build()
    }

    val usuarioApi: UsuarioApiService by lazy {
        retrofit.create(UsuarioApiService::class.java)
    }

    val publicacionApi: PublicacionApiService by lazy {
        retrofit.create(PublicacionApiService::class.java)
    }

    val chatApi: ChatApiService = retrofit.create(ChatApiService::class.java)

}