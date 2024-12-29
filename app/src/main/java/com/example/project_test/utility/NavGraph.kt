package com.example.project_test.utility

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.project_test.Screen
import com.example.project_test.screens.DemoScreen
import com.example.project_test.screens.FavoriteScreen
import com.example.project_test.screens.SettingsScreen
import com.example.project_test.utility.ScreenSelector.DisplayScreenSelector
import com.google.accompanist.navigation.animation.AnimatedNavHost


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    screenWidthDp: Dp,
    screenHeightDp: Dp
) {
    val density = LocalDensity.current
    val screenHeightPx = with(density) { screenHeightDp.toPx() } // Convert dp to px
    val selectorPosition = remember(screenHeightPx) {
        with(density) { (screenHeightPx * 0.79f).toDp() } - 25.dp // Scale position and subtract offset in dp
    }

    // Transition state trackers
    val transitionSpeed = 600
    var swipeRight by remember { mutableStateOf(false) }
    // Exit and enter transitions are dependent on the direction of the swipe
    val enterTransition: (Boolean) -> EnterTransition = {
        fadeIn(
            animationSpec = tween(transitionSpeed),
        )
    }
    val exitTransition: (Boolean) -> ExitTransition = {
        fadeOut(
            animationSpec = tween(transitionSpeed),
        )
    }


    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Favorites.route,
    ) {
        composable(
            route = Screen.Settings.route,
            enterTransition = { enterTransition(swipeRight) },
            exitTransition = { exitTransition(swipeRight) },
        ) {
            SettingsScreen()
        }
        composable(
            route = Screen.Favorites.route,
            enterTransition = { enterTransition(swipeRight) },
            exitTransition = { exitTransition(swipeRight) },
        ) {
            FavoriteScreen(
                navController,
            )
        }
        composable(
            route = Screen.Demo.route,
            enterTransition = { enterTransition(swipeRight) },
            exitTransition = { exitTransition(swipeRight) },
        ) {
            DemoScreen(
                navController,
            )
        }
    }

    // Screen selector container (NB must be rendered after NavGraph set-up)
    Box(
        modifier = Modifier
            .width(screenWidthDp)
            .height(screenWidthDp)
            .offset(y = selectorPosition + 10.dp) // Position view selector at bottom of screen
    ) {
        DisplayScreenSelector(
            navController,
            screenWidthDp,
            onSwipe = { isRightSwipe ->
                swipeRight = isRightSwipe
            },
        )
    }
}