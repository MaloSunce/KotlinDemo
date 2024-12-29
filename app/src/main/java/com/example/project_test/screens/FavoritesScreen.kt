package com.example.project_test.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project_test.R
import com.example.project_test.data.FavoritePlant
import com.example.project_test.data.FavoritePlants
import com.example.project_test.ui.theme.PLantBuddiesTheme
import com.example.project_test.utility.SearchBar
import com.example.project_test.utility.fuzzySearch
import kotlinx.coroutines.launch


// Favorite flowers screen
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FavoriteScreen(
    navController: NavController,
) {
    val plants = remember { mutableStateListOf<FavoritePlant>().apply {
        addAll(FavoritePlants.plants)
    }}

    var editPlantID by remember { mutableStateOf<String?>(null) }
    val removedPlants = remember { mutableStateListOf<FavoritePlant>().apply {
        addAll(FavoritePlants.removedPlants)
    }}

    // Search bar state trackers
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var fuzzResult by remember { mutableStateOf(FavoritePlants.favoriteNames) }


    Card(
        modifier = Modifier
            .fillMaxSize(0.9f)
            .padding(vertical = 50.dp, horizontal = 25.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Gray.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 1f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .padding(top = 10.dp)
                .padding(start = 10.dp)
        ) {
            Text( // Screen title
                text = stringResource(id = R.string.favorites),
                style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                fontSize = 30.sp,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            IconToggleButton(
                checked = showSearchBar,
                onCheckedChange = {
                    showSearchBar = !showSearchBar
                }
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
                    fuzzResult = FavoritePlants.favoriteNames // Reset fuzzResults upon closing search bar
                } else if (showSearchBar and searchQuery.isNotEmpty()){
                    fuzzResult = fuzzySearch(searchQuery,FavoritePlants.favoriteNames) as MutableList<String>
                    val matchingPlants = plants.filter { plant ->
                        plant.name.contains(searchQuery.trim(), ignoreCase = true)
                    }
                    fuzzResult += matchingPlants.map { it.name }
                }
                if (targetState) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .padding(bottom = 10.dp)
                    ) {
                        SearchBar(onTextChanged = { enteredText ->
                            searchQuery = enteredText
                            fuzzResult = fuzzySearch(searchQuery, FavoritePlants.favoriteNames) as MutableList<String>
                            val matchingPlants = plants.filter { plant ->
                                plant.name.contains(searchQuery.trim(), ignoreCase = true)
                            }
                            fuzzResult += matchingPlants.map { it.name }
                        })
                    }
                }
                Divider(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                )
            }
        }

        // Default view for empty favorites list
        if ((FavoritePlants.removedPlants.size == plants.size) or plants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
                    .padding(top = 200.dp)
            ) {
                Text(
                    text = "You have not added any favorites yet",
                    style = TextStyle(color = MaterialTheme.colorScheme.tertiary),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Scrollable column containing all the favorite items
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                itemsIndexed(plants) { _, plant ->
                    AnimatedVisibility(
                        visible = !removedPlants.contains(plant) &&
                                (fuzzResult.contains(plant.name) || fuzzResult.contains(plant.latinName)),
                        enter = expandVertically(),
                        exit = shrinkVertically(animationSpec = tween(durationMillis = 1000))
                    ) {
                        FavColumnItem(
                            navController = navController,
                            editPlantID = editPlantID,
                            plant = plant,
                            onEditPlant = {plantId ->
                                editPlantID = plantId.toString()
                            },
                            removedPlants
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(300.dp)) // Extra whitespace at bottom
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavColumnItem(
    navController: NavController,
    editPlantID: String?,
    plant: FavoritePlant,
    onEditPlant: (Int?) -> Unit,
    removedPlants: MutableList<FavoritePlant>
    ) {
    val isFavorite = remember { mutableStateOf(true) }
    val nameReset = remember { mutableStateOf(false) }
    val isExpanded = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card( // Outer container
            onClick = { isExpanded.value = !isExpanded.value },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 15.dp, vertical = 12.dp)
                .offset(30.dp)
                .shadow(
                    elevation = 7.dp,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(100.dp)
                    .background( // Slight gradient
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    )
                    .padding(10.dp)
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .offset(x = 50.dp)
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (editPlantID != plant.id.toString()) { // Display plant name and edit button
                        var name = plant.nickname.ifEmpty { plant.name } // Display nickname if the plant has one

                        if (nameReset.value) {
                            name = plant.name
                            nameReset.value = false
                        }

                        // Check character limit
                        val textMeasurer = rememberTextMeasurer()
                        val textStyle = TextStyle(fontSize = 20.sp)
                        var textWidth = textMeasurer.measure(
                            text = name,
                            style = textStyle
                        ).size.width
                        var _name = name

                        val maxWidthPx = 500
                        if (textWidth > maxWidthPx) {
                            while (textWidth > maxWidthPx && _name.isNotEmpty()) {
                                _name = _name.dropLast(1) // Remove last character
                                textWidth = textMeasurer.measure(
                                    text = _name,
                                    style = textStyle
                                ).size.width
                            }
                            _name = "$_name..."
                        } else {
                            _name = name
                        }

                        Text(
                            text = _name, // Character limit
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier,
                            style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit plant name button",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    onEditPlant(plant.id)
                                }
                        )
                    } else { // Display text field for editing plant name
                        onEditPlant(plant.id)
                        EditPlantName(
                            plant = plant,
                            onClose = {
                                onEditPlant(null)
                            }
                        )
                    }
                }
                Text(
                    text = plant.latinName,         // Latin name
                    fontSize = 15.sp,
                    modifier = Modifier
                        .offset(x = 50.dp, y = 28.dp)
                        .basicMarquee(),
                    style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                )
                Text(
                    text = plant.family,            // Family
                    fontSize = 15.sp,
                    modifier = Modifier
                        .offset(x = 50.dp, y = 50.dp)
                        .basicMarquee(),
                    style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                )

                Box(                                // Bookmark icon
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                ) {
                    IconToggleButton(
                        checked = isFavorite.value,
                        onCheckedChange = { // Remove plant from favorites
                            coroutineScope.launch() {
                                isFavorite.value = !isFavorite.value
                                if (!isFavorite.value) {
                                    val error = FavoritePlants.removeFavorite(plant) // Tro to add plant to [removedPlants]
                                    if (error != null) {
                                        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                                    } else {
                                        removedPlants.add(plant)
                                    }
                                }
                                if (editPlantID == plant.id.toString()) {
                                    onEditPlant(null)
                                }
                                FavoritePlants.writeToFile()
                            }
                        }
                    ) {
                        Icon( // Fill
                            Icons.Filled.Bookmark,
                            contentDescription = "Favorite plant button, fill",
                            tint = if (isFavorite.value) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                        Icon( // Outline
                            Icons.Outlined.BookmarkBorder,
                            contentDescription = "Favorite plant button, outline",
                            tint = if (isFavorite.value) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
            AnimatedVisibility( // Expand card to reveal more information
                visible = isExpanded.value,
                enter = expandVertically(animationSpec = tween(durationMillis = 1000)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 1000))
            ) {
                ExpandFavItem(
                    navController,
                    plant,
                    onNicknameReset = {
                        nameReset.value = true
                    }
                )
            }
        }
        // Use image if available
        if (plant.imageURL >= 0) {
            Image(
                painter = painterResource(plant.imageURL),
                contentDescription = "Image of ${plant.name}",
                modifier = Modifier
                    .padding(start = 15.dp, top = 25.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .size(80.dp),
                contentScale = ContentScale.Crop
            )
        } else { // Otherwise use placeholder image
            Image(
                painter = painterResource(R.drawable.flower_placeholder),
                contentDescription = "Placeholder image of flower",
                modifier = Modifier
                    .padding(start = 15.dp, top = 25.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(15.dp)
                    .size(50.dp)
            )
        }
    }
}

@Composable
fun ExpandFavItem(
    navController: NavController,
    plant: FavoritePlant,
    onNicknameReset: () -> Unit,
    ) {
    val context = LocalContext.current

    Card( // Outer container
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 15.dp)
            .padding(bottom = 15.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        )
        {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CardTextItem(
                    title = "Other names",
                    content = if (plant.otherNames.isEmpty()) "None" else plant.otherNames.joinToString(", "),
                    modifier = Modifier.weight(1f)
                )
                if (plant.nickname != "" && plant.nickname != plant.name) { // Only display button if the plant has a nickname
                    OutlinedButton(
                        modifier = Modifier
                            .width(100.dp)
                            .height(30.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = { // Reset nickname
                            val error = FavoritePlants.editPlantName( plant, "")
                            if (error != null) {
                                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                            } else {
                                onNicknameReset()
                            }
                        }
                    ) {
                        Text(
                            text = "Reset nickname",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            val origins = plant.origin?.joinToString(", ") ?: ""
            val flowerColor = plant.flowerColor ?: ""
            val leafColor = plant.leafColor ?: ""

            CardTextItem("Native to", origins, Modifier)
            if (flowerColor != "") {
                CardTextItem("Flower colors", flowerColor, Modifier)
            }
            if (leafColor != "") {
                CardTextItem("Leaf colors", leafColor, Modifier)
            }
        }
        OutlinedButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 10.dp)
                .padding(end = 10.dp),
            onClick = { // Navigate to DetailsScreen
                Toast.makeText(
                    context,
                    "This functionality is not included in the demo.",
                    Toast.LENGTH_LONG).show()
                //navController.navigate(Screen.PlantInfo.withArgs(plant.id.toString()))
                //ScreenSelector.incrementWheelIndex(-2)
            }
        ) {
            Text(
                text = "View plant",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Composable
fun CardTextItem(title: String, content: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
    ) {
        Text(           // Title
            text = "$title ",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
        Text(           // Content
            text = content,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 2.dp),
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}

// Text edit field to change favorite plant name
@Composable
fun EditPlantName(plant: FavoritePlant, onClose: () -> Unit) {
    var nicknameText by remember { mutableStateOf(plant.nickname.ifEmpty { plant.name }) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .height(25.dp)
            .fillMaxWidth(0.85f)
    ) {
        BasicTextField(
            value = nicknameText,
            onValueChange = {
                if (it.length <= 20) { // Character limit
                    nicknameText = it
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
                .background(MaterialTheme.colorScheme.secondary),
            textStyle = TextStyle(
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            singleLine = true,
        )
        Icon(
            Icons.Outlined.Check,
            contentDescription = "Exit plant name edit",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .clickable {
                    onClose()
                }
        )
    }
    if (nicknameText != plant.name) { // Only update nickname if it was changed
        val error = FavoritePlants.editPlantName(plant, nicknameText)
        if (error != null) {
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun FavoriteScreenPreview() {
    PLantBuddiesTheme {
        FavoriteScreen(rememberNavController())
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun ExpandFavItemPreview() {
    PLantBuddiesTheme {
        ExpandFavItem(
            rememberNavController(),
            FavoritePlants.plants[0],
            onNicknameReset = {})
    }
}

