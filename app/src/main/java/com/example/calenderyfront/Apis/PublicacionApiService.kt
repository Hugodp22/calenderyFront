package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.DataObjects.Publicacion
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PublicacionApiService {

    @GET("api/publicaciones/{id}")
    suspend fun obtenerPublicacionPorId(@Path("id") id: Long): Response<Publicacion>
}