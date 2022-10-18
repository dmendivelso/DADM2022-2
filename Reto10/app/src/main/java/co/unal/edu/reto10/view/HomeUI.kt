package co.unal.edu.reto10.view

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.unal.edu.reto10.model.Concession

@Composable
fun ConcessionItem(concession: Concession){
    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Surface() {
            Row(
                Modifier
                    .padding(4.dp)
                    .fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxHeight()
                        .weight(0.8f)
                ) {
                    Box(Modifier
                        .padding(4.dp)
                        .fillMaxWidth()) {
                        Text(
                            text = concession.nombrecorto,
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(250.dp)
                        )
                        Text(
                            text = concession.codigo,
                            style = MaterialTheme.typography.caption,
                            color = Color.Black,
                            modifier = Modifier
                                .background(
                                    Color.LightGray
                                )
                                .padding(4.dp)
                                .align(Alignment.TopEnd)
                        )
                    }
                    Text(
                        text = concession.tipo,
                        style = MaterialTheme.typography.caption,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis

                    )
                }
            }
        }
    }
}

