package lexitcom.lexitcomapp.tmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import lexitcom.lexitcomapp.data.models.TVSeries

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StatisticsSwipeCard(
    state: PagerState = rememberPagerState(),
    seriesInLearning: List<TVSeries>,
) {
    HorizontalPager(
        count = seriesInLearning.size,
        state = state
    ) { page ->
        val seriesList = seriesInLearning.toList()
        val currentSeries = seriesList[page]
//        val lineChartData = currentSeries.progress // Generate LineChartData for the current series

        // You can customize LineChartCompose() with necessary parameters
        Column(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentSeries.name,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            BarChartCompose()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        DotsIndicator(
            totalDots = seriesInLearning.size,
            selectedIndex = state.currentPage,
            selectedColor = Color.Black,
            unSelectedColor = Color.LightGray
        )
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color
) {
    LazyRow {
        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(unSelectedColor)
                )
            }
            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            }
        }
    }
}

@Composable
fun BarChartCompose() {
    val steps = 5
    val barsData = DataUtils.getBarChartData(
        listSize = 8,
        maxRange = 8,
        barChartType = BarChartType.VERTICAL,
        dataCategoryOptions = DataCategoryOptions()
    )
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp
    val xStepsSize = (screenWidth / (barsData.size - 1))

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(MaterialTheme.colorScheme.onBackground)
        .steps(barsData.size - 1)
        .bottomPadding(40.dp)
        .axisLabelAngle(20f)
        .labelData { i -> barsData[i].toString() }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { i ->
            val yScale = 20 / steps
            (i * yScale).toString()
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val barChartData = BarChartData(
        chartData = barsData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.onSecondary
    )

    BarChart(
        modifier = Modifier
            .height(250.dp),
        barChartData = barChartData
    )
}