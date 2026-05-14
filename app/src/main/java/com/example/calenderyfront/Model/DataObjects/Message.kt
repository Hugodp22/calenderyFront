package com.example.calenderyfront.Model.DataObjects

enum class EstadoMensaje {
    ENVIADO,ENTREGADO,LEIDO
}

data class Message(
    val idUsuario: Int,
    val mensaje: String,
)

data class ChatId(
    val idChat: Int
)

data class ChatDto(
    val user1: Int,
    val user2: Int,
    val id : Int?,
)

data class PublicUserKeyDto(
    val publicKey: String
)

data class MessageToSend(
    val chatId: Int,
    val fromUser: Int,
    val toUser: Int,
    val selfMessage: String,
    val content: String
)

data class MessageResponseDto(
    val idMensaje: Int,
    val idChat: Int,
    val idUsuario: Int,
    val contenido: String,
    val timeStamp: String,
    val estadoMensaje: String? = null,
)

data class PageChatMessages<MessageResponseDto>(
    val content: List<MessageResponseDto>,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)