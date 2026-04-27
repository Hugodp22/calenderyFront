package com.example.calenderyfront.common

sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()

    data class Error(
        val exception: Exception,
        val message: String? = exception.message
    ) : Result<Nothing>()
}