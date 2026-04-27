package com.example.calenderyfront.screens.home

import com.example.calenderyfront.Model.dataObjects.PublicacionHome
import com.example.calenderyfront.Model.dataObjects.UserInfo

data class HomeUiState(
    val userInfo: UserInfo,
    val otherUserId: Int,
    val posts: List<PublicacionHome>,
    val ultimaPagina: Boolean = false
)