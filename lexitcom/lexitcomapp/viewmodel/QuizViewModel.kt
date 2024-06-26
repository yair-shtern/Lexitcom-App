package lexitcom.lexitcomapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import lexitcom.lexitcomapp.data.room.Graph
import lexitcom.lexitcomapp.data.models.Quiz
import lexitcom.lexitcomapp.data.repositories.QuizRepository


class QuizViewModel(
    private val quizRepository: QuizRepository = Graph.quizRepository
) : ViewModel() {
//    var quizEnglishState by mutableStateOf("")
//    var quizTranslatedState by mutableStateOf("")
//
//    fun onQuizEnglishChanged(newString: String) {
//        quizEnglishState = newString
//    }
//
//    fun onQuizTranslatedChanged(newString: String) {
//        quizTranslatedState = newString
//    }

    lateinit var getAllQuizzes: Flow<List<Quiz>>

    init {
        viewModelScope.launch {
            getAllQuizzes = quizRepository.getAllQuizzes()
        }
    }

     fun addQuiz(quiz: Quiz) {
        viewModelScope.launch(Dispatchers.IO) {
            quizRepository.addAQuiz(quiz)
        }
    }

     fun updateAQuiz(quiz: Quiz) {
        viewModelScope.launch(Dispatchers.IO) {
            quizRepository.updateAQuiz(quiz)
        }
    }

     fun deleteAQuiz(quiz: Quiz) {
        viewModelScope.launch {
            quizRepository.deleteAQuiz(quiz)
        }
    }

    fun getAQuizById(id: Long): Flow<Quiz> {
        return quizRepository.getAQuizById(id)
    }
}