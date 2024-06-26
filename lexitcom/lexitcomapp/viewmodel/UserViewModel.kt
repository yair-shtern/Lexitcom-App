package lexitcom.lexitcomapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lexitcom.lexitcomapp.data.models.SeriesPosition
import lexitcom.lexitcomapp.utils.Result
import lexitcom.lexitcomapp.data.models.User
import lexitcom.lexitcomapp.data.repositories.UserRepository

class UserViewModel(private val userRepository: UserRepository = UserRepository()) : ViewModel() {
    val currentUser: LiveData<User> = userRepository.currentUser
    val favorites: LiveData<Set<Int>> = userRepository.favorites
    val progress: LiveData<Map<Int,SeriesPosition>> = userRepository.progress


    fun addToFavorites(seriesId: Int) {
        viewModelScope.launch {
            when (userRepository.addToFavorites(seriesId)) {
                is Result.Success -> Unit
                else -> {
                    // Handle error, e.g., show a snackbar
                }
            }
        }
    }

    fun removeFromFavorites(seriesId: Int) {
        viewModelScope.launch {
            when (userRepository.removeFromFavorites(seriesId)) {
                is Result.Success -> Unit
                else -> {
                    // Handle error, e.g., show a snackbar
                }
            }
        }
    }

    fun updateProgress(seriesId: Int, position:SeriesPosition) {
        viewModelScope.launch {
            when (userRepository.updateProgress(seriesId,position)) {
                is Result.Success -> Unit
                else -> {
                    // Handle error, e.g., show a snackbar
                }
            }
        }
    }

    fun removeFromProgress(seriesId: Int) {
        viewModelScope.launch {
            when (userRepository.removeFromProgress(seriesId)) {
                is Result.Success -> Unit
                else -> {
                    // Handle error, e.g., show a snackbar
                }
            }
        }
    }

    fun updateUserLanguage(languageCode: String) {
        viewModelScope.launch {
            when (userRepository.updateUserLanguage(languageCode)) {
                is Result.Success -> Unit
                else -> {
                    // Handle error, e.g., show a snackbar
                }
            }
        }
    }

    fun updateUserData(name: String, email: String, password: String) {
        viewModelScope.launch {
            when (userRepository.updateUserData(name = name, email = email, password = password)) {
                is Result.Success -> Unit
                else -> {
                    // Handle error, e.g., show a snackbar
                }
            }
        }
    }

    fun isSeriesInFavorites(seriesId: Int): Boolean {
        return when (val result = userRepository.isSeriesInFavorites(seriesId)) {
            is Result.Success -> result.data
            is Result.Error -> {
                // Handle error, e.g., show a snackbar
                false
            }
        }
    }

    fun getUserLanguage(): String? {
        viewModelScope.launch {
            when (val result = userRepository.getUserLanguage()) {
                is Result.Success -> result.data
                else -> {
                    // Handle error, e.g., show a snackbar

                }
            }
        }
        return null
    }

    fun getUserName(): String? {
        viewModelScope.launch {
            when (val result = userRepository.getUserName()) {
                is Result.Success -> result.data
                else -> {
                    // Handle error, e.g., show a snackbar

                }
            }
        }
        return null
    }

    fun getUserEmail(): String? {
        viewModelScope.launch {
            when (val result = userRepository.getUserEmail()) {
                is Result.Success -> result.data
                else -> {
                    // Handle error, e.g., show a snackbar

                }
            }
        }
        return null
    }

    fun getUserPassword(): String? {
        viewModelScope.launch {
            when (val result = userRepository.getUserPassword()) {
                is Result.Success -> result.data
                else -> {
                    // Handle error, e.g., show a snackbar

                }
            }
        }
        return null
    }

}
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//import lexitcom.lexitcomapp.utils.Result.*
//import lexitcom.lexitcomapp.data.User
//import lexitcom.lexitcomapp.data.repositories.UserRepository
//
//class UserViewModel : ViewModel() {
//
//    private val userRepository: UserRepository
//
//    private val _currentUser = MutableLiveData<User>()
//
//    val currentUser: LiveData<User> get() = _currentUser
//
//    init {
//        userRepository = UserRepository(
////            FirebaseAuth.getInstance(),
////            Injection.instance()
//        )
//        loadCurrentUser()
//    }
//
//    val favorites: LiveData<Set<String>> = userRepository.favorites
//    val positionPerSeries: LiveData<Map<String, Pair<Int, Int>>> = userRepository.positionPerSeries
//
//    val progressPerSeries: LiveData<Map<String, Map<Int, Map<Int, Set<Int>>>>> = userRepository.progressPerSeries
//
//    private fun loadCurrentUser() {
//        viewModelScope.launch {
//            when (val result = userRepository.getCurrentUser()) {
//                is Success -> _currentUser.value = result.data
//                else -> {
//                    // Handle error, e.g., show a snackbar
//                }
//            }
//        }
//    }
//
//    fun addToFavorites(seriesName: String) {
//        viewModelScope.launch {
//            if (_currentUser.value?.favorites?.contains(seriesName) == false) {
//                when (userRepository.addToFavorites(seriesName)) {
//                    is Success -> Unit
//                    is Error -> {
//
//                    }
//                }
//            }
//        }
//    }
//
//    fun removeFromFavorites(seriesName: String) {
//        viewModelScope.launch {
//            if (_currentUser.value?.favorites?.contains(seriesName) == true) {
//                when (userRepository.removeFromFavorites(seriesName)) {
//                    is Success -> Unit
//                    is Error -> {
//
//                    }
//                }
//            }
//        }
//    }
//
//    fun updateSeriesPosition(
//        seriesName: String,
//        seasonNum: Int,
//        episodeNum: Int
//    ) {
//        viewModelScope.launch {
//            when (userRepository.updateSeriesPosition(seriesName, seasonNum, episodeNum)) {
//                is Success -> Unit
//                is Error -> {
//
//                }
//            }
//        }
//    }
//
//    suspend fun removeFromPosition(seriesName: String) {
//        viewModelScope.launch {
//            when (userRepository.removeFromPosition(seriesName)) {
//                is Success -> Unit
//                is Error -> {
//
//                }
//            }
//        }
//    }
//
//    suspend fun updateProgress(
//        seriesName: String,
//        seasonNum: Int,
//        episodeNum: Int,
//        quizzesIndexesLeft: Set<Int>
//    ) {
//        viewModelScope.launch {
//            when (userRepository.updateProgress(
//                seriesName,
//                seasonNum,
//                episodeNum,
//                quizzesIndexesLeft
//            )) {
//                is Success -> Unit
//                is Error -> {
//
//                }
//            }
//        }
//    }
//
//    fun getAllFavorites() {
//        viewModelScope.launch {
//            when (val result = userRepository.getAllFavorites()) {
//                is Success -> result.data
//                is Error -> {
//                }
//
//            }
//        }
//    }
//
//    fun getAllPositions()  {
//        viewModelScope.launch {
//            when (val result = userRepository.getAllPositions()) {
//                is Success -> result.data
//                is Error -> {
//                }
//            }
//        }
//    }
//
//    fun getAllProgress() {
//        viewModelScope.launch {
//            when (val result = userRepository.getAllProgress()) {
//                is Success -> result.data
//                is Error -> {
//
//                }
//            }
//        }
//    }
//
//}