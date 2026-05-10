package com.example.calenderyfront.Model.DataObjects

data class Message(
    val idUsuario: Int,
    val mensaje: String,
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