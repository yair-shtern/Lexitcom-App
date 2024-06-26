package lexitcom.lexitcomapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lexitcom.lexitcomapp.viewmodel.SitcomsViewModel
import lexitcom.lexitcomapp.viewmodel.UserViewModel

@Composable
fun Favorites(
    userViewModel: UserViewModel,
    navigateToSeries: (Int) -> Unit,
    sitcomsViewModel: SitcomsViewModel
) {
    val favoritesId by userViewModel.favorites.observeAsState(emptySet())

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Your Favorites", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(favoritesId.mapNotNull { id ->
                sitcomsViewModel.getSitcomById(id)
            })
            { series ->
                SeriesCard(
                    series = series,
                    navigateToSeries = navigateToSeries,
                    modifier = Modifier
                )
            }
        }
    }
}
