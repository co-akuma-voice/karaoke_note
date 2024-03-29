package com.example.karaoke_note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.BrandKind
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
    artistDao: ArtistDao
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val firstVisibleListItem by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val songScoreList = remember { mutableStateListOf<SongScore>() }
    val isLoading = remember { mutableStateOf(false) }
    val pageSize = 10  // 1回のロードで取得するアイテム数

    // 初回および追加データのロードを行う関数
    fun loadSongs(offset: Int) {
        if (!isLoading.value) {
            isLoading.value = true
            val newSongs = songScoreDao.getLatestScores(pageSize, offset)
            songScoreList.addAll(newSongs)
            isLoading.value = false
        }
    }

    // 初回のデータロード
    LaunchedEffect(Unit) {
        loadSongs(songScoreList.size)
    }

    Column {
        Box(modifier = Modifier.weight(8f)) {
            LazyColumn(
                modifier = Modifier,
                state = listState
            ) {
                itemsIndexed(songScoreList) { index, songScore ->
                    // 各アイテムの表示
                    val song = songDao.getSong(songScore.songId)
                    if (song != null) {
                        val artist = artistDao.getNameById(song.artistId)
                        if (artist != null) {
                            LatestCard(song, songScore, artist, navController)
                        }
                    }

                    // リストの末尾に到達した場合、追加のデータをロード
                    if (index == songScoreList.size - 1 && !isLoading.value) {
                        LaunchedEffect(songScoreList.size) {
                            loadSongs(songScoreList.size)
                        }
                    }
                }
                // ロード中のインジケータ表示
                if (isLoading.value) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // Scroll to Top ボタン
            AnimatedScrollUpButton(
                isVisible = (firstVisibleListItem > 0),
            ){
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
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = "Scroll to Top",
                    tint = Color.White,
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
        GameKind.DAM_RANKING_BATTLE_ONLINE.name -> R.drawable.dam_ranking_battle_online
        GameKind.DAM_PRECISE_SCORING_AI.name -> R.drawable.dam_seimitsu_saiten_ai
        GameKind.DAM_PRECISE_SCORING_DX_G.name -> R.drawable.dam_seimitsu_saiten_dx_g
        GameKind.DAM_PRECISE_SCORING_DX_DUET.name -> R.drawable.dam_seimitsu_saiten_dx_duet
        GameKind.DAM_PRECISE_SCORING_DX.name -> R.drawable.dam_seimitsu_saiten_dx
        else -> R.drawable.unknown_game
    }
    return painterResourceId
}

@ExperimentalMaterial3Api
@Composable
fun LatestCard(
    song: Song,
    songScore: SongScore,
    artist: String,
    navController: NavController
) {
    var commentforcard = ""
    if (songScore.comment.isNotEmpty()) {
        commentforcard = "..."
    }
    val keyFormat = if (songScore.key != 0) { "%+d" } else { "%d" }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Card(
            onClick = {
                navController.navigate("song_data/${song.id}")
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(Color(0xffffffff)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(3f),
                ) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = songScore.date.toString(),
                            modifier = Modifier
                                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                .align(Alignment.Start),
                            color = Color.Gray,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 6.sp,
                        )
                        Text(
                            text = song.title,
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
                        .width(50.dp)
                        .height(60.dp)
                        .padding(top = 12.dp),
                        //.background(Color.Blue),
                ){
                    Image(
                        painter = painterResource(id = getPainterResourceIdOfGameImage(songScore.gameKind.name)),
                        contentDescription = "Selected Game",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.TopCenter)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .padding(top = 6.dp),
                        //.background(Color.Green),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = String.format("%.3f", songScore.score),
                            modifier = Modifier
                                .padding(top = 0.dp, end = 16.dp, bottom = 2.dp)
                                .align(Alignment.End),
                            color = Color.DarkGray,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = String.format(keyFormat, songScore.key),
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
