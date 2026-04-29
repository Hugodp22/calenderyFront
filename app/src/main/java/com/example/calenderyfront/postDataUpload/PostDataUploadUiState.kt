package com.example.calenderyfront.postDataUpload

import com.example.calenderyfront.Model.DataObjects.UserInfo
import java.time.LocalDate

data class PostDataUploadUiState (
    val userInfo: UserInfo,
    val postId: Int,
    val photoPath: String,
    val photoUrl: String,
    val message: String,
    val date: LocalDate? = null,
)