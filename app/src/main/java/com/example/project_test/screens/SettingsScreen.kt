package com.example.project_test.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_test.R
import com.example.project_test.data.ThemeManager.colorTheme
import com.example.project_test.data.ThemeManager.toggleTheme
import com.example.project_test.ui.theme.PLantBuddiesTheme

// Compost activity
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxSize(0.9f)
            .padding(vertical = 50.dp, horizontal = 25.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .padding(top = 15.dp)
                .padding(start = 10.dp)
        ) {
            Text( // Screen title
                text = stringResource(id = R.string.settings),
                style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                fontSize = 30.sp,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
        }
        Divider(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 15.dp)
        )
        Column(
            modifier = Modifier
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = "Dark mode",
                    style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Switch(
                    checked = colorTheme,
                    onCheckedChange = {
                        toggleTheme(context)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun SettingsScreenPreview() {
    PLantBuddiesTheme {
        SettingsScreen()
    }
}