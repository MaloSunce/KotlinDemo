package com.example.project_test.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_test.R
import com.example.project_test.ui.theme.PLantBuddiesTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary, 
                        MaterialTheme.colorScheme.primary
                    ),
                    center = Offset.Unspecified,
                    radius = 2500f
                )
            )
    ) {
        LeafAnimation()
        Image(
            painter = painterResource(R.drawable.sunflower_icon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp)
        )
    }
}


// Leaf animation sequence - animates the alpha values of 3 or 4 leafs
@Composable
fun LeafAnimation() {
    // Image state tracker
    val currentImageIndex = remember { mutableIntStateOf(0) }
    val duration = 500

    // Animation timing
    LaunchedEffect(Unit) {
        for (i in 0..5) {
            delay(duration.toLong())   // Wait before showing the next image
            currentImageIndex.intValue = i
        }
    }

    // Displaying the images
    Box(modifier = Modifier.fillMaxSize()) {
        val firstLeafAlpha by animateFloatAsState(
            targetValue = if (currentImageIndex.intValue > 0) 1f else 0f,
            animationSpec = tween(durationMillis = duration),
            label = ""
        )

        val secondLeafAlpha by animateFloatAsState(
            targetValue = if (currentImageIndex.intValue > 1) 1f else 0f,
            animationSpec = tween(durationMillis = duration),
            label = ""
        )

        val thirdLeafAlpha by animateFloatAsState(
            targetValue = if (currentImageIndex.intValue > 2) 1f else 0f,
            animationSpec = tween(durationMillis = duration),
            label = ""
        )

        val fourthLeafAlpha by animateFloatAsState(
            targetValue = if (currentImageIndex.intValue > 3) 1f else 0f,
            animationSpec = tween(durationMillis = duration),
            label = ""
        )

        Image( // First leaf
            painter = painterResource(id = R.drawable.leaf),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.Center)
                .offset(y = 150.dp, x = 45.dp)
                .alpha(firstLeafAlpha)
        )

        Image( // Second leaf
            painter = painterResource(id = R.drawable.leaf),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .offset(y = 230.dp, x = (-45).dp)
                .rotate(-10f)
                .graphicsLayer { scaleX = -1f }
                .alpha(secondLeafAlpha)
        )
        Image( // Colored tint, second leaf
            painter = painterResource(id = R.drawable.leaf),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .offset(y = 230.dp, x = (-45).dp)
                .rotate(-10f)
                .graphicsLayer { scaleX = -1f }
                .alpha(secondLeafAlpha*0.3f)
        )

        Image( // Third leaf
            painter = painterResource(id = R.drawable.leaf),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center)
                .offset(y = 300.dp, x = 40.dp)
                .rotate(15f)
                .alpha(thirdLeafAlpha)
        )
        Image( // Colored tint, third leaf
            painter = painterResource(id = R.drawable.leaf),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center)
                .offset(y = 300.dp, x = 40.dp)
                .rotate(15f)
                .alpha(thirdLeafAlpha*0.5f)
        )
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun SplashScreenPreview() {
    PLantBuddiesTheme {
        SplashScreen()
    }
}