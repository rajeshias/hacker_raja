package com.example.rajahacker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_response")
data class ApiResponse(
    @PrimaryKey val id: Int,
    val response: ApiResponseData
)
