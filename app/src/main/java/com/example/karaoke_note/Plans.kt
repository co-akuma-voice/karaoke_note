package com.example.karaoke_note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karaoke_note.data.ArtistDao
import com.example.karaoke_note.data.Song
import com.example.karaoke_note.data.SongDao
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.data.SongScoreDao
import java.time.LocalDate

@ExperimentalMaterial3Api
@Composable
fun PlansPage(songDao: SongDao, songScoreDao: SongScoreDao, artistDao: ArtistDao, showEntrySheetDialog: MutableState<Boolean>, editingSongScore: MutableState<SongScore?>) {
    Column {
        Box(
            modifier = Modifier.weight(8f)
        ) {
            LazyColumn(
                modifier = Modifier
            ) {
                val songDataList = songScoreDao.getAll0Scores()
                items(songDataList) { songData ->
                    val song = songDao.getSong(songData.songId)
                    if (song != null) {
                        val artist = artistDao.getNameById(song.artistId)
                        if (artist != null) {
                            PlansCard(song, songData, artist, showEntrySheetDialog, editingSongScore)
                        }
                    } else {
                        // データベースが壊れている
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PlansCard(song: Song, songScore: SongScore, artist: String, showEntrySheetDialog: MutableState<Boolean>, editingSongScore: MutableState<SongScore?>) {
    remember { mutableStateOf(false) }
    val keyFormat = if (songScore.key != 0) { "%+d" } else { "%d" }

    Column(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Card(
            onClick = {
                editingSongScore.value = songScore.copy(date = LocalDate.now())
                showEntrySheetDialog.value = true
                      },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row (
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(3f),
                ) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceAround
                    ){
                        Text(
                            text = song.title,
                            modifier = Modifier
                                .padding(start = 20.dp, top = 6.dp, bottom = 4.dp)
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
                                .padding(start = 20.dp, top = 4.dp, bottom = 6.dp)
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
                        .padding(4.dp)
                        //.background(Color.Green),
                ) {
                    Text(
                        text = String.format(keyFormat, songScore.key),
                        modifier = Modifier
                            .padding(top = 2.dp, end = 16.dp, bottom = 2.dp),
                        color = Color.DarkGray,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}