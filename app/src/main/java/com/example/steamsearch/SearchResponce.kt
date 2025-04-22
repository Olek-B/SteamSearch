package com.example.steamsearch

import com.google.gson.annotations.SerializedName

data class SearchResponce(
    val total: Int,
    val items: List<SearchItem>
)

data class SearchItem(
    val id: Int
)
