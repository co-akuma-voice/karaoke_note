package com.example.karaoke_note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karaoke_note.ui.theme.Karaoke_noteTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Karaoke_noteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
/*                    val navController = rememberNavController()
                    Scaffold(
                        topBar = {
                            AppBar(navController)
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController,
                            startDestination = "home",
                            Modifier.padding(paddingValues)
                        ) {
                            composable("home") {
                                Home(navController)
                            }
                            composable("song_data") {
                                Greeting("song_data", Modifier.padding(paddingValues))
                            }
                        }
                    }
*/
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
                    //LatestCard(20)
                    //BottomBar()
                }
            }
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

sealed class BottomBarItem(var dist: String, var icon: ImageVector) {
    object Latest : BottomBarItem("Latest", Icons.Filled.Home)
    object Lists : BottomBarItem("Lists", Icons.Filled.List)
}
@ExperimentalMaterial3Api
@Composable
fun BottomBar() {
    val selectedItem = remember { mutableStateOf(0) }
    val items = listOf(BottomBarItem.Latest, BottomBarItem.Lists)

    BottomNavigation {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.dist) },
                label = { Text(item.dist) },
                selected = selectedItem.value == index,
                selectedContentColor = Color.White,
                onClick = { selectedItem.value = index }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Karaoke_noteTheme {
        Greeting("Android")
    }
}