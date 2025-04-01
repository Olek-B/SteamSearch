package com.example.steamsearch
import SteamTopApiService
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import retrofit2.http.GET


class TopGamesViewModel : ViewModel() {
    private val _gameIds = mutableStateListOf<Int>()
    val gameIds: List<Int> get() = _gameIds

    init {
        fetchMostPlayedGames()
    }

    private fun fetchMostPlayedGames() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance2.api.getMostPlayedGames()
                _gameIds.clear()
                _gameIds.addAll(response.response.ranks.map { it.appid })
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }
}

// Add this class in the same file as your SteamViewModel
class TopGamesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopGamesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TopGamesViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



object RetrofitInstance2 {
    private const val BASE_URL = "https://api.steampowered.com/"

    val api: SteamTopApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SteamTopApiService::class.java)
    }
}
