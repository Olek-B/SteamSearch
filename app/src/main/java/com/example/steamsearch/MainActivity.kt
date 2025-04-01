package com.example.steamsearch

import GameCardColumn
import SavedGameCardColumn
import TopGameCardColumn
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.steamsearch.ui.theme.SteamSearchTheme
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SteamSearchTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { NavBar(navController) }
                ) {
                    NavigationGraph(navController, Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") {
            TopGameCardColumn() }
        composable("Bookmarks") {
            SavedGameCardColumn() }
    }
}
