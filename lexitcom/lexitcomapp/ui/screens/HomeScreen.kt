package lexitcom.lexitcomapp.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import lexitcom.lexitcomapp.R
import lexitcom.lexitcomapp.data.models.TVSeries
import lexitcom.lexitcomapp.viewmodel.UserViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import lexitcom.lexitcomapp.data.models.SeriesPosition
import lexitcom.lexitcomapp.viewmodel.SitcomsViewModel

@Composable
fun Home(
    userViewModel: UserViewModel,
    sitcomsViewModel: SitcomsViewModel,
    navigateToSeries: (Int) -> Unit,
    navigateToEpisode: (Int, SeriesPosition) -> Unit
) {
    val favoritesId by userViewModel.favorites.observeAsState(emptySet())
    val progress by userViewModel.progress.observeAsState(emptyMap())


    LaunchedEffect(progress, block = {
        progress.mapNotNull { item ->
            sitcomsViewModel.updatePosition(
                item.key,
                item.value.seasonNum,
                item.value.episodeNum
            )
        }
    })

    LazyColumn {
        item {
            Text(
                text = "My Favorites",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            LazyRow {
                items(favoritesId.mapNotNull { id ->
                    sitcomsViewModel.getSitcomById(id)
                })
                { series ->
                    SeriesCard(
                        series = series,
                        navigateToSeries = navigateToSeries,
                        modifier = Modifier
                            .height(180.dp)
                            .width(240.dp)
                    )
                }
            }
        }
        item {
            Text(
                text = "Continue Learning",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            LazyRow {
                items(progress.mapNotNull { item ->
                    sitcomsViewModel.updatePosition(
                        item.key,
                        item.value.seasonNum,
                        item.value.episodeNum
                    )
                    sitcomsViewModel.getSitcomById(item.key)
                }) { series ->
                    SeriesInLearningCard(
                        series = series,
                        navigateToSeries = navigateToSeries,
                        modifier = Modifier
                            .height(210.dp)
                            .width(240.dp),
                        navigateToEpisode = navigateToEpisode,
                        sitcomsViewModel = sitcomsViewModel,
                        userViewModel = userViewModel
                    )
                }
            }
        }
        item {
            Text(
                text = "Sitcoms",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        val cols = 2
        sitcomsViewModel.getAll()?.let {
            items(it.chunked(cols)) { items ->
                Row {
                    for ((index, series) in items.withIndex()) {
                        Box(modifier = Modifier.fillMaxWidth(1f / (cols - index))) {
                            SeriesCard(
                                series = series,
                                navigateToSeries = navigateToSeries,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun SeriesCard(
    series: TVSeries,
    navigateToSeries: (Int) -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .clickable {
                navigateToSeries(series.seriesId)
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/images/${series.imageName}")
                        .build(),
                    contentDescription = series.name,
                    modifier = Modifier.aspectRatio(16f / 9f),
                )
            }
            Text(
                text = series.name,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SeriesInLearningCard(
    series: TVSeries,
    navigateToSeries: (Int) -> Unit,
    modifier: Modifier,
    navigateToEpisode: (Int, SeriesPosition) -> Unit = { _, _ -> },
    sitcomsViewModel: SitcomsViewModel,
    userViewModel: UserViewModel,
) {
    var menuExpended by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMassage by remember { mutableStateOf("") }
    var remove by remember { mutableStateOf(false) }
    var visable by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visable,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 500)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 500)
        )
    )
    {
        Card(
            modifier = modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .clickable {
                    navigateToEpisode(
                        series.seriesId,
                        series.position
                    )
                },
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/images/${series.imageName}")
                            .build(),
                        contentDescription = series.name,
                        modifier = Modifier.aspectRatio(16f / 9f),
                    )
                    Icon(
                        painter = rememberAsyncImagePainter(
                            model = R.drawable.question_mark_circle
                        ),
                        modifier = Modifier.size(60.dp),
                        contentDescription = "question mark icon",
                    )
                }

                Text(
                    text = series.name,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )

                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigateToSeries(series.seriesId) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_info_outline_24),
                            contentDescription = null
                        )
                    }
                    Text(
                        text = "season ${series.position.seasonNum}, episode ${series.position.episodeNum}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    IconButton(onClick = { menuExpended = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                }
                DropdownMenu(
                    expanded = menuExpended,
                    onDismissRequest = { menuExpended = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Continue season ${series.position.seasonNum}, episode ${series.position.episodeNum}",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            menuExpended = false
                            navigateToEpisode(series.seriesId, series.position)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.question_mark_circle),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Go to episodes",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            menuExpended = false
                            navigateToSeries(series.seriesId)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_info_outline_24),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Download series quizzes",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            menuExpended = false
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_download_24),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Restart series",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            dialogMassage =
                                "The series will restart from the first episode, are You sure?"
                            showDialog = true
                            menuExpended = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Remove from continue learning",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            remove = true
                            dialogMassage =
                                "The series will remove and restart, are You sure?"
                            showDialog = true
                            menuExpended = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null
                            )
                        }
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text(
                                text = "Confirmation",
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        text = {
                            Text(
                                text = dialogMassage,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (remove) {
                                        visable = false
                                        userViewModel.removeFromProgress(seriesId = series.seriesId)
                                    } else {
                                        userViewModel.updateProgress(
                                            seriesId = series.seriesId,
                                            position = SeriesPosition(1, 1)
                                        )
                                    }
                                    sitcomsViewModel.updatePosition(
                                        seriesId = series.seriesId,
                                        seasonNum = 1,
                                        episodeNum = 1
                                    )
                                    showDialog = false
                                }
                            ) {
                                Text("Yes", style = MaterialTheme.typography.bodyMedium)
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDialog = false }
                            ) {
                                Text("No", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    )
                }
            }
        }
    }
}