package lexitcom.lexitcomapp.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import lexitcom.lexitcomapp.ui.Navigation
import lexitcom.lexitcomapp.R
import lexitcom.lexitcomapp.data.models.TVSeries
import lexitcom.lexitcomapp.ui.Screen
import lexitcom.lexitcomapp.ui.screenInSheet
import lexitcom.lexitcomapp.viewmodel.AuthViewModel
import lexitcom.lexitcomapp.viewmodel.SitcomsViewModel
import lexitcom.lexitcomapp.viewmodel.UserViewModel


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainView(authViewModel: AuthViewModel) {

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope: CoroutineScope = rememberCoroutineScope()
    val userViewModel: UserViewModel = viewModel()

    val jsonString =
        LocalContext.current.assets.open("sitcoms.json").bufferedReader().use { it.readText() }
    val sitcoms: List<TVSeries> =
        Gson().fromJson(jsonString, object : TypeToken<List<TVSeries>>() {}.type)

    val sitcomsViewModel = SitcomsViewModel(LocalContext.current)

    val isSheetFullScreen by remember { mutableStateOf(false) }
    val modifier = if (isSheetFullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()

    // Find on which screen (view) we are
    val controller: NavController = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded }
    )

    val roundedCornerRadius = if (isSheetFullScreen) 0.dp else 16.dp


    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = null
                        )
                    }
                })
        },
        bottomBar = {
            createBottomBar(
                currentRoute,
                controller,
                hideSheet = {
                    scope.launch {
                        modalSheetState.hide()
                    }
                }, onSheetOpenerClick = {
                    scope.launch {
                        modalSheetState.show()
                    }
                })
        },
        scaffoldState = scaffoldState,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Navigation(
                navController = controller,
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                sitcomsViewModel = sitcomsViewModel
            )
        }
        ModalBottomSheetLayout(
            sheetState = modalSheetState,
            sheetShape = RoundedCornerShape(
                topStart = roundedCornerRadius,
                topEnd = roundedCornerRadius
            ),
            sheetContent = {
                MoreBottomSheet(
                    modifier = modifier,
                    controller = controller
                ) {
                    scope.launch {
                        modalSheetState.hide()
                    }
                }
            }) {}

    }

}

@Composable
fun MoreBottomSheet(
    modifier: Modifier,
    controller: NavController,
    onCloseSheet: () -> Unit
) {
    Box(
        modifier = modifier
            .height(160.dp)
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            screenInSheet.forEach { screen ->
                Column(
                    modifier = Modifier
                        .clickable {
                            controller.navigate(screen.sRoute)
                            onCloseSheet()
                        }, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.sTitle
                    )
                    Text(text = screen.sTitle, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}


@Composable
private fun createBottomBar(
    currentRoute: String?,
    controller: NavController,
    onSheetOpenerClick: () -> Unit,
    hideSheet: () -> Unit,
) {
    BottomNavigation(
        Modifier.wrapContentSize(),
        backgroundColor = MaterialTheme.colorScheme.secondary
    ) {

        createBottomItem(
            currentRoute,
            Screen.BottomScreen.Home,
            controller,
            false,
            hideSheet
        )

        createBottomItem(
            currentRoute,
            Screen.BottomScreen.Account,
            controller,
            true,
            onSheetOpenerClick
        )

        createBottomItem(
            currentRoute,
            Screen.BottomScreen.Favorites,
            controller,
            false,
            hideSheet
        )

    }
}

@Composable
private fun RowScope.createBottomItem(
    currentRoute: String?,
    screen: Screen.BottomScreen,
    controller: NavController,
    isItSheetOpener: Boolean = false,
    onClick: () -> Unit = {},
) {
    val isSelected = if (isItSheetOpener) {
        screenInSheet.any { item -> item.sRoute == currentRoute }
    } else {
        currentRoute == screen.bRoute
    }

    val tint = if (isSelected) MaterialTheme.colorScheme.onSecondary
    else MaterialTheme.colorScheme.onSecondaryContainer

    BottomNavigationItem(
        selected = isSelected,
        onClick = {
            onClick()
            if (!isItSheetOpener) controller.navigate(screen.bRoute)
        },
        icon = {
            Icon(
                painter = painterResource(id = screen.icon),
                contentDescription = screen.bTitle,
                tint = tint
            )
        },
        label = {
            Text(text = screen.bTitle, color = tint, style = MaterialTheme.typography.titleMedium)
        },
        selectedContentColor = MaterialTheme.colorScheme.onPrimary,
        unselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
}


