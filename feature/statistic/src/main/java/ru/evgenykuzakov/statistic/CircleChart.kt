package ru.evgenykuzakov.statistic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.evgenykuzakov.designsystem.theme.bodyMediumSemibold
import ru.evgenykuzakov.domain.model.AgeGroups
import ru.evgenykuzakov.domain.model.AgeSexStat
import ru.evgenykuzakov.domain.model.Sex
import ru.evgenykuzakov.ui.Footnot13Med
import ru.evgenykuzakov.ui.drawCenteredVerticalText
import kotlin.math.roundToInt

@Composable
fun CircleChart(
    stat: List<AgeSexStat>,
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
            val space = if (femalePercent == 1f || malePercent == 1f ) 0f else 4f
            if (femalePercent > 0)
                drawArc(
                    color = femaleColor,
                    startAngle = -90f + space,
                    sweepAngle = 360 * femalePercent - 2 * space,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                    topLeft = topLeft,
                    size = size
                )
            if (malePercent > 0)
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
    Footnot13Med(text = group)
    Spacer(modifier = Modifier.width(6.dp))
    Footnot13Med(text = (percent * 100).roundToInt().toString() + "%")
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
                    "${it.range.first}-${it.range.last + 1}"
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
    stat: List<AgeSexStat>,
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
    list: List<AgeSexStat>,
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
            .padding(end = 52.dp)
    ) {
        val deltaY = size.height / 2
        val deltaX = strokeWidth.toPx() / 2
        val percent = if (list.isNotEmpty())
            list[0].visitorsCount.toFloat() / totalPeople.toFloat()
        else 0f
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


