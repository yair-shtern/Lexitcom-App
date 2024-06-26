package lexitcom.lexitcomapp.tmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun TranslatorScreen(
    translatorViewModel: TranslatorViewModel
) {
    val translationState = translatorViewModel.state.value
    translationState.targetLanguage = "he"
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        TextField(
            value = translationState.sourceText,
            onValueChange = {
                translatorViewModel.updateSourceText(it)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(7.dp))
        Button(
            onClick = {
                translatorViewModel.translate(context = context)
                translationState.sourceText = ""
            },
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        ) {
            Text(text = "Translate")
        }
        Spacer(modifier = Modifier.height(7.dp))
        Text(text = translationState.translatedText)
    }
//    val languageMap = mapOf(
//        "Hebrew" to "he",
//        "Spanish" to "es",
//        "French" to "fr",
//        "German" to "de",
//        // Add more languages as needed
//    ) // todo remove?
//
//    val translationData by translatorViewModel.translationData.collectAsState()
//    var isDropdownExpanded by remember { mutableStateOf(false) }
//    var sourceText by remember { mutableStateOf("") }
//    var language by remember {
//        mutableStateOf(userViewModel.currentUser.value?.userLanguage ?: "he")
//    }
//
//    Column {
//        Box(modifier = Modifier.padding(4.dp)) {
//            TextField(
//                value = sourceText,
//                onValueChange = { sourceText = it },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Button(onClick = {
//                translatorViewModel.updateTargetLanguage(languageCode = language)
//                isDropdownExpanded = true
//            }) {
//                Text(
//                    text = languageMap.entries.find { it.value == language }!!.key,
//                    style = buttonTextStyle
//                )
//                Icon(
//                    Icons.Default.ArrowDropDown,
//                    tint = PeachyWhite,
//                    contentDescription = "Arrow Down"
//                )
//            }
//            DropdownMenu(
//                expanded = isDropdownExpanded,
//                onDismissRequest = { isDropdownExpanded = false },
//                modifier = Modifier.offset(y = if (isDropdownExpanded) 0.dp else 200.dp)
//            ) {
//                languageMap.forEach { (languageName, languageCode) ->
//                    DropdownMenuItem(
//                        onClick = {
//                            translatorViewModel.updateTargetLanguage(languageCode)
//                            userViewModel.updateUserLanguage(languageCode)
//                            language = languageCode
//                            isDropdownExpanded = false
//                        }
//                    ) {
//                        Text(languageName)
//                    }
//                }
//            }
//        }
//        Button(onClick = {
//            translatorViewModel.translateText(sourceText)
//            sourceText = ""
//
//        }) {
//            Text(text = "Translate")
//        }
//        Text(translationData.translatedText ?: "")
//    }
}