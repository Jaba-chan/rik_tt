package ru.evgenykuzakov.statistic

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.designsystem.theme.bodyMediumSemibold
import ru.evgenykuzakov.designsystem.theme.onlineIndicator
import ru.evgenykuzakov.domain.model.AgeGroups
import ru.evgenykuzakov.domain.model.AgeSexStatistic
import ru.evgenykuzakov.domain.model.Sex
import ru.evgenykuzakov.domain.model.StatisticUIState
import ru.evgenykuzakov.domain.model.User
import kotlin.math.roundToInt


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
                            DefaultHorizontalDivider(modifier = Modifier.padding(start = 66.dp))
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
                        val totalPeople = stateData.menCount + stateData.womenCount
                        CircleChart(
                            totalPeople = totalPeople,
                            stat = stateData.ageSexStat,
                            malePercent = stateData.menCount.toFloat() / totalPeople.toFloat(),
                            femalePercent = stateData.womenCount.toFloat() / totalPeople.toFloat()
                        )
                    }
                )
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
                    DefaultHorizontalDivider()
                    ObserversContent(
                        stateData.unsubscribers,
                        stringResource(R.string.count_of_unsubscribers_in_this_mouth),
                        arrowIconRes = R.drawable.observers_arrow_down,
                        graphImageRes = R.drawable.observers_down
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
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
                    Body2Semibold(
                        text = stringResource(options[time]),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { onSelected(time) },
                ) {
                    Body2Semibold(text = stringResource(options[time]))
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
private fun DefaultHorizontalDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    )
}

@Composable
fun Body2Semibold(
    includeFontPadding: Boolean = true,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMediumSemibold.copy(
            platformStyle = PlatformTextStyle(
                includeFontPadding = includeFontPadding
            )
        ),
        color = color
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


@Composable
fun CircleChart(
    stat: List<AgeSexStatistic>,
    totalPeople: Int,
    malePercent: Float,
    femalePercent: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 9.dp,
    chartSize: Dp = 142.dp,
    maleColor: Color = MaterialTheme.colorScheme.primary,
    femaleColor: Color = MaterialTheme.colorScheme.secondary
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DefaultVerticalSpacer()
        Canvas(
            modifier = modifier
                .size(chartSize)
        ) {
            val diameter = size.minDimension - (strokeWidth).toPx()
            val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
            val size = Size(diameter, diameter)

            val space = 4f
            drawArc(
                color = femaleColor,
                startAngle = -90f + space,
                sweepAngle = 360 * femalePercent - 2 * space,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                topLeft = topLeft,
                size = size
            )
            drawArc(
                color = maleColor,
                startAngle = 360 * femalePercent - 90 + 2 * space,
                sweepAngle = 360 * malePercent - 4 * space,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                topLeft = topLeft,
                size = size
            )
        }
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 40.dp,
                    vertical = 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChartSummaryText(
                group = stringResource(R.string.men),
                percent = malePercent,
                chartColor = maleColor
            )
            Spacer(modifier = Modifier.weight(1f))
            ChartSummaryText(
                group = stringResource(R.string.women),
                percent = femalePercent,
                chartColor = femaleColor
            )
        }
        DefaultHorizontalDivider()
        Row {
            Spacer(modifier = Modifier.width(24.dp))
            AgeGroupHeadings()
            Spacer(modifier = Modifier.width(32.dp))
            AgeSexGroupStatistics(
                stat = stat,
                totalPeople = totalPeople,
                maleColor = maleColor,
                femaleColor = femaleColor
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

    }
}

@Composable
private fun ChartSummaryText(
    group: String,
    percent: Float,
    chartColor: Color
) {
    Spacer(
        modifier = Modifier
            .size(10.dp)
            .background(
                color = chartColor,
                shape = CircleShape
            )
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
        text = group,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
        text = (percent * 100).roundToInt().toString() + "%",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun AgeGroupHeadings(
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyMediumSemibold
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .padding(top = 24.dp, end = 32.dp)
    ) {
        val textMeasurer = rememberTextMeasurer()
        AgeGroups.entries.forEach {
            val text =
                if (it != AgeGroups.GROUP_50plus)
                    "${it.range.first}-${it.range.last}"
                else
                    "${it.range.first}>"
            Canvas(
                modifier = Modifier
                    .height(16.dp)
            ) {
                drawCenteredVerticalText(
                    textMeasurer = textMeasurer,
                    textColor = textColor,
                    textStyle = textStyle,
                    textToDraw = text
                )
            }
        }
    }
}

@Composable
private fun AgeSexGroupStatistics(
    totalPeople: Int,
    stat: List<AgeSexStatistic>,
    maleColor: Color,
    femaleColor: Color
) {
    Column(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AgeGroups.entries.forEach { group ->
            val statByAgeGroup = stat.filter { it.ageGroup == group }
            val statForMen = statByAgeGroup.filter { it.sex == Sex.MAN }
            val statForWomen = statByAgeGroup.filter { it.sex == Sex.WOMAN }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                StatBySexItem(
                    totalPeople = totalPeople,
                    list = statForMen,
                    lineColor = maleColor
                )

                StatBySexItem(
                    totalPeople = totalPeople,
                    list = statForWomen,
                    lineColor = femaleColor
                )
            }

        }
    }
}

@Composable
private fun StatBySexItem(
    totalPeople: Int,
    list: List<AgeSexStatistic>,
    lineColor: Color,
    strokeWidth: Dp = 5.dp,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(11.dp)
    ) {
        val deltaY = size.height / 2
        val deltaX = strokeWidth.toPx() / 2
        var percent = if (list.isNotEmpty())
            list[0].visitorsCount.toFloat() / totalPeople.toFloat()
        else 0f
        percent = 1f
        val lineLength = Offset(percent * size.width + deltaX, size.height - deltaY)

        drawLine(
            color = lineColor,
            cap = StrokeCap.Round,
            strokeWidth = strokeWidth.toPx(),
            start = Offset(deltaX, size.height - deltaY),
            end = lineLength
        )
        val text = if (list.isNotEmpty()) "${(percent * 100).roundToInt()}%" else "0%"
        drawCenteredVerticalText(
            startPadding = 10.dp + lineLength.x.toDp(),
            textMeasurer = textMeasurer,
            textToDraw = text,
            textStyle = textStyle,
            textColor = textColor
        )
    }
}

private fun DrawScope.drawCenteredVerticalText(
    startPadding: Dp = 0.dp,
    textMeasurer: TextMeasurer,
    textToDraw: String,
    textStyle: TextStyle,
    textColor: Color
) {
    val text = textMeasurer.measure(
        text = textToDraw,
        style = textStyle
    )
    drawText(
        topLeft = Offset(startPadding.toPx(), 0f),
        textLayoutResult = text,
        color = textColor
    )
}







