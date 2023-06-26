package com.example.karaoke_note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.karaoke_note.ui.theme.Karaoke_noteTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Karaoke_noteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
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
                    LatestList()
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LatestList() {
    Column {
        ListItem(
            text = { Text("Three line list item") },
            secondaryText = {
                Text("Long secondary text for the current list item")
            },
            singleLineSecondaryText = false,
            trailing = { Text("meta") }
        )
        Divider()
        ListItem(
            text = { Text("Three line list item") },
            overlineText = { Text(text = "OVER-LINE") },
            secondaryText = { Text(text = "Secondary Text") }
        )
        Divider()
        ListItem(
            text = { Text("Three line list item with 24x24 icon.") },
            secondaryText = { Text("This is a long secondary text for the current list item " +
                    "displayed on two lines") },
            singleLineSecondaryText = false,
            icon = {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null
                )
            }
        )
        Divider()
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