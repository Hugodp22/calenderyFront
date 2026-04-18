package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface PublicacionApiService {

    /**
     * Funcion para cargar paginas por el id pensado para el perfil, enviandole la pagina actual
     * y el tamaño de cada pagina, 9 de momento hasta hacer pruebas
     */
    @GET("")
    suspend fun obtenerPublicacionesPerfil(@Body userId: Int,@Query("page") page: Int, @Query("size") size : Int = 9): Response<List<PublicacionProfile>>

}