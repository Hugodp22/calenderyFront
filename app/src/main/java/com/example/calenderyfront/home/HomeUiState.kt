package com.example.calenderyfront.home

import com.example.calenderyfront.Model.DataObjects.Comment
import com.example.calenderyfront.Model.DataObjects.PublicacionHome
import com.example.calenderyfront.Model.DataObjects.UserInfo

data class HomeUiState(
    val userInfo: UserInfo,
    val posts: List<PublicacionHome> = emptyList(),
    val comment: String = "",
    val listComments: List<Comment> = emptyList(),
    val ultimaPaginaPost: Boolean = false,
    val ultimaPaginaComment: Boolean = false
)