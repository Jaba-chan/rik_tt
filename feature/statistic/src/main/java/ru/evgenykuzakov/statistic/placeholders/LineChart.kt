package ru.evgenykuzakov.statistic.placeholders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import ru.evgenykuzakov.domain.model.ByDateStatisticFilter
import ru.evgenykuzakov.domain.model.DateStatistic
import ru.evgenykuzakov.statistic.R
import ru.evgenykuzakov.ui.Body2Semibold
import ru.evgenykuzakov.ui.Footnot13Med
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

@Composable
fun LineChartView(
    axis: List<DateStatistic>,
    filter: ByDateStatisticFilter,
    height: Dp = 148.dp,
    gridStrokeSize: Dp = 1.dp,
    graphStrokeSize: Dp = 3.dp,
    pointsSize: Dp = 11.dp,
    textPadding: PaddingValues = PaddingValues(vertical = 12.dp),
    topGraphPadding: Dp = 6.dp,
    startScroll: Dp = 16.dp,
    gridIntervalSize: Dp = 7.dp,
    gridIntervalSpace: Dp = 5.dp,
    legendColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    legendStyle: TextStyle = MaterialTheme.typography.bodySmall,
    canvasBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    graphColor: Color = MaterialTheme.colorScheme.primary
) {
    val density = LocalDensity.current
    val normalizedAxis = axis.normalize(filter)

    val data = normalizedAxis.map { it.visitors }
    val labels = normalizedAxis.map { it.date }
    val textMeasurer = rememberTextMeasurer()
    var scrolledBy by remember { mutableFloatStateOf(with(density) {startScroll.toPx()}) }
    var coerceAtLeast by remember { mutableFloatStateOf(0f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val space = when (filter) {
        ByDateStatisticFilter.DAY -> 40.dp
        ByDateStatisticFilter.WEEK -> 80.dp
        ByDateStatisticFilter.MONTH -> 60.dp
    }

    val startDelta = when (filter) {
        ByDateStatisticFilter.DAY -> 0.dp
        ByDateStatisticFilter.WEEK -> 16.dp
        ByDateStatisticFilter.MONTH -> 16.dp
    }

    val transformableState = TransformableState { _, panState, _ ->
        val pan = if (selectedIndex == null) panState.x else 0f
        scrolledBy = (scrolledBy + pan)
            .coerceAtLeast(-coerceAtLeast)
            .coerceAtMost(with(density) { startScroll.toPx() })
    }

    Box {
        Canvas(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val clickedIndex = data.indices.minByOrNull { index ->
                            val x = index * space.toPx()
                            abs(x - tapOffset.x + scrolledBy)
                        }
                        selectedIndex = clickedIndex
                    }
                }
                .transformable(transformableState)
                .height(
                    height
                            + textPadding.calculateTopPadding()
                            + textPadding.calculateBottomPadding()
                            + legendStyle.lineHeight.value.dp
                )
                .clipToBounds()
                .fillMaxWidth()

        ) {
            val spacing = space.toPx()
            val startDeltaPx = startDelta.toPx()
            val heightPx = height.toPx()
            val graphStrokeSizePx = graphStrokeSize.toPx()
            val pointsSizePx = pointsSize.toPx()
            val gridStrokeSizePx = gridStrokeSize.toPx()
            val gridIntervalSizePx = gridIntervalSize.toPx()
            val gridIntervalSpacePx = gridIntervalSpace.toPx()

            val width = spacing * (data.size)
            val topGraphPaddingPx = topGraphPadding.toPx()
            val gridCount = 3
            coerceAtLeast = width - size.width
            val maxValue = (data.maxOrNull() ?: 0).toFloat()

            translate(left = scrolledBy + startDeltaPx) {
                repeat(gridCount) { i ->
                    val y = heightPx * i / (gridCount - 1) + topGraphPaddingPx
                    drawLine(
                        color = legendColor,
                        start = Offset(-startDeltaPx, y),
                        end = Offset(width, y),
                        strokeWidth = gridStrokeSizePx,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(
                                gridIntervalSizePx,
                                gridIntervalSpacePx
                            )
                        )
                    )
                }
                var needMove = true
                val linePath = Path().apply {
                    data.forEachIndexed { index, value ->
                        val x = spacing * index + startDeltaPx
                        val y = heightPx - (value / maxValue) * heightPx + topGraphPaddingPx
                        if (needMove) moveTo(x, y)
                        if (value >= 0) {
                            needMove = false
                            lineTo(x, y)
                        }
                    }
                }

                drawPath(
                    path = linePath,
                    color = graphColor,
                    style = Stroke(width = graphStrokeSizePx, cap = StrokeCap.Round)
                )

                data.forEachIndexed { index, value ->
                    val x = index * spacing + startDeltaPx
                    val y = heightPx - (value / maxValue) * heightPx + topGraphPaddingPx
                    if (value >= 0){
                        drawCircle(
                            color = canvasBackgroundColor,
                            radius = pointsSizePx / 2,
                            center = Offset(x, y),
                        )
                        drawCircle(
                            color = graphColor,
                            radius = (pointsSizePx - graphStrokeSizePx) / 2,
                            center = Offset(x, y),
                            style = Stroke(width = graphStrokeSizePx)
                        )
                    }

                    drawCenteredToPointText(
                        startPadding = x.toDp(),
                        topPadding = textPadding.calculateTopPadding() + topGraphPadding,
                        canvasHeight = heightPx.toDp(),
                        textMeasurer = textMeasurer,
                        textToDraw = labels[index].toLabel(filter),
                        textColor = legendColor,
                        textStyle = legendStyle
                    )
                }
                selectedIndex?.let { index ->
                    if( data[index] < 0) return@let
                    val x = (index * spacing) + startDeltaPx
                    drawLine(
                        color = graphColor,
                        start = Offset(x, 0f),
                        end = Offset(x, heightPx + gridIntervalSizePx),
                        strokeWidth = gridStrokeSizePx,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(
                                gridIntervalSizePx,
                                gridIntervalSpacePx
                            )
                        )
                    )
                }
            }

        }
        selectedIndex?.let { index ->
            if (data[index] < 0 ) return@let
            val xDp =
                with(LocalDensity.current) { (index * space.toPx() + scrolledBy).toDp() }
            val gap = RoundedCornerShape(8.dp)
            var boxWidth by remember { mutableIntStateOf(0) }
            Box(
                modifier = Modifier
                    .height(72.dp)
                    .offset {
                        val boxCoerceAtMost = space * data.size - with(density) { coerceAtLeast.toDp() + boxWidth.toDp() }
                        IntOffset(
                            x = ((xDp - 40.dp)
                                .coerceAtLeast(0.dp)
                                .coerceAtMost(boxCoerceAtMost).roundToPx()),
                                    y = 0
                        )
                    }
                    .background(
                        color = canvasBackgroundColor,
                        shape = gap
                    )
                    .border(
                        width = 1.dp,
                        color = legendColor,
                        shape = gap
                    )
                    .clickable {
                        selectedIndex = null
                    }
                    .onGloballyPositioned { coordinates ->
                        boxWidth = coordinates.size.width
                    }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    val context = LocalContext.current
                    val visitors = context.resources.getQuantityString(
                        R.plurals.visitors_count,
                        data[index],
                        data[index]
                    )
                    Body2Semibold(
                        text = visitors,
                        color = graphColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Footnot13Med(
                        text = labels[index].format(
                            DateTimeFormatter.ofPattern(
                                "" +
                                        "d MMMM",
                                Locale("ru")
                            )
                        ),
                        color = legendColor
                    )
                }

            }
        }

    }
}

private fun DrawScope.drawCenteredToPointText(
    startPadding: Dp = 0.dp,
    topPadding: Dp,
    canvasHeight: Dp,
    textMeasurer: TextMeasurer,
    textToDraw: String,
    textStyle: TextStyle,
    textColor: Color
) {
    val text = textMeasurer.measure(
        text = textToDraw,
        style = textStyle
    )
    println(textToDraw)
    drawText(
        topLeft = Offset(
            startPadding.toPx() - text.size.width / 2,
            canvasHeight.toPx() + topPadding.toPx()
        ),
        textLayoutResult = text,
        color = textColor
    )
}


private fun List<DateStatistic>.normalize(filter: ByDateStatisticFilter): List<DateStatistic> {
    val threshold = when(filter){
        ByDateStatisticFilter.DAY -> 9
        ByDateStatisticFilter.WEEK -> 5
        ByDateStatisticFilter.MONTH -> 6
    }
    return if (this.size < threshold) {
        val extraAxis = mutableListOf<DateStatistic>()
        val tailSize = (threshold - this.size) / 2
        val headSize = threshold - this.size - tailSize
        println("headSixe: $headSize")

        repeat(tailSize) { time ->
            extraAxis.add(
                DateStatistic(
                    date = this[0].date.produceExtraDate(true, headSize - time, filter),
                    Int.MIN_VALUE
                )
            )
        }
        extraAxis.addAll(this)
        repeat(headSize) { time ->
            extraAxis.add(
                DateStatistic(
                    date = this[0].date.produceExtraDate(false, time, filter),
                    Int.MIN_VALUE
                )
            )
        }
        extraAxis
    } else this
}

private fun LocalDate.produceExtraDate(
    isHead: Boolean,
    position: Int,
    filter: ByDateStatisticFilter
): LocalDate {
    return when (filter) {
        ByDateStatisticFilter.DAY -> if (isHead) this.minusDays(position.toLong()) else this.plusDays(
            position.toLong()
        )
        ByDateStatisticFilter.WEEK -> if (isHead) this.minusDays(position.toLong() * 7) else this.plusDays(
            position.toLong() * 7
        )
        ByDateStatisticFilter.MONTH -> if (isHead) this.minusMonths(position.toLong()) else this.plusMonths(
            position.toLong()
        )
    }
}

private fun LocalDate.toLabel(
    filter: ByDateStatisticFilter
): String{
    return when (filter) {
        ByDateStatisticFilter.DAY -> this.format(
            DateTimeFormatter.ofPattern("d.MM")
        )
        ByDateStatisticFilter.WEEK -> {
            val firstDayOfWeek =
                this.format(DateTimeFormatter.ofPattern("d.MM"))
            val lastDayOfWeek = this.plusDays(7)
                .format(DateTimeFormatter.ofPattern("d.MM"))
            "$firstDayOfWeek-$lastDayOfWeek"
        }
        ByDateStatisticFilter.MONTH -> this.format(
            DateTimeFormatter.ofPattern("MM.yyyy")
        )
    }
}
