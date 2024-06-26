package lexitcom.lexitcomapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lexitcom.lexitcomapp.data.models.SeriesPosition
import lexitcom.lexitcomapp.ui.screens.AboutUs
import lexitcom.lexitcomapp.ui.screens.EpisodeScreen
import lexitcom.lexitcomapp.ui.screens.Favorites
import lexitcom.lexitcomapp.ui.screens.Home
import lexitcom.lexitcomapp.ui.screens.Profile
import lexitcom.lexitcomapp.ui.screens.SeriesScreen
import lexitcom.lexitcomapp.ui.screens.Settings
import lexitcom.lexitcomapp.viewmodel.AuthViewModel
import lexitcom.lexitcomapp.viewmodel.SitcomsViewModel
import lexitcom.lexitcomapp.viewmodel.UserViewModel

@Composable
fun Navigation(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    sitcomsViewModel: SitcomsViewModel
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screen.BottomScreen.Home.bRoute // TODO
    ) {

        composable(Screen.BottomScreen.Home.bRoute) {
            Home(
                userViewModel = userViewModel,
                sitcomsViewModel = sitcomsViewModel,
                navigateToSeries = { seriesId ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("seriesId", seriesId)
                    navController.navigate(Screen.SeriesScreen.route)
                },
                navigateToEpisode = { seriesId, seriesPosition ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("seriesId", seriesId)
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "seriesPosition",
                        seriesPosition
                    )
                    navController.navigate(Screen.EpisodeScreen.route)
                })
        }

        composable(Screen.BottomScreen.Favorites.bRoute) {
            Favorites(
                userViewModel = userViewModel,
                sitcomsViewModel = sitcomsViewModel,
                navigateToSeries = { seriesId ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("seriesId", seriesId)
                    navController.navigate(Screen.SeriesScreen.route)
                }
            )
        }


        composable(Screen.SheetScreen.Profile.sRoute) {
            Profile(userViewModel = userViewModel)
        }
        composable(Screen.SheetScreen.Settings.sRoute) {
            Settings(userViewModel = userViewModel)
        }
        composable(Screen.SheetScreen.AboutUs.sRoute) {
            AboutUs()
        }
//        composable(Screen.SignupScreen.route) {
//            SignUpScreen(
//                authViewModel = authViewModel,
//                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
//            )
//        }
//        composable(Screen.LoginScreen.route) {
//            LoginScreen(
//                authViewModel = authViewModel,
//                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) }
//            ) {
//                navController.navigate(Screen.ChatRoomsScreen.route)
//            }
//        }
//        composable(Screen.HomeScreen.route) {
//            Home(navigateToSeries = { series ->
//                navController.currentBackStackEntry?.savedStateHandle?.set("series", series)
//                navController.navigate(Screen.SeriesScreen.route)
//            })
//        }

        composable(Screen.SeriesScreen.route) {
            val seriesId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("seriesId")
                    ?: -1

            SeriesScreen(
                seriesId = seriesId,
                sitcomsViewModel = sitcomsViewModel,
                userViewModel = userViewModel,
            ) { _, seriesPosition ->
                navController.currentBackStackEntry?.savedStateHandle?.set("seriesId", seriesId)
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "seriesPosition",
                    seriesPosition
                )
                navController.navigate(Screen.EpisodeScreen.route)
            }
        }

        composable(Screen.EpisodeScreen.route) {
            val seriesId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("seriesId")
                    ?: -1
            val seriesPosition =
                navController.previousBackStackEntry?.savedStateHandle?.get<SeriesPosition>("seriesPosition")
                    ?: SeriesPosition(-1,-1)
            EpisodeScreen(
                seriesId = seriesId,
                seriesPosition = seriesPosition,
                userViewModel = userViewModel,
                sitcomsViewModel = sitcomsViewModel
            )
        }

    }
}

