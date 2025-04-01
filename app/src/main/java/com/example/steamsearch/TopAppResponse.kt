package com.example.steamsearch

data class MostPlayedGamesResponse(
    val response: ResponseData
)

data class ResponseData(
    val ranks: List<GameRank>
)

data class GameRank(
    val rank: Int,
    val appid: Int,
    val last_week_rank: Int,
    val peak_in_game: Int
)
