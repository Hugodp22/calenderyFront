package com.example.calenderyfront.Model.DataObjects

import kotlinx.serialization.Serializable

@Serializable
object Register

@Serializable
object Login

@Serializable
data class Settings(val userId: Int)