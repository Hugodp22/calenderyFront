package com.example.calenderyfront.selection

import com.example.calenderyfront.Model.DataObjects.UserChatSearch
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserSearch

data class SelectionUiState (
    val userInfo: UserInfo,
    val chatOption: Boolean,
    val searchName : String = "",
    val usersList: List<UserSearch> = emptyList(),
    val usersChatList: List<UserChatSearch> = emptyList()
)