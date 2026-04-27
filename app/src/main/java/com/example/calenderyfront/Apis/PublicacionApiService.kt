package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.dataObjects.PostData
import com.example.calenderyfront.Model.dataObjects.PublicacionHome
import com.example.calenderyfront.Model.dataObjects.PublicacionProfile
import com.example.calenderyfront.Model.dataObjects.UrlPost
import com.example.calenderyfront.pageSize
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface PublicacionApiService {

    /**
     * Funcion para cargar paginas por el id pensado para el perfil, enviandole la pagina actual
     * y el tamaño de cada pagina. Tambien se le enviara mes y año para filtrar por estos
     */
    @GET("")
    suspend fun obtenerPublicacionesPerfil(
        @Query("userId") userId: Int,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("page") page: Int,
        @Query("size") size : Int = pageSize,
    ): Response<List<PublicacionProfile>>

    @PUT("")
    suspend fun darLikePublicacion(@Body userId: Int, @Body postId: Int): Response<Unit>

    @GET("api/users/app/")
    suspend fun obtenerUrlSubidaImagenPublicaciones(): Response<UrlPost>

    @PUT("")
    suspend fun mandarDatosPost(@Body postData: PostData):Response<Unit>

    @GET("")
    suspend fun obtenerPublicacionesHome(@Query("page") page: Int, @Query("size") size : Int = pageSize): Response<List<PublicacionHome>>

}