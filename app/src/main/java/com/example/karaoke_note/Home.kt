package com.example.karaoke_note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import java.time.LocalDate

private fun loadDummyData(songDao: SongDao, songScoreDao: SongScoreDao) {
    val songId1 = songDao.insertSong(
        Song(
            title = "長いタイトル長いタイトル長いタイトル長いタイトル",
            artist = "長いアーティスト長いアーティスト長いアーティスト長いアーティスト"
        )
    )
    songScoreDao.insertSongScore(
        SongScore(
            songId = songId1,
            date = LocalDate.parse("1996-08-17"),
            score = 98.76543f,
            key = -6,
            comment = "テストテスト"
        )
    )

    val songId2 = songDao.insertSong(Song(title = "1 2 3 ~恋が始まる~", artist = "いきものがかり"))
    songScoreDao.insertSongScore(
        SongScore(
            songId = songId2,
            date = LocalDate.parse("2023-06-24"),
            score = 100.000f,
            key = -2,
            comment = ""
        )
    )
    val songId3 = songDao.insertSong(Song(title = "ARIA", artist = "Kalafina"))
    songScoreDao.insertSongScore(
        SongScore(
            songId = songId3,
            date = LocalDate.parse("2023-06-24"),
            score = 90.672f,
            key = -1,
            comment = "-1で試す。"
        )
    )
    val songId4 = songDao.insertSong(Song(title = "星月夜", artist = "由薫"))
    songScoreDao.insertSongScore(
        SongScore(
            songId = songId4,
            date = LocalDate.parse("2023-06-24"),
            score = 90.919f,
            key = -3,
            comment = ""
        )
    )
    songScoreDao.insertSongScore(
        SongScore(
            songId = songId4,
            date = LocalDate.parse("2023-06-24"),
            score = 90.920f,
            key = -3,
            comment = ""
        )
    )
    songScoreDao.insertSongScore(
        SongScore(
            songId = songId4,
            date = LocalDate.parse("2023-06-24"),
            score = 90.921f,
            key = -3,
            comment = ""
        )
    )
}

@ExperimentalMaterial3Api
@Composable
fun Home(navController: NavController, songDao: SongDao, songScoreDao: SongScoreDao) {
    loadDummyData(songDao, songScoreDao)
    Column {
        Box(modifier = Modifier.weight(8f)) {
            LazyColumn(
                modifier = Modifier
            ) {
                val songDataList = songScoreDao.getLatestScores(10)
                items(songDataList) { songData ->
                    val song = songDao.getSong(songData.songId)
                    if (song != null) {
                        LatestCard(song, songData, navController)
                    } else {
                        // データベースが壊れている
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(2f)) {
            BottomNavigationBar(navController)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun LatestCard(song: Song, songScore: SongScore, navController: NavController) {
    remember { mutableStateOf(false) }
    var commentforcard = ""
    if (songScore.comment.isNotEmpty()) {
        commentforcard = "..."
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Card(
            onClick = {navController.navigate("song_data/${song.id}")},
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
                            text = song.artist,
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
                            text = String.format("%.3f", songScore.score),
                            modifier = Modifier
                                .padding(top = 0.dp, end = 16.dp, bottom = 2.dp)
                                .align(Alignment.End),
                            color = Color.DarkGray,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = songScore.key.toString(),
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
