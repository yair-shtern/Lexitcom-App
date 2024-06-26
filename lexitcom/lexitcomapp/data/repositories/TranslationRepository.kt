package lexitcom.lexitcomapp.data.repositories

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import lexitcom.lexitcomapp.utils.Result
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TranslationRepository {

    suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val options = TranslatorOptions
            .Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLanguage)
            .build()

        val languageTranslator = Translation.getClient(options)

        return suspendCoroutine { continuation ->
            languageTranslator.translate(text)
                .addOnSuccessListener { translatedText ->
                    continuation.resume(Result.Success(translatedText))
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
     fun downloadTranslator(targetLanguage: String): Result<Boolean> {
        return try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLanguage)
                .build()

            val languageTranslator = Translation.getClient(options)

            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            languageTranslator.downloadModelIfNeeded(conditions)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}