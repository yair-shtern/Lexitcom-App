package lexitcom.lexitcomapp.data.models

data class TranslationState(
    var sourceText: String = "",
    val translatedText: String = "",
    var targetLanguage: String = ""
)
