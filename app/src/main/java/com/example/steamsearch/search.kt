package com.example.steamsearch.ui

import GameCardColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steamsearch.AppData
import com.example.steamsearch.R
import com.example.steamsearch.SteamViewModel
import rememberSteamViewModel

@Composable
fun SearchScreen(viewModel: SteamViewModel = rememberSteamViewModel()) {
    var searchQuery by remember { mutableStateOf("") }

    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.searchLoading.collectAsState()
    val errorMessage by viewModel.searchError.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search for a game") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.searchApps(searchQuery)
                    }
                }) {

                    Icon(
                        painter = painterResource(id = R.drawable.baseline_build_24), // your drawable here
                        contentDescription = "Search"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Loading Indicator
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.CenterHorizontally))
        }

        // Error message
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Search Results
        GameCardColumn(searchResults)
    }
}
