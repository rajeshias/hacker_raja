package com.example.rajahacker

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromApiResponseData(value: ApiResponseData): String {
        val gson = Gson()
        val type = object : TypeToken<ApiResponseData>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toApiResponseData(value: String): ApiResponseData {
        val gson = Gson()
        val type = object : TypeToken<ApiResponseData>() {}.type
        return gson.fromJson(value, type)
    }
}
