package lexitcom.lexitcomapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import lexitcom.lexitcomapp.data.repositories.UserRepository

class AuthViewModel : ViewModel() {
    private val userRepository: UserRepository

    init {
        userRepository = UserRepository(
//            FirebaseAuth.getInstance(),
//            Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<Result<Boolean>>()
//    val authResult: LiveData<Result<Boolean>> get() = _authResult
//
//    fun signUp(email: String, password: String, firstName: String, lastName: String) {
//        viewModelScope.launch {
//            _authResult.value = userRepository.signUp(email, password, firstName, lastName)
//        }
//    }

//    fun login(email: String, password: String) {
//        viewModelScope.launch {
//            _authResult.value = userRepository.login(email, password)
//        }
//    }
}