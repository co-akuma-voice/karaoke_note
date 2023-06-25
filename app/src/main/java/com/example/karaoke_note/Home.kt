package com.example.karaoke_note

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun Home(navController: NavController) {
    Column {
        Greeting("Android")
        Greeting("Android")
        Button(onClick = { navController.navigate("song_data") }) {
            Text("Navigate to song_data")
        }
    }
}