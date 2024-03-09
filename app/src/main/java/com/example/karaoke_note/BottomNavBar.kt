package com.example.karaoke_note

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.karaoke_note.data.SongScoreDao


data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
    val badge: Int? = null
)

@ExperimentalMaterial3Api
@Composable
fun BottomNavigationBar(navController: NavController, songScoreDao: SongScoreDao) {
    val songDataFlow = songScoreDao.getAll0Scores()
    val songDataList by songDataFlow.collectAsState(initial = listOf())
    val plansBadge = songDataList.size
    val bottomNavItems = listOf(
        BottomNavItem(
            name = "Latest",
            route = "latest",
            icon = Icons.Filled.Schedule,
        ),
        BottomNavItem(
            name = "Plans",
            route = "plans",
            icon = Icons.Filled.PlaylistAdd,
            badge = plansBadge
        ),
        BottomNavItem(
            name = "List",
            route = "list",
            icon = Icons.Filled.List,
        )
    )

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = item.route == navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationBarItem(
                icon = {
                    if (item.badge != null) {
                        BadgedBox(badge = { Badge { Text(item.badge.toString()) }}) {
                            Icon(item.icon, contentDescription = item.name)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.name)
                    }
                },
                label = { Text(text = item.name) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route)
                }
            )
        }
    }
}