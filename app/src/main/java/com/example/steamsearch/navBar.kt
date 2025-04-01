package com.example.steamsearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.steamsearch.ui.theme.SteamDarkBlue
import com.example.steamsearch.ui.theme.SteamLightBlue

@Composable
fun NavBar(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val navBarHeight = screenHeight / 12

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(navBarHeight)
                .background(SteamDarkBlue) // Add background to navbar
                .padding(horizontal = 16.dp, vertical = 8.dp) // Padding for better spacing
        ) {
            NavButton(
                iconRes = R.drawable.baseline_home_24,
                contentDescription = "Home",
                height = navBarHeight,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("home") }
            )

            NavButton(
                iconRes = R.drawable.baseline_build_24,
                contentDescription = "Search",
                height = navBarHeight,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("search") }
            )

            NavButton(
                iconRes = R.drawable.baseline_bookmark_24,
                contentDescription = "Bookmarks",
                height = navBarHeight,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("bookmarks") }
            )
        }
    }
}

@Composable
fun NavButton(
    iconRes: Int,
    contentDescription: String,
    height: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .clip(shape = RoundedCornerShape(20.dp))
            .height(height)
            .background(SteamDarkBlue)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}