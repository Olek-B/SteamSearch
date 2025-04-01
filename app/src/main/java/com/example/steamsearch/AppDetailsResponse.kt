package com.example.steamsearch

import com.google.gson.annotations.SerializedName

// Model classes
data class AppDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: AppData?
)

data class AppData(
    @SerializedName("type") val type: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("steam_appid") val appId: Int?,
    @SerializedName("short_description") val shortDescription: String?,
    @SerializedName("price_overview") val priceOverview: PriceOverview?,
    @SerializedName("header_image") val headerImage	: PriceOverview?,
    // Add other fields as needed
)

data class PriceOverview(
    @SerializedName("final_formatted") val finalFormatted: String?
)