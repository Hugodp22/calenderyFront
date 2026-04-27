package com.example.calenderyfront.Screens.home

import com.example.calenderyfront.Model.DataObjects.PublicacionHome

data class HomeUiState(
    val isLoading: Boolean = false,
    val posts: List<PublicacionHome> = emptyList(),
    val error: String? = null
)