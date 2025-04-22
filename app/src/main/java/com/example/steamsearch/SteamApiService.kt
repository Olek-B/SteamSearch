package com.example.steamsearch

import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApiService {

    @GET("api/appdetails")
    suspend fun getAppDetails(
        @Query("appids") appId: String,
        @Query("cc") countryCode: String = "us",
        @Query("l") language: String = "en"
    ): Map<String, AppDetailsResponse>

    @GET("api/storesearch") // Update with your actual API endpoint
    suspend fun searchApps(
        @Query("term") term: String,
        @Query("cc") cc: String,
        @Query("l") language: String
    ): SearchResponce

}
