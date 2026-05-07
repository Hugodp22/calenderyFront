package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.DataObjects.PageSelectionUsers
import com.example.calenderyfront.Model.DataObjects.PublicKeyDto
import com.example.calenderyfront.Model.DataObjects.SelectionUserData
import com.example.calenderyfront.Model.DataObjects.UrlPhotos
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserProfile
import com.example.calenderyfront.Model.DataObjects.UserRegister
import com.example.calenderyfront.Model.DataObjects.UserSearch
import com.example.calenderyfront.Model.DataObjects.UserSettings
import com.example.calenderyfront.Model.DataObjects.UserValidation
import com.example.calenderyfront.pageSize
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UsuarioApiService {

    /**
     * Funcion para registrar usuario y que nos devuelvan en caso de exito
     * Sus datos configurables para llevarlo a la pantalla de configuracion
     */
    @POST("api/users/auth/register")
    suspend fun registrarUsuario(@Body datosUsuario: UserRegister): Response<UserInfo>

    /**
     * Funcion para verificar si el token ya es valido y que devuelva los datos en caso de ser correcto
     */
    @GET("api/users/activeAccountConfirmation")
    suspend fun validarUsuario(@Query("idUsuario") idUsuario: Int): Response<Unit>

    /**
     * Funcion para cargar los datos del usuario basico para su perfil
     */
    @GET("api/users/app/getUserSettings")
    suspend fun buscarSettingsUsuarioPorId(@Query("idUsuario") idUsuario: Int): Response<UserSettings>

    /**
     * Funcion para updatear los datos del usuario segun sus datos configurables
     * Y que nos devuelvan sus datos updateados para volver al perfil
     */
    @PUT("api/users/app/updateUserSetting")
    suspend fun cambiarConfiguracionUsuario(@Query ("idUsuario") idUsuario: Int, @Body datosUsuario: UserSettings): Response<Unit>

    @GET("api/users/app/getUserProfile")
    suspend fun buscarDatosPerfil(@Query ("idUsuario") idUsuario: Int): Response<UserProfile>

    @PUT("api/follower/app/follow")
    suspend fun seguirUsuario(@Query("idUsuario") idUsuario: Int, @Query("userToFollowId") userToFollowId: Int): Response<Unit>

    @DELETE("api/follower/app/unfollow")
    suspend fun dejarDeSeguirUsuario(@Query("idUsuario") idUsuario: Int, @Query("userToUnFollowId") userToUnFollowId: Int): Response<Unit>

    /**
     * Funcion para mandar la clave publica generada junto al usuario cuya clave publica se ha generado
     * para asignarsela
     */
    @PUT("api/users/app/publicKey")
    suspend fun mandarClavePublica(@Query("userId") userId: Int, @Body publicKeyDto: PublicKeyDto): Response<Unit>

    /**
     * Funcion para buscar por cabecera si existe el usuario
     */
    @POST("api/users/auth/login")
    suspend fun buscarPerfilUsuarioPorCabecera(): Response<UserInfo>

    @GET("api/users/auth/validateUser")
    suspend fun validarUsuarioPorCorreo(@Query("email",encoded = true) email: String): Response<UserValidation>

    @GET("api/users/app/getUploadProfileSignedUrl")
    suspend fun obtenerUrlSubidaImagenAvatares(): Response<UrlPhotos>

    @GET("api/users/auth/resendRegistrationToken")
    suspend fun reenviarCorreo(@Query("idUsuario") idUsuario: Int): Response<Unit>

    @GET("")
    suspend fun obtenerUsuariosBuscados(
        @Query("idUsuario")idUsuario: Int,
        @Query("searchName") searchName: String,
        @Query("page") page: Int,
        @Query("size") size : Int = pageSize,
    ): Response<PageSelectionUsers<SelectionUserData>>

    /**
     * Funcion para validar si el usuario esta enable mediante la cabecera
     * para la pantalla de redirigir
     */
    @POST("")
    suspend fun validarUsuarioPorCabecera(): Response<UserInfo>

}