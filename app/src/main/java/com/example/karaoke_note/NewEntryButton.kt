package com.example.karaoke_note

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun NewEntryButton(navController: NavController){
    //val ctx = LocalContext.current

    FloatingActionButton(
        onClick = {
            //Toast.makeText(ctx, "Floating Action Button is Clicked.", Toast.LENGTH_SHORT).show()
            navController.navigate(route = "new_entry")
        },
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add",
            tint = Color.White,
        )
    }
}