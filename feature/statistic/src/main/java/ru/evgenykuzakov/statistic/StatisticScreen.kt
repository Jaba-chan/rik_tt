package ru.evgenykuzakov.statistic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.designsystem.theme.bodyMediumSemibold
import ru.evgenykuzakov.designsystem.theme.onlineIndicator
import ru.evgenykuzakov.domain.model.StatisticUIState
import ru.evgenykuzakov.domain.model.User


@Composable
fun StatisticScreen(
    viewModel: StatisticScreenViewModel = koinViewModel(),
    paddingValues: PaddingValues
) {
    val state by viewModel.uiState.collectAsState()
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

    when (state) {
        is Resource.Error -> {
            (state as Resource.Error<StatisticUIState>).message?.let { Text(text = it) }
        }

        is Resource.Loading -> {
            Text(text = "Loading")
        }

        is Resource.Success -> {
            val stateData = (state as Resource.Success<StatisticUIState>).data

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
                    ObserversContent(
                        stateData.visitors,
                        stringResource(R.string.count_of_observers_up),
                        arrowIconRes = R.drawable.observers_arrow_up,
                        graphImageRes = R.drawable.observers_up
                    )
                }
                DefaultVerticalSpacer()
                PeriodSelector(
                    options = visitorsByDateOptions,
                    0,
                    {},
                    true
                )
                DefaultVerticalSpacer()
                HeadingCard(
                    headingText = stringResource(R.string.most_often_visitors)
                ) {
                    repeat(stateData.mostOftenVisitors.size) { time ->
                        UserContent(stateData.mostOftenVisitors[time])
                        if (time < stateData.mostOftenVisitors.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 66.dp),
                                color = MaterialTheme.colorScheme.background
                            )
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
                    }
                ) {

                }
                DefaultVerticalSpacer()
                HeadingCard(
                    headingText = stringResource(R.string.observers),
                ) {
                    ObserversContent(
                        stateData.subscribers,
                        stringResource(R.string.new_subscribers_in_this_mouth),
                        arrowIconRes = R.drawable.observers_arrow_up,
                        graphImageRes = R.drawable.observers_up
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.background)
                    ObserversContent(
                        stateData.unsubscribers,
                        stringResource(R.string.count_of_unsubscribers_in_this_mouth),
                        arrowIconRes = R.drawable.observers_arrow_down,
                        graphImageRes = R.drawable.observers_down
                    )
                }
            }

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
            val isSelected = selectedPos == time
            if (isSelected) {
                Button(
                    onClick = { onSelected(time) },
                ) {
                    Text(
                        text = stringResource(options[time]),
                        style = MaterialTheme.typography.bodyMediumSemibold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { onSelected(time) },
                ) {
                    Text(
                        text = stringResource(options[time]),
                        style = MaterialTheme.typography.bodyMediumSemibold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

        }

    }
}

@Composable
private fun DefaultVerticalSpacer() {
    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
fun H2Text(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun Body2Semibold(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMediumSemibold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun HeadingCard(
    headingText: String,
    underHeadingContent: (@Composable ColumnScope.() -> Unit)? = null,
    cardContent: @Composable ColumnScope.() -> Unit
) {
    Column {
        H2Text(text = headingText)
        Spacer(modifier = Modifier.height(12.dp))
        if (underHeadingContent != null) {
            underHeadingContent()
            Spacer(modifier = Modifier.height(12.dp))
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            content = cardContent,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }

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
            Text(
                text = graphDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                )
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
                    .size(38.dp)
                    .clip(CircleShape)
            )
            if (user.isOnline) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.BottomEnd)
                        .background(
                            color = MaterialTheme.colorScheme.onlineIndicator,
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            brush = SolidColor(MaterialTheme.colorScheme.surface),
                            shape = CircleShape
                        )
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













