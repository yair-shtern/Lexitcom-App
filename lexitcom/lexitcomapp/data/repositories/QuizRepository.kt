package lexitcom.lexitcomapp.data.repositories


import kotlinx.coroutines.flow.Flow
import lexitcom.lexitcomapp.data.room.QuizDao
import lexitcom.lexitcomapp.data.models.Quiz


class QuizRepository(private val quizDao: QuizDao)  {

     suspend fun addAQuiz(quiz: Quiz) = quizDao.addQuiz(quiz)

     fun getAllQuizzes(): Flow<List<Quiz>> = quizDao.getAllQuizzes()

     fun getAQuizById(id: Long): Flow<Quiz> = quizDao.getQuizById(id)

     suspend fun updateAQuiz(quiz: Quiz) = quizDao.updateAQuiz(quiz)

     suspend fun deleteAQuiz(quiz: Quiz) = quizDao.deleteQuiz(quiz)
}
