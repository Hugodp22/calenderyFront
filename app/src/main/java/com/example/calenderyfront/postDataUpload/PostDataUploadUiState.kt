package com.example.calenderyfront.postDataUpload

import com.example.calenderyfront.Model.DataObjects.UserInfo

data class PostDataUploadUiState (
    val userInfo: UserInfo,
    val postId: Int,
    val photoPath: String,
    val photoUrl: String,
    val message: String,
    val month: Int,
    val year: Int,
)