package lexitcom.lexitcomapp.ui.screens

import android.content.res.Resources
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Down
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Up
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.stream.JsonReader
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.IconButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lexitcom.lexitcomapp.R
import lexitcom.lexitcomapp.data.models.DummyQuiz
import lexitcom.lexitcomapp.data.models.Quiz
import lexitcom.lexitcomapp.data.models.SeriesPosition
import lexitcom.lexitcomapp.data.models.TVEpisode
import lexitcom.lexitcomapp.data.models.Word
import lexitcom.lexitcomapp.data.models.WordList
import lexitcom.lexitcomapp.viewmodel.QuizViewModel
import lexitcom.lexitcomapp.viewmodel.SitcomsViewModel
import lexitcom.lexitcomapp.viewmodel.UserViewModel
import java.io.InputStreamReader
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun EpisodeScreen(
    seriesId: Int,
    seriesPosition: SeriesPosition,
    userViewModel: UserViewModel,
    sitcomsViewModel: SitcomsViewModel,
    quizViewModel: QuizViewModel = QuizViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentUser by userViewModel.currentUser.observeAsState()
    var language by remember { mutableStateOf(currentUser?.userLanguage ?: "es") }
    // Observe changes in the user view model's currentUser
    LaunchedEffect(currentUser?.userLanguage) {
        language = currentUser?.userLanguage.toString()
    }

    var episode by remember {
        mutableStateOf(
            sitcomsViewModel.getEpisodeByIdAndPosition(
                seriesId = seriesId,
                seasonNum = seriesPosition.seasonNum,
                episodeNum = seriesPosition.episodeNum
            )
        )
    }


    if (episode != null) {
        userViewModel.updateProgress(seriesId, seriesPosition)

        val quiz = remember { mutableStateOf(getNewQuiz(episode!!, context.resources)) }

        var selectedOption by remember { mutableStateOf("Flash Cards") }
        val swipeThreshold = LocalConfiguration.current.screenWidthDp.toFloat() / 2F

        var passedWords by remember { mutableStateOf(emptyList<Word>()) }
        var wordList by remember { mutableStateOf(quiz.value.wordList.words.reversed()) }
        var currentCardIndex by remember { mutableIntStateOf(wordList.size - 1) }
        var leftSwappedWords by remember { mutableStateOf(emptyList<Word>()) }
        var rightSwappedWords by remember { mutableStateOf(emptyList<Word>()) }

        LaunchedEffect(episode) {
            quiz.value = getNewQuiz(episode!!, context.resources)
            passedWords = emptyList()
            wordList = quiz.value.wordList.words.reversed()
            currentCardIndex = wordList.size - 1
            leftSwappedWords = emptyList()
            rightSwappedWords = emptyList()
        }

        val progress by animateFloatAsState(
            targetValue = 1f - (wordList.size.toFloat() / (wordList.size.toFloat() + passedWords.size.toFloat())),
            animationSpec = tween(300),
            label = ""
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)
                ) {
                    Row(modifier = Modifier.clickable {
                        val prevEpisode = sitcomsViewModel.getPrevEpisode(
                            seriesId = seriesId,
                            seasonNum = seriesPosition.seasonNum,
                            episodeNum = seriesPosition.episodeNum
                        )
                        if (prevEpisode != null) {
                            episode = prevEpisode
                            seriesPosition.seasonNum = episode!!.seasonNum
                            seriesPosition.episodeNum = episode!!.episodeNum
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                        Text(
                            text = "previous\nepisode",
                            maxLines = 2,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .padding(start = 32.dp)
                            .clickable {
                                if (passedWords.isNotEmpty()) {
                                    val lastPassedWord = passedWords.last()
                                    wordList = wordList
                                        .toMutableList()
                                        .apply {
                                            add(lastPassedWord)
                                        }
                                    if (leftSwappedWords.contains(lastPassedWord)) {
                                        leftSwappedWords = leftSwappedWords
                                            .toMutableList()
                                            .apply {
                                                remove(lastPassedWord)
                                            }
                                    } else {
                                        rightSwappedWords = rightSwappedWords
                                            .toMutableList()
                                            .apply {
                                                remove(lastPassedWord)
                                            }
                                    }
                                    passedWords = passedWords.dropLast(1)
                                    currentCardIndex++
                                }
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.reverse_left),
                            contentDescription = null
                        )
                        Text(
                            text = "Undo",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${episode!!.seriesName} ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = "season ${episode!!.seasonNum} episode ${episode!!.episodeNum}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium

                    )
                }

                Column(
                    horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)
                ) {
                    Row(modifier = Modifier.clickable {
                        val nextEpisode = sitcomsViewModel.getNextEpisode(
                            seriesId = seriesId,
                            seasonNum = seriesPosition.seasonNum,
                            episodeNum = seriesPosition.episodeNum
                        )
                        if (nextEpisode != null) {
                            episode = nextEpisode
                            seriesPosition.seasonNum = episode!!.seasonNum
                            seriesPosition.episodeNum = episode!!.episodeNum
                        }
                    }) {
                        Text(
                            text = "next\nepisode",
                            maxLines = 2,
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .padding(end = 32.dp)
                            .clickable {
                                wordList = quiz.value.wordList.words
                                passedWords = emptyList()
                                currentCardIndex = wordList.size - 1
                                leftSwappedWords = emptyList()
                                rightSwappedWords = emptyList()
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh, contentDescription = null
                        )
                        Text(
                            text = "Restart",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }


            Row(
                modifier = Modifier
                    .weight(8f)
                    .zIndex(1f)
                    .padding(vertical = 8.dp)
            ) {

                AnimatedContent(
                    targetState = selectedOption, transitionSpec = {
                        slideIntoContainer(
                            animationSpec = tween(300, easing = EaseIn), towards = Up
                        ).togetherWith(
                            slideOutOfContainer(
                                animationSpec = tween(300, easing = EaseOut), towards = Down
                            )
                        )
                    }, label = ""
                ) { targetState ->
                    if (wordList.isNotEmpty()) {
                        when (targetState) {
                            "Flash Cards" -> {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .zIndex(1f)
                                    ) {
                                        wordList.forEachIndexed { index, word ->
                                            Swipeable(
                                                onSwipeLeft = {
                                                    coroutineScope.launch {
                                                        val lastWord = wordList.lastOrNull()
                                                        if (lastWord != null) {
                                                            leftSwappedWords =
                                                                leftSwappedWords.toMutableList()
                                                                    .apply {
                                                                        add(lastWord)
                                                                    }
                                                            passedWords =
                                                                passedWords.toMutableList().apply {
                                                                    add(lastWord)
                                                                }
                                                            wordList = wordList.dropLast(1)
                                                            currentCardIndex--
                                                        }
                                                    }
                                                },
                                                onSwipeRight = {
                                                    coroutineScope.launch {
                                                        val lastWord = wordList.lastOrNull()
                                                        if (lastWord != null) {
                                                            rightSwappedWords =
                                                                rightSwappedWords.toMutableList()
                                                                    .apply {
                                                                        add(lastWord)
                                                                    }
                                                            passedWords =
                                                                passedWords.toMutableList().apply {
                                                                    add(lastWord)
                                                                }
                                                            wordList = wordList.dropLast(1)
                                                            currentCardIndex--
                                                        }
                                                    }
                                                },
                                                swipeThreshold = swipeThreshold,
                                                content = {
                                                    word.translatedMap[language]?.let {
                                                        FlipCard(
                                                            front = word.english,
                                                            back = it,
                                                            onClick = {},
                                                            isCurrentCard = index == currentCardIndex
                                                        )
                                                    }
                                                },
                                            )
                                        }
                                    }
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .fillMaxWidth(),
                                        progress = progress,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        backgroundColor = Color.LightGray
                                    )
                                }
                            }

                            "Match" -> {
                                val pairs = wordList.map {
                                    (it.english to (it.translatedMap[language] ?: ""))
                                }
                                MatchGame(pairs = pairs,
                                    onGameComplete = { wordList = emptyList() /* todo */ })
                            }

                            "Learn" -> {
                                Text(
                                    text = "Learn Content",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxSize(),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    } else {
                        var showLeftWords by remember { mutableStateOf(false) }
                        var showRightWords by remember { mutableStateOf(false) }
                        Card(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceAround,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Finish",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Card(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Image(
                                                modifier = Modifier.size(80.dp),
                                                painter = painterResource(id = R.drawable.x_circle),
                                                contentDescription = null
                                            )
                                            Button(modifier = Modifier.padding(8.dp),
                                                onClick = { showLeftWords = true }) {
                                                Text(
                                                    text = "Words you don't know",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                            DropdownMenu(
                                                modifier = Modifier.wrapContentSize(),
                                                expanded = showLeftWords,
                                                onDismissRequest = {
                                                    showLeftWords = false
                                                }) {
                                                leftSwappedWords.forEach { word ->
                                                    var text by remember {
                                                        mutableStateOf(
                                                            word.english
                                                        )
                                                    }
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = text,
                                                                style = MaterialTheme.typography.bodyLarge
                                                            )
                                                        },
                                                        onClick = {
                                                            if (text == word.english) {
                                                                text =
                                                                    word.translatedMap[language]
                                                                        ?: ""
                                                            } else {
                                                                text = word.english
                                                            }
                                                        },
                                                        trailingIcon = {
                                                            Column(horizontalAlignment = Alignment.End) {
                                                                IconButton(onClick = {
                                                                    rightSwappedWords =
                                                                        rightSwappedWords.toMutableList()
                                                                            .apply {
                                                                                add(word)
                                                                            }
                                                                    leftSwappedWords =
                                                                        leftSwappedWords.toMutableList()
                                                                            .apply {
                                                                                remove(word)
                                                                            }
                                                                    coroutineScope.launch {
                                                                        showLeftWords =
                                                                            false
                                                                        delay(300)
                                                                        showLeftWords = true
                                                                    }
                                                                }) {
                                                                    Icon(
                                                                        painter = painterResource(
                                                                            id = R.drawable.check_circle
                                                                        ),
                                                                        tint = Color.Green,
                                                                        contentDescription = null
                                                                    )
                                                                }
                                                                Text(
                                                                    text = "I know this word",
                                                                    style = MaterialTheme.typography.bodySmall
                                                                )
                                                            }
                                                        })
                                                }
                                            }
                                        }
                                    }
                                    Card(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Image(
                                                modifier = Modifier.size(80.dp),
                                                painter = painterResource(id = R.drawable.check_circle),
                                                contentDescription = null
                                            )
                                            Button(modifier = Modifier.padding(8.dp),
                                                onClick = { showRightWords = true }) {
                                                Text(
                                                    text = "Words you know",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                            DropdownMenu(
                                                modifier = Modifier.wrapContentSize(),
                                                expanded = showRightWords,
                                                onDismissRequest = {
                                                    showRightWords = false
                                                }) {
                                                rightSwappedWords.forEach { word ->
                                                    var text by remember {
                                                        mutableStateOf(
                                                            word.english
                                                        )
                                                    }
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = text,
                                                                style = MaterialTheme.typography.bodyLarge
                                                            )
                                                        },
                                                        onClick = {
                                                            if (text == word.english) {
                                                                text =
                                                                    word.translatedMap[language]
                                                                        ?: ""
                                                            } else {
                                                                text = word.english
                                                            }
                                                        },
                                                        trailingIcon = {
                                                            Column(horizontalAlignment = Alignment.End) {
                                                                IconButton(onClick = {
                                                                    leftSwappedWords =
                                                                        leftSwappedWords.toMutableList()
                                                                            .apply {
                                                                                add(word)
                                                                            }
                                                                    rightSwappedWords =
                                                                        rightSwappedWords.toMutableList()
                                                                            .apply {
                                                                                remove(word)
                                                                            }
                                                                    coroutineScope.launch {
                                                                        showRightWords =
                                                                            false
                                                                        delay(300)
                                                                        showRightWords =
                                                                            true
                                                                    }
                                                                }) {
                                                                    Icon(
                                                                        painter = painterResource(
                                                                            id = R.drawable.x_circle
                                                                        ),
                                                                        tint = Color.Red,
                                                                        contentDescription = null
                                                                    )
                                                                }
                                                                Text(
                                                                    text = "I don't know this word",
                                                                    style = MaterialTheme.typography.bodySmall
                                                                )
                                                            }
                                                        })
                                                }
                                            }
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(onClick = {
                                        val prevEpisode =
                                            sitcomsViewModel.getPrevEpisode(
                                                seriesId = seriesId,
                                                seasonNum = seriesPosition.seasonNum,
                                                episodeNum = seriesPosition.episodeNum
                                            )
                                        if (prevEpisode != null) {
                                            episode = prevEpisode
                                            seriesPosition.seasonNum =
                                                episode!!.seasonNum
                                            seriesPosition.episodeNum =
                                                episode!!.episodeNum
                                        }
                                    }) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                                contentDescription = null
                                            )

                                            Text(
                                                text = "Previous episode",
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                        }
                                    }
                                    Button(onClick = {
                                        wordList = quiz.value.wordList.words
                                        passedWords = emptyList()
                                        currentCardIndex = wordList.size - 1
                                    }) {


                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = null
                                            )
                                            Text(
                                                text = "Retake the quiz",
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                        }
                                    }
                                    Button(onClick = {
                                        val nextEpisode =
                                            sitcomsViewModel.getNextEpisode(
                                                seriesId = seriesId,
                                                seasonNum = seriesPosition.seasonNum,
                                                episodeNum = seriesPosition.episodeNum
                                            )
                                        if (nextEpisode != null) {
                                            episode = nextEpisode
                                            seriesPosition.seasonNum =
                                                episode!!.seasonNum
                                            seriesPosition.episodeNum =
                                                episode!!.episodeNum
                                        }
                                    }) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = null
                                            )
                                            Text(
                                                text = "Next episode",
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                        }

                                    }
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(onClick = {/*todo*/ }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_download_24),
                                            contentDescription = null
                                        )
                                    }
                                    Text(
                                        text = "Save quiz",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(end = 2.dp)
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .clickable {
                        selectedOption = "Flash Cards"
                    }) {
                    Text(
                        text = "Flash Cards",
                        color = if (selectedOption == "Flash Cards") MaterialTheme.colorScheme.onSecondary
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(end = 2.dp)
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .clickable {
                        selectedOption = "Match"
                    }) {
                    Text(
                        text = "Match",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selectedOption == "Match") MaterialTheme.colorScheme.onSecondary
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .clickable {
                        selectedOption = "Learn"
                    }) {
                    Text(
                        text = "Learn",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selectedOption == "Learn") MaterialTheme.colorScheme.onSecondary
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    } else {
        Text(text = "Unable to load episode", style = MaterialTheme.typography.bodyLarge)
    }
}

fun splitPairsIntoPortions(pairs: List<Pair<String, String>>): List<List<Pair<String, String>>> {
    val portions = mutableListOf<List<Pair<String, String>>>()
    var remainingPairs = pairs

    while (remainingPairs.isNotEmpty()) {
        val portionSize = if (remainingPairs.size >= 8) {
            6
        } else {
            remainingPairs.size.coerceIn(3, 8)
        }
        val portion = remainingPairs.take(portionSize)
        portions.add(portion)
        remainingPairs = remainingPairs.drop(portionSize)
    }

    return portions
}

@Composable
fun MatchGame(
    pairs: List<Pair<String, String>>, onGameComplete: () -> Unit
) {
    val shuffledPairs = pairs.shuffled()
    val portions = remember { mutableStateOf(splitPairsIntoPortions(shuffledPairs)) }
    var currentPortionIndex by remember { mutableStateOf(0) }
    val currentPortion = portions.value.getOrNull(currentPortionIndex) ?: emptyList()
    val visiblePairs =
        remember { mutableStateListOf<Int>().apply { addAll(currentPortion.indices) } }

    // Render the grid layout with pairs randomly placed
    GridLayout(
        pairs = currentPortion,
        visiblePairs = visiblePairs,
        onPairClick = { pairIndex ->
            visiblePairs.remove(pairIndex)
            if (visiblePairs.isEmpty()) {
                currentPortionIndex++
                if (currentPortionIndex == portions.value.size) {
                    onGameComplete()
                } else {
                    visiblePairs.addAll(portions.value[currentPortionIndex].indices)
                }
            }
        })
}

@Composable
fun GridLayout(
    pairs: List<Pair<String, String>>,
    visiblePairs: List<Int>,
    onPairClick: (Int) -> Unit
) {
    // Initialize gridSize and shuffledIndices
    val gridSize = visiblePairs.size * 2
    val shuffledIndices = (0 until gridSize).shuffled()

    // MutableState to keep track of the last clicked index
    val lastClickedIndex = remember { mutableStateOf<Int?>(null) }

    // Render the grid layout with pairs randomly placed
    NonlazyGrid(columns = pairs.size / 2, itemCount = gridSize) { index ->
        // Calculate the pair index
        val itemIndex = shuffledIndices[index]

        // Get the pair data
        val pairIndex = visiblePairs[itemIndex / 2]
        val pair = pairs[pairIndex]

        // Calculate the randomCellIndex for the current pair item
        val isPairFirstItem = itemIndex % 2 == 0

        // Render each pair item
        GridItem(text = if (isPairFirstItem) pair.first else pair.second, onClick = {
            // Update the last clicked index
            if (lastClickedIndex.value == null) {
                lastClickedIndex.value = itemIndex
            }
            // Check if the last click is the other item in the pair
            else {
                val lastIndex = lastClickedIndex.value!!
                val otherItemIndex =
                    if (isPairFirstItem) itemIndex + 1 else itemIndex - 1
                if (lastIndex == otherItemIndex) {
                    onPairClick(pairIndex)
                }
                lastClickedIndex.value = null
            }
        })
    }
}

@Composable
fun GridItem(text: String, onClick: () -> Unit) {
    Box(modifier = Modifier
        .size(200.dp)
        .padding(4.dp)
        .background(color = Color.LightGray)
        .clickable(onClick = { onClick() }),
        contentAlignment = Alignment.Center,
        content = {
            if (text.isNotEmpty()) {
                // Render your UI for the visible pair
                Text(
                    text = text,
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
        })
}


@Composable
fun NonlazyGrid(
    columns: Int, itemCount: Int, content: @Composable() (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var rows = (itemCount / columns)
        if (itemCount.mod(columns) > 0) {
            rows += 1
        }

        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId

                    if (index < itemCount) {
                        content(index)
                    }
                }
            }
        }
    }
}


fun getNewQuiz(
    episode: TVEpisode, resources: Resources
): Quiz {
    // Open the JSON file as a stream
    val inputStream = resources.openRawResource(R.raw.quiz_data)
    val reader = JsonReader(InputStreamReader(inputStream))

    reader.use {
        it.beginArray() // Begin reading the JSON array
        while (it.hasNext()) { // Iterate over array elements
            val quiz = readQuiz(it) // Read a single quiz object
            if (quiz.id == episode.quizId) {
                return quiz
            }
        }
    }

    return DummyQuiz // Return a default quiz if no matching quiz is found
}

private fun readQuiz(reader: JsonReader): Quiz {
    var id: Long = 0
    var wordList = WordList(emptyList())

    reader.beginObject() // Begin reading the JSON object
    while (reader.hasNext()) {
        when (val name = reader.nextName()) {
            "id" -> id = reader.nextLong()
            "wordList" -> wordList = readWordList(reader)
            else -> reader.skipValue() // Skip values of properties we're not interested in
        }
    }
    reader.endObject() // End reading the JSON object

    return Quiz(id, wordList)
}

private fun readWordList(reader: JsonReader): WordList {
    val words = mutableListOf<Word>()

    reader.beginObject() // Begin reading the WordList object
    while (reader.hasNext()) {
        val name = reader.nextName()
        if (name == "words") {
            reader.beginArray() // Begin reading the array of words
            while (reader.hasNext()) {
                words.add(readWord(reader)) // Read each word and add it to the list
            }
            reader.endArray() // End reading the array of words
        } else {
            reader.skipValue() // Skip values of properties we're not interested in
        }
    }
    reader.endObject() // End reading the WordList object

    return WordList(words)
}

private fun readWord(reader: JsonReader): Word {
    var english = ""
    val translatedMap = mutableMapOf<String, String>()

    reader.beginObject() // Begin reading the Word object
    while (reader.hasNext()) {
        val name = reader.nextName()
        when (name) {
            "english" -> english = reader.nextString()
            "translatedMap" -> {
                reader.beginObject() // Begin reading the map of translations
                while (reader.hasNext()) {
                    val language = reader.nextName()
                    val translation = reader.nextString()
                    translatedMap[language] = translation // Add translation to the map
                }
                reader.endObject() // End reading the map of translations
            }

            else -> reader.skipValue() // Skip values of properties we're not interested in
        }
    }
    reader.endObject() // End reading the Word object

    return Word(english, translatedMap)
}


@Composable
fun Swipeable(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    swipeThreshold: Float,
    sensitivityFactor: Float = 3f,
    content: @Composable () -> Unit,
) {
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var dismissRight by remember { mutableStateOf(false) }
    var dismissLeft by remember { mutableStateOf(false) }
    var initializeOffset by remember { mutableStateOf(false) }
    val density = LocalDensity.current.density

    val rotationZValue by animateFloatAsState(
        targetValue = offset.x / 30, animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow,
        ), label = ""
    )

    LaunchedEffect(dismissRight, dismissLeft) {
        if (dismissRight || dismissLeft) {
            delay(300)
            if (dismissRight) onSwipeRight.invoke()
            if (dismissLeft) onSwipeLeft.invoke()
        }
    }
    LaunchedEffect(initializeOffset) {
        if (initializeOffset) {
            delay(300)
            offset = Offset.Zero
            initializeOffset = false
        }
    }

    val animatedOffset = animateOffsetAsState(
        targetValue = if (!initializeOffset) offset else Offset.Zero,
        animationSpec = if (!initializeOffset) tween(durationMillis = 0) else tween(
            durationMillis = 500, easing = EaseIn
        ),
        label = ""
    )

    AnimatedVisibility(
        visible = !(dismissRight || dismissLeft), exit = slideOut(
            targetOffset = {
                IntOffset((offset.x * 5).roundToInt(), (offset.y * 5).roundToInt())
            }, animationSpec = tween(durationMillis = 1500)
        )
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    animatedOffset.value.x.roundToInt(),
                    animatedOffset.value.y.roundToInt()
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(onDragEnd = {
                    if (abs(offset.x) > swipeThreshold) {
                        if (offset.x > 0) {
                            dismissRight = true
                        } else {
                            dismissLeft = true
                        }
                    } else {
                        initializeOffset = true
                    }
                }, onDrag = { _, dragAmount ->
                    val horizontalDragAmount = dragAmount.x / density
                    val verticalDragAmount = dragAmount.y / density

                    offset += androidx.compose.ui.geometry.Offset(
                        horizontalDragAmount * sensitivityFactor,
                        verticalDragAmount * sensitivityFactor
                    )
                })

            }
            .graphicsLayer(
                rotationZ = rotationZValue
            )) {
            content()

            if (offset != Offset.Zero) {
                val iconSize = (abs(offset.x) * 0.3f).coerceIn(0f, 512f).dp
                val painter =
                    if (offset.x > 0) painterResource(id = R.drawable.check_circle)
                    else painterResource(id = R.drawable.x_circle)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = abs(offset.x * 0.015f).coerceIn(0f, 20f).dp,
                            color = if (offset.x > 0f) Color.Green else Color.Red,
                            shape = RoundedCornerShape(20.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painter,
                        contentDescription = if (offset.x > 0) "V" else "X",
                        modifier = Modifier.size(iconSize),
                        tint = if (offset.x > 0) Color.Green else Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun FlipCard(
    front: String,
    back: String,
    onClick: () -> Unit,
    isCurrentCard: Boolean,
) {
    var rotated by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(500, easing = CubicBezierEasing(0.4f, 0.0f, 0.8f, 0.8f)),
        label = ""
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!rotated) 1f else 0f, animationSpec = tween(500), label = ""
    )

    val animateBack by animateFloatAsState(
        targetValue = if (rotated) 1f else 0f, animationSpec = tween(500), label = ""
    )

    val animateColor by animateColorAsState(
        targetValue = if (rotated) Color.LightGray else Color.White,
        animationSpec = tween(500),
        label = ""
    )

    LaunchedEffect(isCurrentCard) {
        if (!isCurrentCard) {
            rotated = false
        }
    }

    Card(modifier = if (isCurrentCard) {
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable {
                rotated = !rotated
                onClick()
            }
    } else Modifier,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = animateColor) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = if (rotated) back else front, modifier = Modifier.graphicsLayer {
                    alpha = if (rotated) animateBack else animateFront
                    rotationY = rotation
                }, fontSize = 60.sp, fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

data class Card(val word: String, val isRevealed: Boolean, val isMatched: Boolean)

@Composable
fun MatchingGame(
    words: List<Pair<String, String>>, onGameComplete: () -> Unit
) {
    val shuffledWords =
        remember { words.shuffled().flatMap { it.toList() } } // Fix type mismatch
    val cardIndices =
        remember { (shuffledWords.indices).shuffled() } // Shuffled card indices

    val cards = remember {
        shuffledWords.mapIndexed { index, word ->
            Card(word, false, false)
        }
    }
    val (revealedCards, setRevealedCards) = remember {
        mutableStateOf(cards.map {
            it.copy(
                isRevealed = false
            )
        })
    }
    val matchedPairs = remember { mutableStateOf(0) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(cardIndices.size) { shuffledIndex ->
            val realIndex = cardIndices[shuffledIndex]
            val card = revealedCards[realIndex]
            MatchCard(content = if (card.isRevealed) {
                // Italicize revealed word
                "<i>${card.word}</i>"
            } else {
                ""
            }, onClick = {
                val newRevealedCards = revealedCards.toMutableList()
                if (!newRevealedCards[realIndex].isRevealed) {
                    newRevealedCards[realIndex] =
                        newRevealedCards[realIndex].copy(isRevealed = true)
                    setRevealedCards(newRevealedCards.toList())
                    checkMatch(newRevealedCards, onGameComplete)
                }
            })
        }
    }
}

fun checkMatch(revealedCards: List<Card>, onGameComplete: () -> Unit) {
    val revealed = revealedCards.filter { it.isRevealed }
    if (revealed.size == 2) {
        if (revealed[0].word == revealed[1].word) {
            val updatedCards = revealedCards.map { card ->
                if (card.word == revealed[0].word) {
                    card.copy(isMatched = true)
                } else {
                    card
                }
            }
//            setRevealedCards(updatedCards)
//            matchedPairs.value++
//            if (matchedPairs.value == words.size) {
//                onGameComplete()
//            }
        } else {
            // Handle mismatch (optional: hide cards after a short delay)
        }
    }
}

@Composable
fun MatchCard(
    content: String, onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clickable { onClick() },
        color = if (content.isEmpty()) Color.LightGray else Color.White
    ) {
        Text(
            text = content,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            color = Color.Black
        )
    }
}

@Composable
fun MatchCube(
    content: String, onMatch: (Boolean) -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }

    Box(modifier = Modifier
        .padding(4.dp)
        .aspectRatio(1f)
        .background(color = Color.Gray)
        .clickable {
            isVisible = false
            onMatch(content == "Word") // Adjust this logic based on your pairs
        }) {
        Text(
            text = content,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            color = Color.White
        )
    }
}