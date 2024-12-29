package com.example.project_test.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_test.ui.theme.PLantBuddiesTheme

@Composable
fun SearchBar(
    onTextChanged: (String) -> Unit,
    onSearch: (String) -> Unit = {}) {
    var text by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onTextChanged(it) // Callback to return the search query
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(text)
                }
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
                .padding(10.dp)
                .padding(start = 5.dp, end = 5.dp)
                .align(Alignment.Center),
        )
        if (text.isEmpty()) {
            Text(
                text = "Search",
                style = TextStyle(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 18.dp),
            )
        }
        IconButton( // "Reset query" button
            onClick = {
                text = ""
                onTextChanged(text)
                      },
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Reset search query",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(30.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var searchQuery by remember { mutableStateOf("") }
    PLantBuddiesTheme {
        SearchBar(onTextChanged = { enteredText ->
            searchQuery = enteredText
        })
    }
}