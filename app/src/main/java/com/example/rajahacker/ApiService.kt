package com.example.rajahacker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("job_list/s2/Medical_Dental?_ts=1&feedid=101581&SelfServiceRequest=true&locale=en-gb&iVersionNumber=16")
    suspend fun getApiResponse(): ApiResponseData

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://feeds.trac.jobs/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}