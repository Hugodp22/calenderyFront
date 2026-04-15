package com.example.calenderyfront.userAuth

import android.util.Log
import com.example.calenderyfront.RetrofitClient
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicInterceptor: Interceptor {
    //Este metodo se ejecuta con cada peticion por red
    override fun intercept(chain: Interceptor.Chain): Response {

        val peticion = chain.request()
        val url = peticion.url.encodedPath
        val peticionBuilder = peticion.newBuilder()

        Log.d("RETROFIT_DEBUG", "Url: $url")

        if (url.endsWith("users/auth/register")) {
            Log.d("RETROFIT_DEBUG", "Ruta pública detectada. Enviando sin cabeceras.")
            return chain.proceed(peticion)
        }

        val context = RetrofitClient.getContext()

        //Buscamos a nivel interno email y contraseña
        val email = SessionManager.getEmail(context)
        val keypass = SessionManager.getKeypass(context)

        Log.d("RETROFIT_DEBUG", "Sesión detectada: Email=$email, Pass=${keypass?.isNotEmpty()}")

        //En caso de tenerlas, se creara una cabecera con estas
        if (!email.isNullOrBlank() && !keypass.isNullOrBlank()) {
            val head = Credentials.basic(email,keypass)

            peticionBuilder.header("Authorization", head)
            Log.d("RETROFIT_DEBUG", "Cabecera Authorization añadida $head")
        }

        else {
            Log.e("RETROFIT_DEBUG", "ERROR: No hay credenciales en SessionManager para esta ruta")
        }
        val peticionFinal = peticionBuilder.build()

        Log.d("RETROFIT_DEBUG", "--- DETALLES DE PETICIÓN ---")
        Log.d("RETROFIT_DEBUG", "Método: ${peticionFinal.method}")
        Log.d("RETROFIT_DEBUG", "URL: ${peticionFinal.url}")

        val headers = peticionFinal.headers
        for (i in 0 until headers.size) {
            Log.d("RETROFIT_DEBUG", "Header: ${headers.name(i)} = ${headers.value(i)}")
        }

        return chain.proceed(peticionFinal)
    }
}