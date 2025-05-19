package com.example.karaoke_note.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.karaoke_note.data.SongScore
import com.example.karaoke_note.getPainterResourceIdOfGameImage
import java.time.format.DateTimeFormatter
import java.util.Locale

//
// 結果の詳細表示用ダイアログ
//
@Composable
fun SongScoreDetailDialog(
    onDismissRequest: () -> Unit,
    //song: Song,
    //artist: Artist,
    songScore: SongScore
){
    val dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    val keyFormat = if (songScore.key != 0) { "%+d" } else { "%d" }
    val fontSizeSmall = 12
    val fontSizeMedium = 20
    val fontSizeLarge = 32

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                //.height(150.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ){
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = getPainterResourceIdOfGameImage(songScore.gameKind.name)),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(60.dp)
                            .padding(end = fontSizeSmall.dp)
                            .align(Alignment.TopEnd)
                    )

                    Column(
                        modifier = Modifier.padding(
                            top = fontSizeSmall.dp,
                            start = fontSizeSmall.dp
                        )
                    ) {
                        Text(
                            text = songScore.date.format(dateFormat),
                            modifier = Modifier.padding(bottom = fontSizeSmall.dp),
                            fontSize = fontSizeSmall.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "点数",
                                modifier = Modifier
                                    .padding(
                                        start = fontSizeSmall.dp,
                                        end = (fontSizeSmall / 2).dp,
                                    ),
                                fontSize = fontSizeSmall.sp
                            )
                            Text(
                                text = String.format(Locale.US, "%.3f", songScore.score),
                                modifier = Modifier.padding(),
                                fontSize = fontSizeLarge.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "キー",
                                modifier = Modifier
                                    .padding(
                                        start = fontSizeSmall.dp,
                                        end = (fontSizeSmall / 2).dp,
                                    ),
                                fontSize = fontSizeSmall.sp
                            )
                            Text(
                                text = String.format(keyFormat, songScore.key),
                                modifier = Modifier.padding(),
                                fontSize = fontSizeMedium.sp
                            )
                        }
                    }
                }
                Text(
                    text = songScore.comment,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(fontSizeSmall.dp),
                    fontSize = fontSizeSmall.sp
                )
            }
        }
    }
}