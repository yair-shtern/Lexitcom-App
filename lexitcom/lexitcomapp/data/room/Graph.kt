package lexitcom.lexitcomapp.data.room

import android.content.Context
import androidx.room.Room
import lexitcom.lexitcomapp.data.repositories.QuizRepository

object Graph {
    private lateinit var database: QuizDatabase

    val quizRepository by lazy {
        QuizRepository(quizDao = database.quizDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, QuizDatabase::class.java, "quizzes.db").build()
    }
}