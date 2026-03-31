package com.example.calenderyfront.Apis

import com.example.calenderyfront.Model.DataObjects.UserLogin
import com.example.calenderyfront.Model.DataObjects.UserProfile
import com.example.calenderyfront.Model.DataObjects.UserRegister
import com.example.calenderyfront.Model.DataObjects.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuarioApiService {

    @POST("api/v1/users/auth/register")
    suspend fun registrarUsuario(@Body datosUsuario: UserRegister): Response<Unit>

    @GET("api/v1/users/auth")
    suspend fun buscarUsuarioPorCorreo(@Body datosUsuario: UserLogin): Response<UserProfile>
    /**
     * @Path("id"): Le dice a Retrofit que reemplace el "{id}" de la URL
     * con el valor que pasemos por parámetro.
     */
    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuarioPorId(@Path("id") id: Long): Response<Usuario>
}