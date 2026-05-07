package com.example.calenderyfront.selection

import com.example.calenderyfront.Model.DataObjects.SelectionUserData
import com.example.calenderyfront.Model.DataObjects.UserInfo

data class SelectionUiState (
    val userInfo: UserInfo,
    val chatOption: Boolean,
    val searchName : String = "",
    val selectionUsersList: List<SelectionUserData> = emptyList(),
    val lastPage: Boolean = false
)