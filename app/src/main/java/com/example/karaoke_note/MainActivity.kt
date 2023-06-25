package com.example.karaoke_note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.karaoke_note.ui.theme.Karaoke_noteTheme

class MainActivity : ComponentActivity() {
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
                    val canPop = remember { mutableStateOf(false) }
                    LaunchedEffect(navController) {
                        navController.addOnDestinationChangedListener { _, _, _ ->
                            canPop.value = navController.previousBackStackEntry != null
                        }
                    }
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("My App") },
                                navigationIcon = {
                                    if (canPop.value) {
                                        IconButton(onClick = { navController.navigateUp() }) {
                                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                                        }
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        NavHost(navController, startDestination = "home", Modifier.padding(paddingValues)) {
                            composable("home") {
                                Column {
                                    Greeting("Android")
                                    Greeting("Android")
                                    Button(onClick = { navController.navigate("song_data") }) {
                                        Text("Navigate to song_data")
                                    }
                                }
                            }
                            composable("song_data") {
                                Greeting("song_data", Modifier.padding(paddingValues))
                            }
                        }
                    }
                }
            }
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