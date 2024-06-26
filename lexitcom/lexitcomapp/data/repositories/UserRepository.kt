package lexitcom.lexitcomapp.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lexitcom.lexitcomapp.data.models.SeriesPosition
import lexitcom.lexitcomapp.utils.Result
import lexitcom.lexitcomapp.data.models.User

//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await

class UserRepository(
//    private val auth: FirebaseAuth,
//    private val firestore: FirebaseFirestore
) {

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private val _favorites = MutableLiveData<Set<Int>>()
    val favorites: LiveData<Set<Int>> get() = _favorites

    private val _progress = MutableLiveData<Map<Int, SeriesPosition>>()
    val progress: LiveData<Map<Int, SeriesPosition>> get() = _progress

    init {
        // Initialize user data from Firebase
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val dummyUser = User(
            name = "Dummy User",
            email = "dummyuser@gmail.com",
            password = "Dummy123",
            favorites = mutableSetOf(2, 4),
            userLanguage = "he",
            progress = mutableMapOf(
                3 to SeriesPosition(2, 4),
                5 to SeriesPosition(3, 5)
            )
        )

        _currentUser.postValue(dummyUser)
        _favorites.postValue(dummyUser.favorites)
        _progress.postValue(dummyUser.progress)

//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            val email = currentUser.email
//            if (email != null) {
//                firestore.collection("users").document(email).get().addOnSuccessListener { document ->
//                    val user = document.toObject(User::class.java)
//                    if (user != null) {
//                        _currentUser.postValue(user)
//                        _favorites.postValue(user.favorites)
//                        _positionPerSeries.postValue(user.positionPerSeries)
//                        _progressPerSeries.postValue(user.progressPerSeries)
//                    }
//                }.addOnFailureListener { exception ->
//                    // Handle failure
//                }
//            }
//        }
    }

    fun addToFavorites(seriesId: Int): Result<Boolean> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        user.favorites.apply { add(seriesId) }
        return Result.Success(true)
    }

    fun removeFromFavorites(seriesId: Int): Result<Boolean> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        user.favorites.apply { remove(seriesId) }
        return Result.Success(true)
    }

    fun updateProgress(seriesId: Int, position: SeriesPosition): Result<Boolean> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        user.progress[seriesId] = position
        return Result.Success(true)
    }


    fun removeFromProgress(seriesId: Int): Result<Boolean> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        user.progress.apply { remove(seriesId) }
        return Result.Success(true)
    }


    suspend fun updateUserLanguage(languageCode: String): Result<Boolean> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        updateUser(user.copy(userLanguage = languageCode))
        return Result.Success(true)
    }

    suspend fun updateUserData(name: String, email: String, password: String): Result<Boolean> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        updateUser(user.copy(name = name, email = email, password = password))
        return Result.Success(true)
    }

    fun isSeriesInFavorites(seriesId: Int): Result<Boolean> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        return Result.Success(_favorites.value?.contains(seriesId) ?: false)
    }

    private suspend fun updateUser(user: User) {
        _currentUser.postValue(user)
        _favorites.postValue(user.favorites)
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            val email = currentUser.email
//            if (email != null) {
//                firestore.collection("users").document(email).set(user)
//                    .addOnSuccessListener {
//                        // Update successful
//                    }
//                    .addOnFailureListener { exception ->
//                        // Handle failure
//                    }
//            }
//        }
    }

    fun getUserLanguage(): Result<String> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        return Result.Success(user.userLanguage)
    }

    fun getUserName(): Result<String> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        return Result.Success(user.name)
    }


    fun getUserEmail(): Result<String> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        return Result.Success(user.email)
    }


    fun getUserPassword(): Result<String> {
        val user = _currentUser.value ?: return Result.Error(Exception("User not found"))
        return Result.Success(user.password)
    }

}


//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//
//import kotlinx.coroutines.tasks.await
//
//class UserRepository(
////    private val auth: FirebaseAuth,
////    private val firestore: FirebaseFirestore
//) {
//    val dummyUser = User(
//        firstName = "Dummy",
//        lastName = "User",
//        email = "dummyuser@gmail.com",
//        password = "Dummy123"
//    )
//
////    suspend fun alreadySignUp(): Boolean {
////        return auth.currentUser != null
////    }
//
//    //    suspend fun signUp(
////        email: String,
////        password: String,
////        firstName: String,
////        lastName: String
////    ): Result<Boolean> =
////        try {
////            auth.createUserWithEmailAndPassword(email, password).await()
////            val user = User(firstName, lastName, email)
////            saveUserToFirestore(user)
////            Result.Success(true)
////        } catch (e: Exception) {
////            Result.Error(e)
////        }
////
////    private suspend fun saveUserToFirestore(user: User) {
////        firestore.collection("users").document(user.email).set(user).await()
////    }
////
////    suspend fun login(email: String, password: String): Result<Boolean> =
////        try {
////            auth.signInWithEmailAndPassword(email, password).await()
////            Result.Success(true)
////        } catch (e: Exception) {
////            Result.Error(e)
////        }
////
//
////    fun getChatMessages(roomId: String): Flow<List<Message>> = callbackFlow {
//
////        awaitClose { subscription.remove() }
//
////    }
////            }
////                }
////                    }).isSuccess
////                        doc.toObject(Message::class.java)!!.copy()
////                    trySend(it.documents.map { doc ->
////                querySnapshot?.let {
////            .addSnapshotListener { querySnapshot, _ ->
////            .orderBy("timestamp")
////            .collection("messages")
////        val subscription = firestore.collection("rooms").document(roomId)
//
//
//    suspend fun getCurrentUser(): Result<User> {
//
//        return Result.Success(dummyUser)
//    }
//
//    suspend fun addToFavorites(seriesName: String): Result<Boolean> {
////        try {
////            firestore.collection("rooms").document(roomId)
////                .collection("messages").add(message).await()
//        dummyUser.favorites.add(seriesName)
//        return Result.Success(true)
////        } catch (e: Exception) {
////            kotlin.Result.Error(e)
//    }
//
//    suspend fun removeFromFavorites(seriesName: String): Result<Boolean> {
//        dummyUser.favorites.remove(seriesName)
//        return Result.Success(true)
//    }
//
//    suspend fun updateSeriesPosition(
//        seriesName: String,
//        seasonNum: Int,
//        episodeNum: Int
//    ): Result<Boolean> {
//        dummyUser.positionPerSeries[seriesName] = Pair(seasonNum, episodeNum)
//        return Result.Success(true)
//    }
//
//    suspend fun removeFromPosition(seriesName: String): Result<Boolean> {
//        dummyUser.positionPerSeries.remove(seriesName)
//        return Result.Success(true)
//    }
//
//    suspend fun updateProgress(
//        seriesName: String,
//        seasonNum: Int,
//        episodeNum: Int,
//        quizzesIndexesLeft: Set<Int>
//    ): Result<Boolean> {
//        val seriesProgress = dummyUser.progressPerSeries.getOrPut(seriesName) { mutableMapOf() }
//        val seasonProgress =
//            seriesProgress.getOrPut(seasonNum) { mutableMapOf() }
//        seasonProgress[episodeNum] = quizzesIndexesLeft
//
//        return Result.Success(true)
//    }
//
////        try {
////        val email = auth.currentUser?.email
////        if (email != null) {
////            val userDocument = firestore.collection("users").document(email).get().await()
////            val user = userDocument.toObject(User::class.java)
////            if (user != null) {
////                Log.d("user", "$email")
////                Result.Success(user)
////            } else {
////                Result.Error(Exception("User data not found"))
////            }
////        } else {
////            Result.Error(Exception("User not authenticated"))
////        }
////    } catch (e: Exception)
////    {
////        Result.Error(e)
////    }
//
//
//    suspend fun getAllFavorites(): Result<MutableSet<String>> {
//        // todo try and catch
//        return Result.Success(dummyUser.favorites)
//    }
//
//
//    suspend fun getAllPositions(): Result<MutableMap<String, Pair<Int, Int>>> {
//        // todo try and catch
//        return Result.Success(dummyUser.positionPerSeries)
//    }
//
//    suspend fun getAllProgress(): Result<MutableMap<String, MutableMap<Int, MutableMap<Int, Set<Int>>>>> {
//        // todo try and catch
//        return Result.Success(dummyUser.progressPerSeries)
//    }
//
//}