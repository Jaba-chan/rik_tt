package ru.evgenykuzakov.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.evgenykuzakov.statistic.placeholders.CardVisitorsItem
import ru.evgenykuzakov.statistic.placeholders.CircleChartItem
import ru.evgenykuzakov.statistic.placeholders.HeaderItem
import ru.evgenykuzakov.statistic.placeholders.LineChartItem
import ru.evgenykuzakov.statistic.placeholders.MostOftenVisitors
import ru.evgenykuzakov.statistic.placeholders.ObserversCardItem
import ru.evgenykuzakov.statistic.placeholders.StatisticScreenItem


@Composable
fun StatisticScreen(
    viewModel: StatisticScreenViewModel = koinViewModel(),
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()


    val screenItems = buildList {
        add(StatisticScreenItem.Spacer(height = 48.dp))
        add(StatisticScreenItem.Header)
        add(StatisticScreenItem.VisitorsCard)
        add(StatisticScreenItem.LineChart)
        add(StatisticScreenItem.MostOftenVisitors)
        add(StatisticScreenItem.CircleChart)
        add(StatisticScreenItem.ObserversCard)
        add(StatisticScreenItem.Spacer(height = 32.dp))
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
                start = 16.dp,
                end = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        items(
            items = screenItems,
            key = { it.hashCode() },
            contentType = { item ->
                when (item) {
                    is StatisticScreenItem.Header -> StatisticContentType.Header
                    is StatisticScreenItem.Spacer -> when (item.height) {
                        48.dp -> StatisticContentType.TopSpacer
                        else -> StatisticContentType.BottomSpacer
                    }
                    is StatisticScreenItem.VisitorsCard -> StatisticContentType.CardVisitors
                    is StatisticScreenItem.LineChart -> StatisticContentType.Graph
                    is StatisticScreenItem.MostOftenVisitors -> StatisticContentType.List
                    is StatisticScreenItem.CircleChart -> StatisticContentType.Graph
                    is StatisticScreenItem.ObserversCard -> StatisticContentType.CardObservers
                }
            }

        ){ item ->
            when (item) {
                is StatisticScreenItem.Header -> HeaderItem()
                is StatisticScreenItem.Spacer -> Spacer(
                    modifier = Modifier.height(item.height)
                )
                is StatisticScreenItem.VisitorsCard -> CardVisitorsItem(uiState)
                is StatisticScreenItem.LineChart -> LineChartItem(
                    uiState = uiState,
                    onFilterChanged = viewModel::onLineChartFilterChanged
                )

                is StatisticScreenItem.MostOftenVisitors -> {
                    MostOftenVisitors(uiState)
                }

                is StatisticScreenItem.CircleChart -> CircleChartItem(
                    uiState = uiState,
                    onFilterChanged = viewModel::onCircleChartFilterChanged
                )
                is StatisticScreenItem.ObserversCard -> ObserversCardItem(uiState)
            }
        }
    }
}

sealed class StatisticContentType {
    data object Header : StatisticContentType()
    data object CardVisitors : StatisticContentType()
    data object CardObservers : StatisticContentType()
    data object Graph : StatisticContentType()
    data object List : StatisticContentType()
    data object TopSpacer : StatisticContentType()
    data object BottomSpacer : StatisticContentType()
}






