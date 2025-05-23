package com.example.karaoke_note

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.BrandKind
import com.example.karaoke_note.data.FilterSetting
import com.example.karaoke_note.data.GameKind
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun LatestPage(
    navController: NavController,
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    filterSetting: FilterSetting,
    searchText: String,
    focusManagerOfSearchBar: FocusManager
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val isTopOfList by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    val songScoreList = remember { mutableStateListOf<SongScore>() }
    val isLoading = remember { mutableStateOf(false) }
    val pageSize = 10
    var loadingDone by remember { mutableStateOf(false) }

    fun loadSongs(offset: Int, searchText: String) {
        if (!isLoading.value) {
            isLoading.value = true
            val newSongs = songScoreDao.getLatestScoresByText("%$searchText%", pageSize, offset)
            if (newSongs.isEmpty()) loadingDone = true
            songScoreList.addAll(newSongs)
            isLoading.value = false
        }
    }

    // `searchText`の変更時にデータを再ロード
    LaunchedEffect(searchText) {
        songScoreList.clear()
        loadingDone = false
        loadSongs(0, searchText)
    }

    // スクロール位置に基づいてデータのロードが必要か判断
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false
            lastVisibleItem.index >= songScoreList.size - pageSize / 2 && !isLoading.value && !loadingDone
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            loadSongs(songScoreList.size, searchText)
        }
    }

    Column {
        Box(modifier = Modifier.weight(8f)) {
            LazyColumn(
                modifier = Modifier,
                state = listState
            ) {
                val filteredSongScoreList = songScoreList.filter { songScore ->
                    songScore.gameKind in filterSetting.getSelectedGameKinds()
                }

                itemsIndexed(filteredSongScoreList) { index, songScore ->
                    // 各アイテムの表示
                    val song = songDao.getSong(songScore.songId)
                    if (song != null) {
                        val artist = artistDao.getNameById(song.artistId)
                        if (artist != null) {
                            LatestList(song, songScore, artist, focusManagerOfSearchBar, navController)
                        }
                    }
                }

                // ロード中のインジケータ表示
                if (isLoading.value) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // Scroll to Top ボタン
            AnimatedScrollUpButton(
                isVisible = (!isTopOfList),
            ){
                clearFocusFromSearchBar(focusManagerOfSearchBar)
                coroutineScope.launch {
                    listState.animateScrollToItem(index = 0)
                }
            }
        }
    }
}

@Composable
fun AnimatedScrollUpButton(
    isVisible: Boolean,
    onClick: () -> Unit
){
    val middleFABSize = 56

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .offset(y = -(middleFABSize * 1.25).dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(initialOffsetX = { it * 2 }),
            exit = slideOutHorizontally(targetOffsetX = { it * 2 }),
        ) {
            IconButton(
                onClick = { onClick() },
                modifier = Modifier,
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Scroll to Top",
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                )
            }
        }
    }
}

fun getPainterResourceIdOfBrandImage(brandName: String): Int {
    val painterResourceId = when (brandName) {
        BrandKind.JOY.name -> R.drawable.joysound
        BrandKind.DAM.name -> R.drawable.dam
        else -> R.drawable.unknown_game
    }
    return painterResourceId
}
fun getPainterResourceIdOfGameImage(gameName: String): Int {
    val painterResourceId = when (gameName) {
        GameKind.JOY_NATIONAL_SCORING_GP.name -> R.drawable.joy_zenkoku_saiten_grand_prix_gp
        GameKind.JOY_ANALYSIS_SCORING_AI_PLUS.name -> R.drawable.joy_bunseki_saiten_ai_plus
        GameKind.JOY_ANALYSIS_SCORING_AI.name -> R.drawable.joy_bunseki_saiten_ai
        GameKind.JOY_ANALYSIS_SCORING_MASTER.name -> R.drawable.joy_bunseki_saiten_master
        GameKind.DAM_PRECISE_SCORING_AI_HEART.name -> R.drawable.dam_seimitsu_saiten_ai_heart
        GameKind.DAM_PRECISE_SCORING_AI.name -> R.drawable.dam_seimitsu_saiten_ai
        GameKind.DAM_PRECISE_SCORING_DX_G.name -> R.drawable.dam_seimitsu_saiten_dx_g
        GameKind.DAM_PRECISE_SCORING_DX_DUET.name -> R.drawable.dam_seimitsu_saiten_dx_duet
        GameKind.DAM_PRECISE_SCORING_DX.name -> R.drawable.dam_seimitsu_saiten_dx
        GameKind.DAM_RANKING_BATTLE_ONLINE.name -> R.drawable.dam_ranking_battle_online
        else -> R.drawable.unknown_game
    }
    return painterResourceId
}

@SuppressLint("DefaultLocale")
@ExperimentalMaterial3Api
@Composable
fun LatestList(
    song: Song,
    songScore: SongScore,
    artist: String,
    focusManagerOfSearchBar: FocusManager,
    navController: NavController
) {
    val keyFormat = if (songScore.key != 0) { "%+d" } else { "%d" }
    val fontSize = listOf(12, 10, 8)

    Column {
        ListItem(
            modifier = Modifier
                //.height(90.dp)
                .clickable {
                    focusManagerOfSearchBar.clearFocus()
                    navController.navigate("song_data/${song.id}")
                },
            leadingContent = {
                Image(
                    painter = painterResource(id = getPainterResourceIdOfGameImage(songScore.gameKind.name)),
                    contentDescription = "Selected Game",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(32.dp)
                )
            },
            headlineContent = {
                Column {
                    Text(
                        text = songScore.date.toString(),
                        modifier = Modifier
                            .padding(top = 2.dp, bottom = 4.dp)
                            .align(Alignment.Start),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = fontSize[2].sp,
                    )
                    Text(
                        text = song.title,
                        modifier = Modifier
                            .padding(top = 1.dp, bottom = 1.dp)
                            .align(Alignment.Start),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = fontSize[0].sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = artist,
                        modifier = Modifier
                            .padding(top = 1.dp, bottom = 4.dp)
                            .align(Alignment.Start),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = fontSize[1].sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            },
            supportingContent = {
                Text(
                    text = songScore.comment,
                    modifier = Modifier
                        .padding(top = 2.dp, end = 6.dp, bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = fontSize[2].sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Column {
                    Text(
                        text = String.format("%.3f", songScore.score),
                        modifier = Modifier
                            .padding(top = 0.dp, end = 16.dp, bottom = 4.dp)
                            .align(Alignment.End),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = fontSize[0].sp,
                    )
                    Text(
                        text = String.format(keyFormat, songScore.key),
                        modifier = Modifier
                            .padding(top = 4.dp, end = 16.dp, bottom = 2.dp)
                            .align(Alignment.End),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = fontSize[0].sp,
                    )
                }
            },
            shadowElevation = 1.dp
        )
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}
