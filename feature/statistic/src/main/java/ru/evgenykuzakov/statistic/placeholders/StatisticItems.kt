package ru.evgenykuzakov.statistic.placeholders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.statistic.R
import ru.evgenykuzakov.statistic.StatisticUIState
import ru.evgenykuzakov.ui.H2Text
import ru.evgenykuzakov.ui.HeadingCard


@Composable
fun HeaderItem(){
    Text(
        text = stringResource(R.string.statistics),
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun LineChartItem(
    uiState: StatisticUIState,
    onFilterChanged: (Int) -> Unit
) {
    val visitorsByDateOptions = listOf(
        R.string.by_days,
        R.string.by_weeks,
        R.string.by_months
    )

    HeadingCard(
        headingText = null,
        underHeadingContent = {
            PeriodSelector(
                options = visitorsByDateOptions,
                selectedPos = uiState.dateFilter.ordinal,
                onSelected = onFilterChanged,
            )
        },
        cardContent = {
            DefaultVerticalSpacer()
            Row(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 32.dp
                    )
            ) {
                when (val dateStat = uiState.dateStatistic) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        LineChartView(
                            filter = uiState.dateFilter,
                            axis = dateStat.data
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CircleChartItem(
    uiState: StatisticUIState,
    onFilterChanged: (Int) -> Unit
) {
    val ageSexStatOptions = listOf(
        R.string.today,
        R.string.week,
        R.string.month,
        R.string.all_times
    )

    HeadingCard(
        headingText = stringResource(R.string.sex_and_age),
        underHeadingContent = {
            PeriodSelector(
                options = ageSexStatOptions,
                selectedPos = uiState.ageSexFilter.ordinal,
                onSelected = onFilterChanged,
            )
        },
        cardContent = {
            when (val ageSexStatistic = uiState.ageSexStatistic) {
                is Resource.Error -> {}
                is Resource.Loading -> {}
                is Resource.Success -> {
                    val men = ageSexStatistic.data.menCount
                    val women = ageSexStatistic.data.womenCount
                    val totalPeople = men + women
                    if (totalPeople > 0)
                        CircleChart(
                            totalPeople = totalPeople,
                            stat = ageSexStatistic.data.stats,
                            malePercent = men.toFloat() / totalPeople.toFloat(),
                            femalePercent = women.toFloat() / totalPeople.toFloat()
                        )
                    else
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            H2Text(
                                text = stringResource(R.string.now_empty),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                }
            }
        }
    )
}

@Composable
fun CardVisitorsItem(
    uiState: StatisticUIState
) {
    HeadingCard(
        headingText = stringResource(R.string.visitors)
    ) {
        when (val visitors = uiState.visitorsByType) {
            is Resource.Error -> {}
            is Resource.Loading -> {}
            is Resource.Success -> {
                ObserversContent(
                    visitors.data.view.count,
                    stringResource(R.string.count_of_observers_up),
                    arrowIconRes = R.drawable.observers_arrow_up,
                    graphImageRes = R.drawable.observers_up
                )
            }
        }
    }
}

@Composable
fun MostOftenVisitors(
    uiState: StatisticUIState
){
    HeadingCard(
        headingText = stringResource(R.string.most_often_visitors)
    ) {
        when(val mostOftenVisitors = uiState.mostOftenVisitors){
            is Resource.Error -> {}
            is Resource.Loading -> {}
            is Resource.Success ->
                repeat(mostOftenVisitors.data.size) { time ->
                    UserContent(mostOftenVisitors.data[time])
                    if (time < mostOftenVisitors.data.size - 1) {
                        DefaultHorizontalDivider(modifier = Modifier.padding(start = 66.dp))
                    }
                }
        }
    }
}

@Composable
fun ObserversCardItem(
    uiState: StatisticUIState
) {
    HeadingCard(
        headingText = stringResource(R.string.observers),
    ) {
        when (val visitors = uiState.visitorsByType) {
            is Resource.Error -> {}
            is Resource.Loading -> {}
            is Resource.Success -> {
                ObserversContent(
                    visitors.data.subscribers.count,
                    stringResource(R.string.new_subscribers_in_this_mouth),
                    arrowIconRes = R.drawable.observers_arrow_up,
                    graphImageRes = R.drawable.observers_up
                )
                DefaultHorizontalDivider()
                ObserversContent(
                    visitors.data.unsubscribers.count,
                    stringResource(R.string.count_of_unsubscribers_in_this_mouth),
                    arrowIconRes = R.drawable.observers_arrow_down,
                    graphImageRes = R.drawable.observers_down
                )
            }
        }
    }
}

sealed class StatisticScreenItem {
    data object Header : StatisticScreenItem()
    data class  Spacer(val height: Dp) : StatisticScreenItem()
    data object VisitorsCard : StatisticScreenItem()
    data object LineChart : StatisticScreenItem()
    data object MostOftenVisitors : StatisticScreenItem()
    data object CircleChart : StatisticScreenItem()
    data object ObserversCard : StatisticScreenItem()
}

