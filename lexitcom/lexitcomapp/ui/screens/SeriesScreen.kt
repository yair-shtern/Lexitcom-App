package lexitcom.lexitcomapp.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import lexitcom.lexitcomapp.R
import lexitcom.lexitcomapp.data.models.SeriesPosition
import lexitcom.lexitcomapp.data.models.TVEpisode
import lexitcom.lexitcomapp.viewmodel.SitcomsViewModel
import lexitcom.lexitcomapp.viewmodel.UserViewModel

@Composable
fun SeriesScreen(
    seriesId: Int,
    userViewModel: UserViewModel,
    sitcomsViewModel: SitcomsViewModel,
    navigateToEpisode: (Int, SeriesPosition) -> Unit
) {
    val series by remember { mutableStateOf(sitcomsViewModel.getSitcomById(seriesId)) }
    if (series != null) {
        var seasonMenuExpended by remember { mutableStateOf(false) }
        var position by remember { mutableStateOf(series!!.position) }
        LaunchedEffect(series!!.position){
            position = series!!.position
        }
        var seriesInFavorites by remember {
            mutableStateOf(
                userViewModel.isSeriesInFavorites(
                    seriesId
                )
            )
        }
        var numEpisodes by remember { mutableStateOf(series!!.seasons[position.seasonNum]!!.size) }

        LazyColumn {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    contentAlignment = Alignment.TopEnd
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/images/${series!!.imageName}")
                            .build(),
                        contentDescription = "${series!!.name} image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                    if (seriesInFavorites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            modifier = Modifier.padding(8.dp),
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }
            }
            item {
                Divider()
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = series!!.name,
                        modifier = Modifier
                            .padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Divider()
                Button(
                    onClick = {
                        navigateToEpisode(
                            seriesId,
                            position
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Text(
                        text = "Start - season ${position.seasonNum}, episode ${position.episodeNum}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        if (seriesInFavorites) {
                            IconButton(onClick = {
                                userViewModel.removeFromFavorites(seriesId)
                                seriesInFavorites = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null
                                )
                            }
                            Text(
                                text = "Remove from Favorites",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            IconButton(onClick = {
                                userViewModel.addToFavorites(seriesId)
                                seriesInFavorites = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                            Text(
                                text = "Add to Favorites",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        }
                        Text(text = "Share", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_download_24),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = "Download Season ${position.seasonNum}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Divider()
                Box(modifier = Modifier.padding(4.dp)) {
                    Button(onClick = { seasonMenuExpended = true }) {
                        Text(
                            text = "Season ${position.seasonNum}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            tint = Color.White,
                            contentDescription = "Arrow Down"
                        )
                    }
                    DropdownMenu(
                        expanded = seasonMenuExpended,
                        onDismissRequest = { seasonMenuExpended = false },
                        modifier = Modifier
                            .offset(y = if (seasonMenuExpended) 0.dp else 200.dp)
                    ) {
                        for (i in 1..series!!.numSeasons) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Season $i",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                onClick = {
                                    seasonMenuExpended = false
                                    sitcomsViewModel.updatePosition(
                                        series!!.seriesId,
                                        i,
                                        position.episodeNum
                                    ) // todo
                                    position.seasonNum = i
                                    numEpisodes = series!!.seasons[position.seasonNum - 1]!!.size
                                }
                            )
                        }
                    }
                }
                Divider()
            }
            val cols = 2
            items((1..numEpisodes).chunked(cols)) { items ->
                Row {
                    for ((index, episodeNum) in items.withIndex()) {
                        Box(modifier = Modifier.fillMaxWidth(1f / (cols - index))) {
                            series!!.seasons[position.seasonNum]?.get(episodeNum)?.let { episode ->
                                EpisodeCard(
                                    seriesId = seriesId,
                                    episode = episode,
                                    navigateToEpisode = navigateToEpisode
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        Text(text = "Series Not Found", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun EpisodeCard(
    seriesId: Int,
    episode: TVEpisode,
    navigateToEpisode: (Int, SeriesPosition) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/images/${episode.imageName}")
                    .build(),
                contentDescription = "${episode.seriesName} image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clickable {
                        navigateToEpisode(
                            seriesId,
                            SeriesPosition(episode.seasonNum, episode.episodeNum)
                        )
                    },
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigateToEpisode(
                    seriesId,
                    SeriesPosition(episode.seasonNum, episode.episodeNum)
                ) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.question_mark_circle),
                        contentDescription = null
                    )
                }
                Text(
                    text = "Episode ${episode.episodeNum}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_download_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}