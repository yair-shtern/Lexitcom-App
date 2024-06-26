package lexitcom.lexitcomapp.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import lexitcom.lexitcomapp.data.models.Quiz

@Dao
interface QuizDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuiz(quizEntity: Quiz)

    @Query("SELECT * FROM `quiz-table`")
    fun getAllQuizzes(): Flow<List<Quiz>>

    @Update
    suspend fun updateAQuiz(quizEntity: Quiz)

    @Delete
    suspend fun deleteQuiz(quizEntity: Quiz)

    @Query("SELECT * FROM `quiz-table` WHERE id=:id")
    fun getQuizById(id: Long): Flow<Quiz>
}