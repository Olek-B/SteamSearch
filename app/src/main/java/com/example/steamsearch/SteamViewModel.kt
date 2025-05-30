package com.example.steamsearch

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import android.util.Log


// At top level of the file (outside any class)
val Context.dataStore by preferencesDataStore(name = "saved_games")

class SteamViewModel(@SuppressLint("StaticFieldLeak") private val context: Context) : ViewModel() {
    private val SAVED_GAMES_KEY = stringSetPreferencesKey("saved_game_ids")

    val savedGameIds: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[SAVED_GAMES_KEY] ?: emptySet()
        }

    fun saveGame(appData: AppData) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                val current = preferences[SAVED_GAMES_KEY]?.toMutableSet() ?: mutableSetOf()
                current.add(appData.appId.toString())
                preferences[SAVED_GAMES_KEY] = current
            }
        }
    }

    fun removeSavedGame(appId: Int) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                val current = preferences[SAVED_GAMES_KEY]?.toMutableSet() ?: mutableSetOf()
                current.remove(appId.toString())
                preferences[SAVED_GAMES_KEY] = current
            }
        }
    }

    // App details state management
    private val _appDetailsMap = mutableMapOf<String, MutableStateFlow<AppData?>>()
    private val _loadingStates = mutableMapOf<String, MutableStateFlow<Boolean>>()
    private val _errorStates = mutableMapOf<String, MutableStateFlow<String?>>()

    fun getAppDetailsFlow(appId: String): StateFlow<AppData?> {
        return _appDetailsMap.getOrPut(appId) { MutableStateFlow(null) }.asStateFlow()
    }

    fun getLoadingStateFlow(appId: String): StateFlow<Boolean> {
        return _loadingStates.getOrPut(appId) { MutableStateFlow(false) }.asStateFlow()
    }

    fun getErrorFlow(appId: String): StateFlow<String?> {
        return _errorStates.getOrPut(appId) { MutableStateFlow(null) }.asStateFlow()
    }

    fun fetchAppDetails(appId: String) {
        viewModelScope.launch {
            val loadingFlow = _loadingStates.getOrPut(appId) { MutableStateFlow(false) }
            val errorFlow = _errorStates.getOrPut(appId) { MutableStateFlow(null) }
            val detailsFlow = _appDetailsMap.getOrPut(appId) { MutableStateFlow(null) }

            loadingFlow.value = true
            errorFlow.value = null

            try {
                val response = RetrofitClient.steamApiService.getAppDetails(appId)
                val details = response[appId]?.data
                if (details != null) {
                    detailsFlow.value = details
                } else {
                    errorFlow.value = "No data found for app $appId"
                }
            } catch (e: Exception) {
                errorFlow.value = "Failed to fetch data for app $appId: ${e.message}"
            } finally {
                loadingFlow.value = false
            }
        }
    }


    private val _searchResults = MutableStateFlow<List<Int>>(emptyList())
    val searchResults: StateFlow<List<Int>> = _searchResults.asStateFlow()

    private val _searchLoading = MutableStateFlow(false)
    val searchLoading: StateFlow<Boolean> = _searchLoading.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    fun searchApps(term: String, cc: String = "us", language: String = "en") {
        viewModelScope.launch {
            _searchLoading.value = true
            _searchError.value = null

            try {
                val response = RetrofitClient.steamApiService.searchApps(term, cc, language)
                Log.i("help", response.toString());
                val results = response.items.map { it.id } // Map the list of SearchItems to a list of IDs
                _searchResults.value = results
            } catch (e: Exception) {
                _searchError.value = "Search failed: ${e.message}"
                Log.i("help", "czemu?");
                _searchResults.value = emptyList()
            } finally {
                _searchLoading.value = false
            }
        }
    }

}

class SteamViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SteamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SteamViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

object RetrofitClient {
    private const val BASE_URL = "https://store.steampowered.com/"

    private val gson = GsonBuilder().create()
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val steamApiService: SteamApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(SteamApiService::class.java)
    }
}
