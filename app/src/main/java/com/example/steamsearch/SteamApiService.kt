package com.example.steamsearch

import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApiService {
    @GET("api/appdetails")
    suspend fun getAppDetails(
        @Query("appids") appId: String
    ): Map<String, AppDetailsResponse> // The response is a map with appid as the key
}

