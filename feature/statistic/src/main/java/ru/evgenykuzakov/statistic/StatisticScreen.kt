package ru.evgenykuzakov.statistic

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.model.StatisticUIState
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.model.VisitorType
import ru.evgenykuzakov.ui.Body2Semibold
import ru.evgenykuzakov.ui.H2Text
import ru.evgenykuzakov.ui.HeadingCard


@Composable
fun StatisticScreen(
    viewModel: StatisticScreenViewModel = koinViewModel(),
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = listOf(
        uiState.dateStatistic,
        uiState.visitorsByType,
        uiState.mostOftenVisitors,
        uiState.ageSexStatistic
    ).any { it is Resource.Loading }

    val visitorsByDateOptions =
        mutableListOf(
            R.string.by_days,
            R.string.by_weeks,
            R.string.by_months,
        )
    val ageSexStatOptions = mutableListOf(
        R.string.today,
        R.string.week,
        R.string.month,
        R.string.all_times
    )

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )

        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = stringResource(R.string.statistics),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            HeadingCard(
                headingText = stringResource(R.string.visitors)
            ) {
                when(val visitors = uiState.visitorsByType){
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
            DefaultVerticalSpacer()
            HeadingCard(
                headingText = null,
                underHeadingContent = {
                    PeriodSelector(
                        options = visitorsByDateOptions,
                        0,
                        {},
                        true
                    )
                },
                cardContent = {
                    DefaultVerticalSpacer()
                    Row(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 32.dp
                        )
                    ) {
                        when(val dateStat = uiState.dateStatistic){
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success ->
                                LineChartView(
                                    axis = dateStat.data
                                )
                        }
                    }
                }
            )

            DefaultVerticalSpacer()
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
            DefaultVerticalSpacer()
            HeadingCard(
                headingText = stringResource(R.string.sex_and_age),
                underHeadingContent = {
                    PeriodSelector(
                        options = ageSexStatOptions,
                        onSelected = {},
                        selectedPos = 0,
                        isScrollable = true
                    )
                },
                cardContent = {
                    when(val ageSexStatistic = uiState.ageSexStatistic){
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            val men = ageSexStatistic.data.menCount
                            val women = ageSexStatistic.data.womenCount
                            val totalPeople = men + women
                            CircleChart(
                                totalPeople = totalPeople,
                                stat = ageSexStatistic.data.stats,
                                malePercent = men.toFloat() / totalPeople.toFloat(),
                                femalePercent =women.toFloat() / totalPeople.toFloat()
                            )
                        }
                    }
                }
            )
            DefaultVerticalSpacer()
            HeadingCard(
                headingText = stringResource(R.string.observers),
            ) {
                when(val visitors = uiState.visitorsByType){
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
            Spacer(modifier = Modifier.height(32.dp))
        }

    }
}

    @Composable
    fun PeriodSelector(
        options: List<Int>,
        selectedPos: Int,
        onSelected: (Int) -> Unit,
        isScrollable: Boolean = false
    ) {
        // Заглушка на врямя
        var selectedPosState by rememberSaveable { mutableIntStateOf(selectedPos) }
        Row(
            modifier = Modifier
                .then(
                    if (isScrollable) {
                        Modifier.horizontalScroll(rememberScrollState())
                    } else {
                        Modifier
                    }
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(options.size) { time ->
                val isSelected = selectedPosState == time
                if (isSelected) {
                    Button(
                        onClick = { selectedPosState = time },
                    ) {
                        Body2Semibold(
                            text = stringResource(options[time]),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { selectedPosState = time },
                    ) {
                        Body2Semibold(text = stringResource(options[time]))
                    }
                }

            }

        }
    }

    @Composable
    internal fun DefaultVerticalSpacer() {
        Spacer(modifier = Modifier.height(28.dp))
    }


    @Composable
    internal fun DefaultHorizontalDivider(
        modifier: Modifier = Modifier
    ) {
        HorizontalDivider(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background
        )
    }

    @Composable
    private fun ObserversContent(
        observersCount: Int,
        graphDescription: String,
        graphImageRes: Int,
        arrowIconRes: Int
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
        ) {
            Image(
                modifier = Modifier.size(width = 95.dp, height = 50.dp),
                painter = painterResource(graphImageRes),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    H2Text(text = observersCount.toString())
                    Spacer(modifier = Modifier.width(2.dp))
                    Image(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        painter = painterResource(arrowIconRes),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = graphDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @Composable
    private fun UserContent(
        user: User,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = user.files.firstOrNull()?.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(38.dp)
                )
                if (user.isOnline) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        painter = painterResource(R.drawable.online_point),
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Body2Semibold(text = "${user.username}, ${user.age}")
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
            )
        }
    }









