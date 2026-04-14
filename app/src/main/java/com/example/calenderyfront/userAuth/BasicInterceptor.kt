package com.example.calenderyfront.userAuth

import android.util.Base64
import android.util.Log
import com.example.calenderyfront.RetrofitClient
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
            val credentials = "$email:$keypass"

            val base64Credentials = Base64.encodeToString(
                credentials.toByteArray(),
                Base64.NO_WRAP
            )

            peticionBuilder.header("Authorization", "Basic $base64Credentials")
            Log.d("RETROFIT_DEBUG", "Cabecera Authorization añadida $base64Credentials")
        }
        else {
            Log.e("RETROFIT_DEBUG", "ERROR: No hay credenciales en SessionManager para esta ruta")
        }
        return chain.proceed(peticionBuilder.build())
    }
}