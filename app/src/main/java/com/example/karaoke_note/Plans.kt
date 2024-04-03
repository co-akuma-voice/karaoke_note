package com.example.karaoke_note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.karaoke_note.ui.component.CustomSwipeToDismiss
import java.time.LocalDate

fun getARGBForSwipeDismiss(colorNumber: Int): Color {
    var argb: Color = Color.Black
    when (colorNumber) {
        0 -> argb = Color(0xffc00000)
        1 -> argb = Color(0x1fff0000)
        2 -> argb = Color.LightGray
        3 -> argb = Color.Gray
    }
    return argb
}

data class PlansItem(
    val id: Long
)

@ExperimentalMaterialApi
@Composable
fun PlansPage(
    songDao: SongDao,
    songScoreDao: SongScoreDao,
    artistDao: ArtistDao,
    showEntrySheetDialog: MutableState<Boolean>,
    editingSongScore: MutableState<SongScore?>
) {
    val songScoreFlow = songScoreDao.getAll0Scores()
    val songScoreList by songScoreFlow.collectAsState(initial = listOf())

    fun removePlan(id: Long) {
        songScoreDao.deleteSongScore(id)
    }

    Column {
        Box(
            modifier = Modifier.weight(8f)
        ) {
            LazyColumn(
                modifier = Modifier
            ) {
                items(songScoreList, {songScore:SongScore -> songScore.id}) { songScore ->
                    val song = songDao.getSong(songScore.songId)
                    if (song != null) {
                        val artist = artistDao.getNameById(song.artistId)
                        if (artist != null) {
                            val dismissState = rememberDismissState(
                                confirmStateChange = {
                                    if (songScoreList.size > 1 && it == DismissValue.DismissedToStart) {
                                        removePlan(songScore.id)    // Plan データを削除する
                                        true
                                    }
                                    else { false }
                                }
                            )

                            CustomSwipeToDismiss(
                                state = dismissState,
                                modifier = Modifier,
                                directions = setOf(DismissDirection.EndToStart),
                                background = {
                                    Box (
                                        modifier = Modifier
                                            .padding()
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .background(getARGBForSwipeDismiss(1)),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "delete",
                                            tint = getARGBForSwipeDismiss(0),
                                            modifier = Modifier
                                                .size(60.dp)
                                                .padding(end = 30.dp)
                                        )
                                    }
                                },
                                dismissContent = {
                                    PlansListItem(song, songScore, artist, showEntrySheetDialog, editingSongScore)
                                }
                            )
                        }
                    }
                    else {
                        // データベースが壊れている
                    }
                }
            }
        }
    }
}

@Composable
fun PlansListItem(
    song: Song,
    songScore: SongScore,
    artist: String,
    showEntrySheetDialog: MutableState<Boolean>,
    editingSongScore: MutableState<SongScore?>
) {
    val keyFormat = if (songScore.key != 0) { "%+d" } else { "%d" }

    ListItem(
        modifier = Modifier
            .height(80.dp)
            .clickable {
                editingSongScore.value = songScore.copy(date = LocalDate.now())
                showEntrySheetDialog.value = true
            },
        headlineContent = {
            Text(
                text = song.title,
                modifier = Modifier
                    .padding(start = 20.dp, top = 6.dp, bottom = 4.dp),
                color = Color.DarkGray,
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = artist,
                modifier = Modifier
                    .padding(start = 20.dp, top = 4.dp, bottom = 6.dp),
                color = Color.Gray,
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {},
        trailingContent = {
            Text(
                text = String.format(keyFormat, songScore.key),
                modifier = Modifier
                    .padding(top = 2.dp, end = 16.dp, bottom = 2.dp),
                color = Color.DarkGray,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
            )
        },
        shadowElevation = 1.dp
    )
    //Divider(color = Color.Gray, thickness = 1.dp)
}