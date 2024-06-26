package lexitcom.lexitcomapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(tableName = "quiz-table")
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "wordList", defaultValue = "")
    var wordList: WordList
)

data class Word(
    val english: String,
    var translatedMap: Map<String, String>
)

data class WordList(
    var words: List<Word>
)

class RoomTypeConverters {
    @TypeConverter
    fun convertWordListToJSONString(wordList: WordList): String = Gson().toJson(wordList)

    @TypeConverter
    fun convertJSONStringToWordList(jsonString: String): WordList {
        return try {
            Gson().fromJson(jsonString, WordList::class.java)
        } catch (e: Exception) {
            // Handle the exception, such as logging an error or returning a default value
            // For simplicity, returning an empty WordList here
            WordList(emptyList())
        }
    }

}
val DummyQuiz = Quiz(
    wordList = WordList(
        listOf(
            Word(
                english = "Hello",
                translatedMap = mapOf(
                    "fr" to "Bonjour",
                    "es" to "Hola",
                    "he" to "שלום",
                    "de" to "Hallo"
                )
            ),
            Word(
                english = "Goodbye",
                translatedMap = mapOf(
                    "fr" to "Au revoir",
                    "es" to "Adiós",
                    "he" to "להתראות",
                    "de" to "Auf Wiedersehen"
                )
            ),
            Word(
                english = "Friend",
                translatedMap = mapOf(
                    "fr" to "Ami",
                    "es" to "Amigo",
                    "he" to "חבר",
                    "de" to "Freund"
                )
            ),
            Word(
                english = "Book",
                translatedMap = mapOf(
                    "fr" to "Livre",
                    "es" to "Libro",
                    "he" to "ספר",
                    "de" to "Buch"
                )
            ),
            Word(
                english = "Cat",
                translatedMap = mapOf(
                    "fr" to "Chat",
                    "es" to "Gato",
                    "he" to "חתול",
                    "de" to "Katze"
                )
            ),
            Word(
                english = "Dog",
                translatedMap = mapOf(
                    "fr" to "Chien",
                    "es" to "Perro",
                    "he" to "כלב",
                    "de" to "Hund"
                )
            ),
            Word(
                english = "House",
                translatedMap = mapOf(
                    "fr" to "Maison",
                    "es" to "Casa",
                    "he" to "בית",
                    "de" to "Haus"
                )
            ),
            Word(
                english = "Car",
                translatedMap = mapOf(
                    "fr" to "Voiture",
                    "es" to "Coche",
                    "he" to "מכונית",
                    "de" to "Auto"
                )
            ),
            Word(
                english = "Computer",
                translatedMap = mapOf(
                    "fr" to "Ordinateur",
                    "es" to "Computadora",
                    "he" to "מחשב",
                    "de" to "Computer"
                )
            ),
            Word(
                english = "Tree",
                translatedMap = mapOf(
                    "fr" to "Arbre",
                    "es" to "Árbol",
                    "he" to "עץ",
                    "de" to "Baum"
                )
            )
        )
    )
)
