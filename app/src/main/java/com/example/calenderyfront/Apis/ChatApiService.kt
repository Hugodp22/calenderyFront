package com.example.calenderyfront.Apis;

import com.example.calenderyfront.Model.DataObjects.ChatDto
import com.example.calenderyfront.Model.DataObjects.ChatId
import com.example.calenderyfront.Model.DataObjects.MessageResponseDto
import com.example.calenderyfront.Model.DataObjects.PageChatMessages;
import retrofit2.Response;
import retrofit2.http.Body
import retrofit2.http.GET;
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query;

interface ChatApiService {
    @GET("api/messages/app/getChatMessages")
    suspend fun getMessages(
        @Query("idChat") idChat: Int,
        @Query("usuarioActual") usuarioActual: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageChatMessages<MessageResponseDto>>

    @POST("api/chat/saveChat")
    suspend fun crearChatUsuario(@Body chatDto: ChatDto): Response<ChatId>

    @GET("api/chat/getUserChat")
    suspend fun obtenerIdChat(@Query("idUsuario") idUsuario: Int): Response<ChatId>

    @PUT("api/messages/app/changeMessageToDeliveredState")
    suspend fun marcarNuevoMensajeComoPendiente(@Query ("idMensaje") idMensaje: Int): Response<Unit>

    @PUT("api/messages/app/changeMessageToReadedState")
    suspend fun marcarNuevoMensajeComoLeido(@Query("idMensaje") idMensaje: Int): Response<Unit>

    @GET("api/messages/app/checkForPendingMessages")
    suspend fun comprobarMensajesPendientes(): Response<Boolean>

    @PUT("api/messages/app/changeAllChatMessagesToReadedState")
    suspend fun marcarMensajesComoLeidos(@Query("idUsuario") idUsuario: Int, @Query("idChat")idChat: Int): Response<Unit>

}