package com.example.rajahacker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ApiResponseDao {
    @Query("SELECT * FROM api_response WHERE id = :id")
    suspend fun getResponse(id: Int): ApiResponse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(apiResponse: ApiResponse)
}
