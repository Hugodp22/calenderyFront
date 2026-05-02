package com.example.calenderyfront.chat

sealed class ChatState {
    object Loading : ChatState() // Estado inicial mientras se cargan los mensajes
    object Started : ChatState() // Estado cuando tod0 ha cargado correctamente
    data class Error(val message: Int) : ChatState() // Estado de error
}