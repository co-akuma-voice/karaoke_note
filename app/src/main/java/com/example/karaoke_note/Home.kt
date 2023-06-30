package com.example.karaoke_note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@ExperimentalMaterial3Api
@Composable
fun Home(navController: NavController) {
    Column {
        Box(
            modifier = Modifier
                .weight(1f)
        ){
            Button(onClick = { navController.navigate("song_data") }) {
                Text("Navigate to song_data")
            }
        }
        Box(
            modifier = Modifier
                .weight(8f)
        ) {
            LazyColumn(
                modifier = Modifier
            ) {
                item {
                    LatestCard(
                        date = "1996/08/17",
                        title = "長いタイトル長いタイトル長いタイトル長いタイトル",
                        artist = "長いアーティスト長いアーティスト長いアーティスト長いアーティスト",
                        score = 98.76543,
                        key = -6,
                        comment = "テストテスト"
                    )
                }
                item {
                    LatestCard(
                        date = "2023/06/24",
                        title = "1 2 3 ~恋が始まる~",
                        artist = "いきものがかり",
                        score = 100.000,
                        key = -2,
                        comment = ""
                    )
                }
                item {
                    LatestCard(
                        date = "2023/06/24",
                        title = "ARIA",
                        artist = "Kalafina",
                        score = 90.672,
                        key = -1,
                        comment = "-1で試す。"
                    )
                }
                items(5) {
                    LatestCard(
                        date = "2023/06/24",
                        title = "星月夜",
                        artist = "由薫",
                        score = 90.919,
                        key = -3,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(2f)
        ) {
            BottomNavigationBar(navController)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun LatestCard(date: String, title: String, artist: String, score: Double, key: Int, comment: String = "") {
    remember { mutableStateOf(false) }
    var commentforcard = ""
    if (comment.isNotEmpty()) {
        commentforcard = "..."
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Card(
            onClick = { },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(Color(0xffffffff)),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Row (
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(3f),
                    //.background(Color.Green)
                ) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = date,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                .align(Alignment.Start),
                            color = Color.Gray,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 6.sp,
                        )
                        Text(
                            text = title,
                            modifier = Modifier
                                .padding(start = 20.dp, top = 4.dp, bottom = 4.dp)
                                .align(Alignment.Start),
                            color = Color.DarkGray,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = artist,
                            modifier = Modifier
                                .padding(start = 20.dp, top = 4.dp, bottom = 4.dp)
                                .align(Alignment.Start),
                            color = Color.Gray,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 8.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .padding(top = 6.dp),
                    //.background(Color.Yellow),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = String.format("%.3f", score),
                            modifier = Modifier
                                .padding(top = 0.dp, end = 16.dp, bottom = 2.dp)
                                .align(Alignment.End),
                            color = Color.DarkGray,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = key.toString(),
                            modifier = Modifier
                                .padding(top = 2.dp, end = 16.dp, bottom = 2.dp)
                                .align(Alignment.End),
                            color = Color.Red,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                        )
                        Text(
                            text = commentforcard,
                            modifier = Modifier
                                .padding(top = 2.dp, end = 16.dp, bottom = 8.dp)
                                .align(Alignment.End),
                            color = Color.DarkGray,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                        )
                    }
                }
            }
        }
    }
}

/*
data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
)

@ExperimentalMaterial3Api
@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    val bottomNavItems = listOf(
        BottomNavItem(
            name = "Home",
            route = "home",
            icon = Icons.Filled.Home,
        ),
        BottomNavItem(
            name = "List",
            route = "list",
            icon = Icons.Filled.List,
        )
    )

    NavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(text = item.name) },
                selected = selectedItem == index,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}*/