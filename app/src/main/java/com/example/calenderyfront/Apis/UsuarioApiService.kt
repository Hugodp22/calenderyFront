package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.DataObjects.UserLogin
import com.example.calenderyfront.Model.DataObjects.UserProfile
import com.example.calenderyfront.Model.DataObjects.UserRegister
import com.example.calenderyfront.Model.DataObjects.UserSettings
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UsuarioApiService {

    /**
     * Funcion para registrar usuario y que nos devuelvan en caso de exito
     * Sus datos configurables para llevarlo a la pantalla de configuracion
     */
    @POST("api/v1/users/auth/register")
    suspend fun registrarUsuario(@Body datosUsuario: UserRegister): Response<UserSettings>

    /**
     * Funcion para updatear los datos del usuario segun sus datos configurables
     * Y que nos devuelvan sus datos updateados para volver al perfil
     */
    @PUT("")
    suspend fun cambiarConfiguracionUsuario(@Body datosUsuario: UserSettings): Response<UserProfile>

    /**
     * Funcion para buscar por correo si existe el usuario, mandando su
     * Contraseña para verificar el inicio de sesion y llevarlo a su perfil
     */
    @GET("api/v1/users/auth")
    suspend fun buscarPerfilUsuarioPorCorreo(@Body datosUsuario: UserLogin): Response<UserProfile>

}