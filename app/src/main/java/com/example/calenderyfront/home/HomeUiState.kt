package com.example.calenderyfront.home

import com.example.calenderyfront.Model.DataObjects.PublicacionHome
import com.example.calenderyfront.Model.DataObjects.UserInfo

data class HomeUiState(
    val userInfo: UserInfo,
    val posts: List<PublicacionHome>,
    val ultimaPagina: Boolean = false
)