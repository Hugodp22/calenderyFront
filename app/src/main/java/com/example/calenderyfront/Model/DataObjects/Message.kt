package com.example.calenderyfront.Model.DataObjects

data class Message(
    val idUsuario: Int,
    val mensaje: String,
    val cifrado: Boolean = true
)