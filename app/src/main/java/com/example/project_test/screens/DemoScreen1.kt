package com.example.project_test.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project_test.R
import com.example.project_test.ui.theme.PLantBuddiesTheme

@Composable
fun DemoScreen(
    navController: NavController,
) {
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
                text = stringResource(id = R.string.demo_screen),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(45.dp)
                .padding(top = 200.dp)
        ) {
            Text(
                text = "This is a demo screen.",
                style = TextStyle(color = MaterialTheme.colorScheme.tertiary),
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun DemoScreenPreview() {
    PLantBuddiesTheme {
        DemoScreen(
            rememberNavController()
            )
    }
}