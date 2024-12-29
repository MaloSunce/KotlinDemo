package com.example.project_test.utility

import android.annotation.SuppressLint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.project_test.R
import com.example.project_test.Screen
import com.example.project_test.ui.theme.PLantBuddiesTheme

// Screen selection wheel
object ScreenSelector {
    @SuppressLint("StaticFieldLeak")
    private var _wheelIndex = mutableIntStateOf(0)
    private var wheelIndex: Int
        get() = _wheelIndex.intValue
        set(value) {
            _wheelIndex.intValue = value
        }

    // Function to update wheelIndex safely
    fun incrementWheelIndex(indexIncrement: Int) {
        _wheelIndex.intValue += indexIncrement
    }

    @Composable
    fun DisplayScreenSelector(
        navController: NavController,
        screenWidthDp: Dp,
        onSwipe: (Boolean) -> Unit,
    ) {
        val context = LocalContext.current
        val screenWidthPx = with(LocalDensity.current) { screenWidthDp.toPx() }
        val fontSize = screenWidthPx * 0.07f
        val fontColor = MaterialTheme.colorScheme.onSurface.toArgb()
        val fontColorSelected = MaterialTheme.colorScheme.onBackground.toArgb()

        // Retrieve all screen titles from string resources
        val screens: List<String> = buildList {
            add(context.getString(R.string.favorites))
            add(context.getString(R.string.settings))
            add(context.getString(R.string.demo_screen))
            add(context.getString(R.string.demo_screen))
            add(context.getString(R.string.demo_screen))
        }

        // Screen selector state trackers
        var currentScreen by remember { mutableIntStateOf(0) }
        val numOfScreens = screens.size
        val titleAngle = (360f / numOfScreens) // Angle between each screen title on the selector

        // Animated angle based on the currently selected screen
        val rotationAngle by animateFloatAsState(
            targetValue = wheelIndex.toFloat() * titleAngle,
            animationSpec = tween(
                durationMillis = 700,
                delayMillis = 50,
                easing = LinearOutSlowInEasing
            ),
            label = "selector_wheel_angle"
        )

        // Bidirectional swipe handler to increment or decrements [currScreen]
        var totalDragAmount by remember { mutableFloatStateOf(0f) } // Drag distance tracker
        val swipeHandler = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    totalDragAmount = 0f
                },
                onDrag = { change, dragAmount ->
                    totalDragAmount += dragAmount.x
                    change.consume()
                },
                onDragEnd = {
                    if (totalDragAmount > 100) { // Decrement on left swipe
                        onSwipe(false)
                        incrementWheelIndex((wheelIndex - 1 + numOfScreens) % numOfScreens)
                        //wheelIndex -= 1
                    } else if (totalDragAmount < -100) { // Increment on right swipe
                        onSwipe(true)
                        incrementWheelIndex((wheelIndex + 1) % numOfScreens)
                        //wheelIndex += 1
                    }
                }
            )
        }

        // Ensuring that [currentScreen] always stays within 0 to [numOfScreens]
        currentScreen = if (wheelIndex < 0) {
            (wheelIndex % numOfScreens + numOfScreens) % numOfScreens
        } else {
            wheelIndex % numOfScreens
        }


        // Screen navigation
        val screenRoutes = listOf(
            Screen.Favorites.route,
            Screen.Settings.route,
            Screen.Demo.route,
        )
        // TODO Toast on error, favorites currently set as default route
        val route = screenRoutes.getOrNull(currentScreen) ?: Screen.Favorites.route
        navController.navigate(route)


        Box( // Container to stack two circles
            modifier = Modifier
                .width(screenWidthDp) // Wheel size = screen width
                .aspectRatio(1f)
                .then(swipeHandler), // Bind swipeHandler to container
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                // Larger, lighter circle
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                modifier = Modifier
                    .fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.outline,
                ),
                shape = CircleShape,
            ) {}
            Box( // Smaller, darker circle
                modifier = Modifier
                    .size(screenWidthDp * 0.65f)
                    .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
            // Sunflower icon
            Image(
                painter = painterResource(R.drawable.sunflower),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(screenWidthDp * 0.67f)
            )

            // Draw all the screen titles along the wheel
            val paint = Paint().asFrameworkPaint()
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                paint.apply {
                    isAntiAlias = true
                    textSize = fontSize
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    color = fontColor
                }

                // Calculate center point of circles
                val center = size.width / 2

                // Predefine positions
                val titlePositions = floatArrayOf(0f, 60f, 120f, 240f, 300f)

                // Render each screen title
                for (i in 0 until numOfScreens) {
                    // Highlight current page
                    if (i != currentScreen) {
                        paint.apply {
                            color = fontColor
                        }
                    } else {
                        paint.apply {
                            color = fontColorSelected
                        }
                    }

                    drawIntoCanvas {
                        val path = Path()
                        val arcRectF = RectF( // Define text curve
                            center - screenWidthPx / 2.7f, // left
                            center - screenWidthPx / 2.7f, // top
                            center + screenWidthPx / 2.7f, // right
                            center + screenWidthPx / 2.7f // bottom
                        )

                        // Starting angle for each title, which is spaced evenly
                        // from the previous elements starting angle, and dependent on
                        // the animated [rotationAngle]. A blank slot is left at the bottom of the
                        // wheel to allow for 3 titles to appear on the screen while to stays below
                        // the view port.

                        // continuousIndex ensures that each screen title is paired with the correct
                        // starting position from titlePositions
                        val continuousIndex = (i - rotationAngle / titleAngle).mod(numOfScreens.toDouble())
                        // adjustedContinuousIndex ensures that the index is always positive
                        val adjustedContinuousIndex = if (continuousIndex < 0) continuousIndex + numOfScreens else continuousIndex
                        val currIndexInt = adjustedContinuousIndex.toInt() // Convert current index to int
                        val nextIndex = (currIndexInt + 1).mod(numOfScreens) // Determine next index
                        val indexRem = adjustedContinuousIndex - currIndexInt // Get remainder from index

                        val baseAngle = titlePositions[currIndexInt] // Get angle of current base position
                        val nextAngle = if (nextIndex == 0 && currIndexInt == numOfScreens - 1) { // Check if wrap-around necessary
                            titlePositions[nextIndex] + 360f // Wrap around
                        } else {
                            titlePositions[nextIndex] // Otherwise use next angle
                        }
                        // Interpolate between current and next angle
                        val interpolatedAngle = baseAngle * (1 - indexRem) + nextAngle * indexRem
                        val offset =  22 - screens[i].length // Equalize length of each title
                        // Convert to float and ensure correct positioning along wheel
                        val startAngle = (interpolatedAngle + offset).toFloat() - 125f

                        // Render current screen title
                        path.addArc(arcRectF, startAngle, titleAngle)
                        it.nativeCanvas.drawTextOnPath(screens[i], path, 0f, 0f, paint)
                    }
                }
            }
            // Clickable boxes over the three visible titles to increment/decrement wheelIndex
            Box(
                modifier = Modifier // Left box
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onSwipe(false)
                        wheelIndex--
                    }
                    .align(Alignment.CenterStart)
                    .height(screenWidthDp * 0.8f)
                    .width(screenWidthDp * 0.3f)
            )
            Box(
                modifier = Modifier // Right box
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onSwipe(true)
                        wheelIndex++
                    }
                    .align(Alignment.CenterEnd)
                    .height(screenWidthDp * 0.8f)
                    .width(screenWidthDp * 0.3f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScreenSelectorLargePreview() {
    val navController: NavHostController = rememberNavController()
    ScreenSelector

    PLantBuddiesTheme {
        ScreenSelector.DisplayScreenSelector(
            navController = navController,
            screenWidthDp = 400.dp,
            onSwipe = {},
        )
    }
}