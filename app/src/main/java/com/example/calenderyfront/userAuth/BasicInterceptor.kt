package com.example.calenderyfront.userAuth

import android.util.Log
import com.example.calenderyfront.clients.RetrofitClient
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor para cada peticion al back
 */
class BasicInterceptor: Interceptor {
    //Este metodo se ejecuta con cada peticion por red
    override fun intercept(chain: Interceptor.Chain): Response {

        val peticion = chain.request()
        val url = peticion.url.encodedPath
        val peticionBuilder = peticion.newBuilder()

        Log.d("RETROFIT_DEBUG", "Url: $url")

        //Detecta si son las peticiones sin seguridad en el back
        //Falta poner la peticion del login
        if (url.endsWith("users/auth/register") || url.endsWith("/users/auth/resendRegistrationToken")) {
            return chain.proceed(peticion)
        }

        val context = RetrofitClient.getContext()

        //Buscamos a nivel interno email y contraseña
        val email = SessionManager.getEmail(context)
        val keypass = SessionManager.getKeypass(context)

        //En caso de tenerlas, se creara una cabecera con estas
        if (!email.isNullOrBlank() && !keypass.isNullOrBlank()) {
            val head = Credentials.basic(email,keypass,Charsets.UTF_8)
            peticionBuilder.header("Authorization", head)
        }

        else {
            Log.e("RETROFIT_DEBUG", "Error: No hay credenciales en SessionManager para esta ruta")
        }

        //Se crea la peticion con la cabecera y se manda al back
        val peticionFinal = peticionBuilder.build()

        return chain.proceed(peticionFinal)
    }
}