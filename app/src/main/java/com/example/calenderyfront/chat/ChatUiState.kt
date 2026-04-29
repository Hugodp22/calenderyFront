package com.example.calenderyfront.chat

import com.example.calenderyfront.Model.DataObjects.Message
import com.example.calenderyfront.Model.DataObjects.UserInfo

data class ChatUiState(
    val userInfo: UserInfo,                   // Info del usuario logeado
    val otherUserId: Int,                     // Id del usuario con el que chateas
    val messages: List<Message> = emptyList() // Lita de mensajes del chat
)