package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.DataObjects.UserLogin
import com.example.calenderyfront.Model.DataObjects.UserRegister
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserSettings
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApiService {

    /**
     * Funcion para registrar usuario y que nos devuelvan en caso de exito
     * Sus datos configurables para llevarlo a la pantalla de configuracion
     */
    @POST("api/users/auth/register")
    suspend fun registrarUsuario(@Body datosUsuario: UserRegister): Response<UserInfo>

    //@GET("") cambiar a path
    //suspend fun buscarDatosUsuarioPorId(@Body userId: Int): Response<UserSettings>

    /**
     * Funcion para updatear los datos del usuario segun sus datos configurables
     * Y que nos devuelvan sus datos updateados para volver al perfil
     */
    @PUT("")
    suspend fun cambiarConfiguracionUsuario(@Body datosUsuario: UserSettings): Response<UserInfo>

    /**
     * Funcion para verificar si el token ya es valido y que devuelva los datos en caso de ser correcto
     */
    @GET("api/users/activeAccountConfirmation/{idUsuario}")
    suspend fun validarUsuario(@Path("idUsuario") idUsuario: Int): Response<Unit>

    /**
     * Funcion para mandar la clave publica generada junto al usuario cuya clave publica se ha generado
     * para asignarsela
     */
    @PUT("api/users/publicKey")
    suspend fun mandarClavePublica(@Path("id") userId: Int, @Body publicKey: String): Response<Unit>

    /**
     * Funcion para buscar por correo si existe el usuario, mandando su
     * Contraseña para verificar el inicio de sesion y llevarlo a su perfil
     */
    @GET("api/users/auth")
    suspend fun buscarPerfilUsuarioPorLog(@Body datosUsuario: UserLogin): Response<UserInfo>

}