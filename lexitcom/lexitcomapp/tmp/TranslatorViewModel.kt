package lexitcom.lexitcomapp.tmp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lexitcom.lexitcomapp.utils.Result
import lexitcom.lexitcomapp.data.repositories.TranslationRepository
import lexitcom.lexitcomapp.data.models.TranslationState

class TranslatorViewModel(
    private val translationRepository:
    TranslationRepository = TranslationRepository()
) : ViewModel() {

    private val _state = mutableStateOf(TranslationState())
    val state: State<TranslationState> = _state

    fun translate(context: Context) {
        viewModelScope.launch {
            when (val translatedText = translationRepository.translate(
                state.value.sourceText,
                state.value.targetLanguage
            )) {
                is Result.Success -> {
                    _state.value = state.value.copy(
                        translatedText = translatedText.data
                    )
                }

                else -> {
                    // Handle error, e.g., show a snackbar
                    Log.d("here", translatedText.toString())
                    Toast.makeText(
                        context,
                        "Downloading started..",
                        Toast.LENGTH_SHORT
                    ).show()
                    downloadTranslator(context)
                }
            }
        }
    }

    fun downloadTranslator(
        context: Context
    ) {
        viewModelScope.launch {
            when (val result =
                translationRepository.downloadTranslator(state.value.targetLanguage)) {
                is Result.Success -> {
                    Toast.makeText(
                        context,
                        "Downloaded model successfully..",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    // Handle error, e.g., show a snackbar
                    Toast.makeText(
                        context,
                        "Some error occurred couldn't download language model..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun updateSourceText(text: String) {
        _state.value = state.value.copy(sourceText = text)
    }
}