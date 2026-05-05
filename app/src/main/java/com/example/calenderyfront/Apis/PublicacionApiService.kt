package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.DataObjects.Comment
import com.example.calenderyfront.Model.DataObjects.PagePostComments
import com.example.calenderyfront.Model.DataObjects.PageProfilePosts
import com.example.calenderyfront.Model.DataObjects.PostData
import com.example.calenderyfront.Model.DataObjects.PublicacionHome
import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import com.example.calenderyfront.Model.DataObjects.UrlPost
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
    @GET("api/publication/app/getProfilePosts")
    suspend fun obtenerPublicacionesPerfil(
        @Query("idUsuario") idUsuario: Int,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("page") page: Int,
        @Query("size") size : Int = pageSize,
    ): Response<PageProfilePosts<PublicacionProfile>>

    @GET("")
    suspend fun obtenerComentariosPublicacion(
        @Query("idPublicacion") idPublicacion: Int,
        @Query("page") page: Int,
        @Query("size") size : Int = pageSize
    ): Response<PagePostComments<Comment>>

    @PUT("")
    suspend fun enviarComentarioPublicacion(
        @Query("idUsuario") idUsuario: Int,
        @Query("idPublicacion") idPublicacion: Int,
        @Query("comentario") comentario: String
    ): Response<Unit>

    @PUT("")
    suspend fun darLikePublicacion(@Body userId: Int, @Body postId: Int): Response<Unit>

    @PUT("")
    suspend fun quitarLikePublicacion(@Body userId: Int, @Body postId: Int): Response<Unit>

    @GET("api/publication/app/getPostUrl")
    suspend fun obtenerUrlSubidaImagenPublicaciones(): Response<UrlPost>

    @PUT("api/publication/app/putPublicationData")
    suspend fun mandarDatosPost(@Body postData: PostData):Response<Unit>

    @GET("")
    suspend fun obtenerPublicacionesHome(@Query("page") page: Int, @Query("size") size : Int = pageSize): Response<List<PublicacionHome>>

}