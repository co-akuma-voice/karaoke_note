package com.example.karaoke_note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.karaoke_note.data.AppDatabase
import com.example.karaoke_note.data.FilterSetting
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.ui.component.SortMethod
import com.example.karaoke_note.ui.theme.Karaoke_noteTheme

class MainActivity : ComponentActivity() {

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            TODO("Not yet implemented")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songDao = AppDatabase.getDatabase(this).songDao()
        val songScoreDao = AppDatabase.getDatabase(this).songScoreDao()
        val artistDao = AppDatabase.getDatabase(this).artistDao()
        setContent {
            Karaoke_noteTheme {
                // Snackbar の状態
                val snackBarHostState = remember { SnackbarHostState() }
                // NewEntrySheet を開いているかどうか
                val showDialog = remember { mutableStateOf(false) }
                val editingSongScore = remember { mutableStateOf<SongScore?>(null) }
                // List ページで開いているページ (Artist/All songs) を保存する
                val isArtistListSelected = remember { mutableStateOf(true) }
                // All songs におけるソート方法
                val sortMethodOfAllSongs = remember { mutableStateOf(SortMethod.NameAsc) }
                // filteringの設定
                val filterSetting = remember { mutableStateOf(FilterSetting()) }
                // 検索文字列の設定
                val searchText = remember { mutableStateOf("") }
                // SearchBar に対するフォーカスの変更を管理する
                val focusRequesterForSearchBar = remember { FocusRequester() }
                val focusManagerOfSearchBar = LocalFocusManager.current

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        topBar = {
                            Column {
                                AppBar(navController, songDao, songScoreDao, artistDao, filterSetting, searchText, focusRequesterForSearchBar, focusManagerOfSearchBar)
                                Breadcrumbs(navController, focusManagerOfSearchBar, songDao, artistDao)
                            }
                        },
                        bottomBar = {
                            BottomNavigationBar(navController, songScoreDao)
                        },
                        floatingActionButton = {
                            NewEntryScreen(navController, songDao, songScoreDao, artistDao, lifecycleScope, showDialog, editingSongScore, snackBarHostState, focusManagerOfSearchBar)
                        },
                        snackbarHost = {
                            SnackbarHost(snackBarHostState)
                        },
                    ) { paddingValues ->
                        NavHost(
                            navController,
                            startDestination = "latest",
                            Modifier.padding(paddingValues)
                        ) {
                            composable("latest") {
                                LatestPage(navController, songDao, songScoreDao, artistDao, filterSetting.value, searchText.value, focusManagerOfSearchBar)
                            }
                            composable("song_data/{songId}") {backStackEntry ->
                                val songId = backStackEntry.arguments?.getString("songId")?.toLongOrNull()
                                if (songId != null) {
                                    val song = songDao.getSong(songId)
                                    if (song != null) {
                                        SongScores(song, songDao, songScoreDao, lifecycleScope, showDialog, editingSongScore, filterSetting.value, searchText.value, focusManagerOfSearchBar)
                                    }
                                }
                            }
                            composable("plans"){
                                PlansPage(songDao, songScoreDao, artistDao, showDialog, editingSongScore, lifecycleScope, snackBarHostState)
                            }
                            composable("list"){
                                ArtistsPage(navController, isArtistListSelected, sortMethodOfAllSongs, artistDao, songDao, songScoreDao, filterSetting.value, searchText.value, focusManagerOfSearchBar)
                            }
                            composable("song_list/{artistId}"){backStackEntry ->
                                val artistId = backStackEntry.arguments?.getString("artistId")?.toLongOrNull()
                                if (artistId != null) {
                                    SongList(navController, artistId, songDao, songScoreDao, artistDao, filterSetting.value, searchText.value, focusManagerOfSearchBar)
                                }
                            }
                        }
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}

