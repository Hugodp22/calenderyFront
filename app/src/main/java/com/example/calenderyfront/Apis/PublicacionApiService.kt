package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.DataObjects.Comment
import com.example.calenderyfront.Model.DataObjects.PageHomePosts
import com.example.calenderyfront.Model.DataObjects.PagePostComments
import com.example.calenderyfront.Model.DataObjects.PageProfilePosts
import com.example.calenderyfront.Model.DataObjects.PostCommentDto
import com.example.calenderyfront.Model.DataObjects.PostData
import com.example.calenderyfront.Model.DataObjects.PublicacionHome
import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import com.example.calenderyfront.Model.DataObjects.UrlPost
import com.example.calenderyfront.pageSize
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("api/comment/app/getComments")
    suspend fun obtenerComentariosPublicacion(
        @Query("idPublicacion") publicacionId: Int,
        @Query("page") page: Int,
        @Query("size") size : Int = pageSize
    ): Response<PagePostComments<Comment>>

    @POST("api/comment/app/postComment")
    suspend fun enviarComentarioPublicacion(@Body postComment: PostCommentDto): Response<Int>

    @POST("api/like/app/likePublication")
    suspend fun darLikePublicacion(@Query ("idPublicacion") idPublicacion: Int): Response<Unit>

    @DELETE("api/like/app/removeLikePublication")
    suspend fun quitarLikePublicacion(@Query ("idPublicacion") idPublicacion: Int): Response<Unit>

    @GET("api/publication/app/getPostUrl")
    suspend fun obtenerUrlSubidaImagenPublicaciones(): Response<UrlPost>

    @PUT("api/publication/app/putPublicationData")
    suspend fun mandarDatosPost(@Body postData: PostData):Response<Unit>

    @GET("")
    suspend fun obtenerPublicacionesHome(@Query("page") page: Int,
                                         @Query("size") size : Int = pageSize
    ): Response<PageHomePosts<PublicacionHome>>

}