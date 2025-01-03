package com.example.project_test.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project_test.R
import com.example.project_test.data.FavoritePlant
import com.example.project_test.data.testPlants
import com.example.project_test.ui.theme.PLantBuddiesTheme
import com.example.project_test.utility.CheckConnectivityStatus
import com.example.project_test.utility.NoConnectionContent
import com.example.project_test.utility.ScreenSelector
import com.example.project_test.utility.SearchBar

// Watering activity
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AllPlantsScreen(navController: NavController) {
    val connection = CheckConnectivityStatus()
    var pageNumber by remember { mutableIntStateOf(1) }

    // Search bar state trackers
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp, horizontal = 25.dp)
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Gray.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
    )
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp, horizontal = 25.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp)
            ) {
                Text( // Screen title
                    text = LocalContext.current.getString(R.string.all_plants),
                    style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(20.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                IconToggleButton(
                    checked = showSearchBar,
                    onCheckedChange = {
                        showSearchBar = !showSearchBar
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
            }

            AnimatedContent(
                targetState = showSearchBar,
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) + slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { -it }
                    ) with fadeOut(animationSpec = tween(600)) + slideOutVertically(
                        animationSpec = tween(600),
                        targetOffsetY = { -it }
                    )
                },
                label = "Main content animation"
            ) { targetState ->
                Column {
                    if (!showSearchBar or searchQuery.isEmpty()) {
                        searchQuery = "" // Reset query upon closing search bar
                    }

                    if (targetState) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp)
                        ) {
                            SearchBar(
                                onTextChanged = { enteredText ->
                                    searchQuery = enteredText
                                },
                                onSearch = { query ->
                                    // TODO add fuzzy search
                                }
                            )
                        }
                    }
                    Divider(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                    )
                }
            }

            if (connection) { // Display main content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                ) {
                    Column {
                        // Scrollable column containing all the favorite items
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(testPlants.size) { index ->
                                FavColumnItem(
                                    plant = testPlants[index],
                                    navController = navController
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(200.dp)) // Extra whitespace at bottom
                            }
                        }
                    }
                }
            } else { // Display error message if no wifi connection
                NoConnectionContent()
            }
        }
    }
}


@Composable
fun FavColumnItem(navController: NavController, plant: FavoritePlant) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // TODO add toast
                ScreenSelector.incrementWheelIndex(-1)
            },
    ) {
        Row(
            modifier = Modifier
                .padding(start = 20.dp)
                .height(100.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                plant.id
                Text(
                    text = plant.name,              // Name
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .offset(y = 5.dp)
                        .basicMarquee()
                )
                if (plant.latinName.isNotEmpty()) {
                    Text(
                        text = plant.latinName,         // Latin name
                        fontSize = 15.sp,
                        modifier = Modifier.offset(y = 7.dp),
                        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    )
                }
                if (plant.otherNames.isNotEmpty()) {
                    Text(
                        text = plant.otherNames[0],            // Family
                        fontSize = 15.sp,
                        modifier = Modifier.offset(y = 12.dp),
                        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    )
                }
            }
            if (plant.imageURL >= 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Image(
                        painter = painterResource(plant.imageURL),
                        contentDescription = "Image of ${plant.name}",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 1f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0f),
                                    )
                                )
                            )
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .fillMaxHeight()
                ) {
                    Image(
                        painter = painterResource(R.drawable.flower_placeholder),
                        contentDescription = "Placeholder image of flower",
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(15.dp)
                            .fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 1f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0f),
                                    )
                                )
                            )
                    )
                }
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 15.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun AllPlantsScreenPreview() {
    PLantBuddiesTheme {
        AllPlantsScreen(
            rememberNavController()
        )
    }
}
