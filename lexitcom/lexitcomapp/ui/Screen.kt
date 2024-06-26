package lexitcom.lexitcomapp.ui

import androidx.annotation.DrawableRes
import lexitcom.lexitcomapp.R

sealed class Screen(val title: String, val route: String) {
    object LoginScreen : Screen("Login Screen,", "loginscreen")
    object SignupScreen : Screen("Signup Screen", "signupscreen")
    object HomeScreen : Screen("Home Screen", "homescreen")
    object SeriesScreen : Screen("Series Screen", "seriesscreen")
    object EpisodeScreen : Screen("Episode Screen", "episodescreen")


    sealed class BottomScreen(
        val bTitle: String, val bRoute: String, @DrawableRes val icon: Int
    ) : Screen(bTitle, bRoute) {
        object Home : BottomScreen(
            "Home",
            "home",
            R.drawable.baseline_home_filled_24
        )

        object Account : BottomScreen(
            "Account",
            "account",
            R.drawable.baseline_manage_accounts_24
        )

        object Favorites : BottomScreen(
            "Favorites",
            "favorites",
            R.drawable.baseline_favorite_24
        )
    }

    sealed class SheetScreen(
        val sTitle: String, val sRoute: String, @DrawableRes val icon: Int
    ) : Screen(sTitle, sRoute) {
        object Profile : SheetScreen(
            "Profile",
            "profile",
            R.drawable.baseline_account_circle_24
        )
        object Settings : SheetScreen(
            "Settings",
            "settings",
            R.drawable.baseline_settings_24
        )
        object AboutUs : SheetScreen(
            "About Us",
            "aboutus",
            R.drawable.baseline_help_24
        )
    }
}

val screenInSheet = listOf(
    Screen.SheetScreen.Settings,
    Screen.SheetScreen.Profile,
    Screen.SheetScreen.AboutUs
)

