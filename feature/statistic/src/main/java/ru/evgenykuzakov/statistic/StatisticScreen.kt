package ru.evgenykuzakov.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.statistic.placeholders.CardVisitorsItem
import ru.evgenykuzakov.statistic.placeholders.CircleChartItem
import ru.evgenykuzakov.statistic.placeholders.DefaultVerticalSpacer
import ru.evgenykuzakov.statistic.placeholders.HeaderItem
import ru.evgenykuzakov.statistic.placeholders.LargeSpacer
import ru.evgenykuzakov.statistic.placeholders.LineChartItem
import ru.evgenykuzakov.statistic.placeholders.MediumSpacer
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
    val savedScrollIndex = remember { mutableIntStateOf(0) }
    val savedScrollOffset = remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.ageSexStatistic) {
        if (uiState.ageSexStatistic is Resource.Success) {
            listState.scrollToItem(savedScrollIndex.intValue, savedScrollOffset.intValue)
        }
    }
    val screenItems = buildList {
        add(StatisticScreenItem.LargeSpacer)
        add(StatisticScreenItem.Header)
        add(StatisticScreenItem.MediumSpacer)
        add(StatisticScreenItem.VisitorsCard)
        add(StatisticScreenItem.DefaultSpacer)
        add(StatisticScreenItem.LineChart)
        add(StatisticScreenItem.DefaultSpacer)
        add(StatisticScreenItem.MostOftenVisitors)
        add(StatisticScreenItem.DefaultSpacer)
        add(StatisticScreenItem.CircleChart)
        add(StatisticScreenItem.DefaultSpacer)
        add(StatisticScreenItem.ObserversCard)
        add(StatisticScreenItem.MediumSpacer)
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
    ) {
        itemsIndexed(
            items = screenItems,
            key = { index, item -> "${item.hashCode()}-${index}" },
            contentType = { _, item ->
                when (item) {
                    is StatisticScreenItem.Header -> StatisticContentType.Header
                    is StatisticScreenItem.VisitorsCard -> StatisticContentType.CardVisitors
                    is StatisticScreenItem.LineChart -> StatisticContentType.Graph
                    is StatisticScreenItem.MostOftenVisitors -> StatisticContentType.List
                    is StatisticScreenItem.CircleChart -> StatisticContentType.Graph
                    is StatisticScreenItem.ObserversCard -> StatisticContentType.CardObservers
                    is StatisticScreenItem.DefaultSpacer -> StatisticContentType.DefaultSpacer
                    is StatisticScreenItem.MediumSpacer -> StatisticContentType.MediumSpacer
                    is StatisticScreenItem.LargeSpacer -> StatisticContentType.LargeSpacer
                }
            }

        ){ _, item ->
            when (item) {
                is StatisticScreenItem.Header -> HeaderItem()
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
                    onFilterChanged = {
                        savedScrollIndex.intValue = listState.firstVisibleItemIndex
                        savedScrollOffset.intValue = listState.firstVisibleItemScrollOffset
                        viewModel.onCircleChartFilterChanged(it)
                    }
                )
                is StatisticScreenItem.ObserversCard -> ObserversCardItem(uiState)
                is StatisticScreenItem.DefaultSpacer -> DefaultVerticalSpacer()
                is StatisticScreenItem.MediumSpacer -> MediumSpacer()
                is StatisticScreenItem.LargeSpacer -> LargeSpacer()
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
    data object DefaultSpacer : StatisticContentType()
    data object MediumSpacer : StatisticContentType()
    data object LargeSpacer : StatisticContentType()
}






