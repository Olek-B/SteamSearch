import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import android.util.Log
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.steamsearch.AppData
import com.example.steamsearch.SteamViewModel
import com.example.steamsearch.SteamViewModelFactory
import com.example.steamsearch.TopGamesViewModel
import com.example.steamsearch.TopGamesViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCardColumn(
    gameIds: List<Int>? = null,       // Optional predefined list
    randomCount: Int = 0,             // Number of random IDs to generate
    modifier: Modifier = Modifier,
    viewModel: SteamViewModel = rememberSteamViewModel()
) {
    // Generate random popular Steam game IDs if no list is provided
    val popularSteamGameIds = remember {
        listOf(440, 570, 730, 578080, 292030, 271590, 1091500, 1172470, 1245620, 1593500)
    }

    val finalGameIds = remember(gameIds, randomCount) {
        gameIds ?: List(randomCount.coerceAtLeast(1)) {
            popularSteamGameIds.random()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .paddingFromBaseline(bottom = 150.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(finalGameIds) { gameId ->
            GameCard(gameID = gameId)
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
public fun rememberSteamViewModel(context: Context = LocalContext.current): SteamViewModel {
    val factory = remember { SteamViewModelFactory(context) }
    return viewModel(factory = factory)
}

@SuppressLint("RememberReturnType")
@Composable
public fun rememberTopSteamViewModel(context: Context = LocalContext.current): TopGamesViewModel {
    val factory = remember { TopGamesViewModelFactory(context) }
    return viewModel(factory = factory)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(gameID: Int, size: Dp = 400.dp) {
    val context = LocalContext.current
    val viewModel = rememberSteamViewModel(context)
    val appId = gameID.toString()
    val appDetails by viewModel.getAppDetailsFlow(appId).collectAsState()
    val isLoading by viewModel.getLoadingStateFlow(appId).collectAsState()
    val error by viewModel.getErrorFlow(appId).collectAsState()

    LaunchedEffect(gameID) {
        viewModel.fetchAppDetails(appId)
    }

    Box(
        modifier = Modifier
            .width(size * 1.4f)
            .height(size)
            .padding(8.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Background Image with Gradient Overlay
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(error)  // Add this case
                appDetails != null -> {
                    appDetails!!.headerImage?.let { GameHeroImage(gameID, it) }
                    Log.i("Lama", appDetails.toString())
                }
                else -> Log.i("aaaaaaa", "pomidor")  // This should now rarely trigger
            }


            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0.4f * size.value
                        )
                    )
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(error)  // Add this case
                appDetails != null -> {
                    GameDetailsCard(appDetails!!,viewModel)
                    Log.i("Lama", appDetails.toString())
                }
                else -> Log.i("aaaaaaa", "pomidor")  // This should now rarely trigger
            }
        }
    }
}

@Composable
private fun GameHeroImage(gameID: Int, imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Game cover image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun LoadingIndicator() {
    CircularProgressIndicator(
//        modifier = Modifier.align(Alignment.Center),
        color = Color.White
    )
}

@Composable
private fun ErrorMessage(error: String?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.8f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Error",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = error ?: "Unknown error occurred",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameDetailsCard(appDetails: AppData, viewModel: SteamViewModel) {
    val savedGameIds by viewModel.savedGameIds.collectAsState(initial = emptySet())
    val isSaved = savedGameIds.contains(appDetails.appId.toString())

    val error by viewModel.getErrorFlow(appDetails.appId.toString()).collectAsState()
    if (error != null) {
        Log.e("GameCard", "Error state for ${appDetails.appId}: $error")
    }
    Log.i("GamedetailsCard", "Error state for ${appDetails}:")



    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = appDetails.name ?: "Unnamed Game",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            if (isSaved) {
                                appDetails.appId?.let { viewModel.removeSavedGame(it) }
                            } else {
                                viewModel.saveGame(appDetails)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isSaved) "Remove from saved" else "Save game",
                            tint = if (isSaved) Color.Red else Color.White
                        )
                    }
                }
                // ... rest of your card content ...

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = appDetails.priceOverview?.finalFormatted?.let { "$it" } ?: "FREE",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF66C0F4) // Steam blue color
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = appDetails.shortDescription ?: "No description available",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun GameCardPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GameCard(
                gameID = 440,
            )
        }
    }
}


@Composable
fun SavedGameCardColumn(viewModel: SteamViewModel = rememberSteamViewModel()) {
    val savedGameIds by viewModel.savedGameIds.collectAsState(initial = emptySet())

    val finalGameIds by remember(savedGameIds) {
        derivedStateOf {
            savedGameIds.mapNotNull { it.toIntOrNull() }
        }
    }
    GameCardColumn(finalGameIds, viewModel = viewModel)
}


@Composable
fun TopGameCardColumn(viewModel: TopGamesViewModel = rememberTopSteamViewModel()) {
    val gameIds by remember { mutableStateOf(viewModel.gameIds) }


    GameCardColumn(gameIds)
}
