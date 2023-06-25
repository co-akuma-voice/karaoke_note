package com.example.karaoke_note

import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController

@Composable
fun AppBar(navController: NavController) {
    val canPop = remember { mutableStateOf(false) }
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            canPop.value = navController.previousBackStackEntry != null
        }
    }

    TopAppBar(
        title = { Text("カラオケ点数管理") },
        navigationIcon = {
            if (canPop.value) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            }
        }
    )
}