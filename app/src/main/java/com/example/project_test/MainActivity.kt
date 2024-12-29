package com.example.project_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.example.project_test.data.FavoritePlants
import com.example.project_test.data.ThemeManager
import com.example.project_test.screens.SplashScreen
import com.example.project_test.ui.theme.PLantBuddiesTheme
import com.example.project_test.utility.ScreenSelector
import com.example.project_test.utility.SetupNavGraph
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private var loading by mutableStateOf(true)

    private fun initApp() {
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()

            withContext(Dispatchers.IO) {
                ScreenSelector                                         // Initialise screen selector
                ThemeManager.intiColorTheme(this@MainActivity) // Initialise light/dark mode
                FavoritePlants.initFavorites(applicationContext) // Initialise favorite plants
            }

            // Ensure that the splash screen displays for minimum 3 seconds
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = 4000 - elapsedTime // TODO tweak value

            if (remainingTime > 0) {
                delay(remainingTime)
            }
            loading = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialise theme manager before splash screen to ensure correct theme
        ThemeManager.intiColorTheme(applicationContext)

        initApp()

        setContent {
            Crossfade(targetState = loading, label = "") { loading ->
                if (loading) {
                    SplashScreen() // Display splash screen while loading
                } else {
                    PLantBuddiesTheme(colorTheme = ThemeManager.colorTheme) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            // Initialise navigation controller
                            navController = rememberAnimatedNavController()

                            // Get screen dimensions
                            val configuration = LocalConfiguration.current
                            val screenWidthDp = configuration.screenWidthDp.dp
                            val screenHeightDp = configuration.screenHeightDp.dp

                            SetupNavGraph(   // Initialise navigation graph
                                navController = navController,
                                screenWidthDp = screenWidthDp,
                                screenHeightDp = screenHeightDp
                            )
                        }
                    }
                }
            }
        }
    }
}



