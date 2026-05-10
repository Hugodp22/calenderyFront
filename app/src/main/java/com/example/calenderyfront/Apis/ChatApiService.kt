package com.example.calenderyfront.Apis;

import com.example.calenderyfront.Model.DataObjects.ChatDto
import com.example.calenderyfront.Model.DataObjects.Message;
import com.example.calenderyfront.Model.DataObjects.PageChatMessages;
import com.example.calenderyfront.Model.DataObjects.UserVisualInfo
import retrofit2.Response;
import retrofit2.http.Body
import retrofit2.http.GET;
import retrofit2.http.POST
import retrofit2.http.Query;

interface ChatApiService {

    @GET("chat/messages")
    suspend fun getMessages(
        @Query("userId")userId: Int,
        @Query("otherUserId")otherUserId: Int,
        @Query("page")page: Int,
        @Query("size")size: Int
    ): Response<PageChatMessages<Message>>

    @POST("chat/send")
    suspend fun sendMessage(
        @Query("userId") userId: Int,
        @Query("otherUserId") otherUserId: Int,
        @Body message: Message
    ): Response<Unit>

    @POST("api/chat/saveChat")
    suspend fun crearChatUsuario(@Body chatDto: ChatDto): Response<Unit>


}