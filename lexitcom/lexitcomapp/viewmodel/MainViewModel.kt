package lexitcom.lexitcomapp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import lexitcom.lexitcomapp.ui.Screen

class MainViewModel : ViewModel() {

    private val _currentScreen: MutableState<Screen> =
        mutableStateOf(Screen.BottomScreen.Home)

    val currentScreen: MutableState<Screen>
        get() = _currentScreen

    fun setCurrentScreen(screen: Screen){
        _currentScreen.value = screen
    }
}