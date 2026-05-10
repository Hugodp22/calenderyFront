package com.example.calenderyfront.Model.DataObjects

data class Message(
    val idUsuario: Int,
    val mensaje: String,
    val cifrado: Boolean = true
)

data class ChatDto(
    val user1: Int,
    val user2: Int,
    val id : Int?,
)

data class MessageToSend(
    val idChat: Int,
    val idUsuario: Int,
    val otherUserId: Int,
    val myPublickKeyMessage: String,
    val otherPublickKeyMessage: String
)