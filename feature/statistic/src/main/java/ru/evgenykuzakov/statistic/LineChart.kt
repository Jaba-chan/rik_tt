package ru.evgenykuzakov.statistic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import ru.evgenykuzakov.domain.model.DateStatistic
import ru.evgenykuzakov.ui.Body2Semibold
import ru.evgenykuzakov.ui.Footnot13Med
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LineChartView(
    axis: List<DateStatistic>,
    gridStrokeSize: Dp = 1.dp,
    graphStrokeSize: Dp = 3.dp,
    pointsSize: Dp = 11.dp,
    gridHeight: Dp = 148.dp,
    textTopPadding: Dp = 12.dp,
    legendColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    legendStyle: TextStyle = MaterialTheme.typography.bodySmall,
    canvasBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    graphColor: Color = MaterialTheme.colorScheme.primary
) {
    val data = axis.map { it.visitors }
    val labels = axis.map { it.date }
    val textMeasurer = rememberTextMeasurer()
    var scrolledBy by remember { mutableFloatStateOf(0f) }
    var coerceAtLeast by remember { mutableFloatStateOf(0f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val density = LocalDensity.current
    val space: Dp = 40.dp
    val transformableState = TransformableState { _, panState, _ ->
        val pan = if (selectedIndex == null) panState.x else 0f
        scrolledBy = (scrolledBy + pan)
            .coerceAtLeast(-coerceAtLeast)
            .coerceAtMost(with(density) { 16.dp.toPx() })
    }
    Box {
        Canvas(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val clickedIndex = data.indices.minByOrNull { index ->
                            val x = index * space.toPx()
                            kotlin.math.abs(x - tapOffset.x + scrolledBy)
                        }
                        selectedIndex = clickedIndex
                    }
                }
                .transformable(transformableState)
                .height(gridHeight + 2 * textTopPadding + legendStyle.lineHeight.value.dp)
                .clipToBounds()
                .fillMaxWidth()

        ) {
            val spacing = space.toPx()
            val width = spacing * (data.size - 1) + 32.dp.toPx()
            coerceAtLeast = width - size.width
            val gridHeightPx = gridHeight.toPx()
            val maxValue = (data.maxOrNull() ?: 0).toFloat()
            val gridCount = 3
            val topGraphPadding = 6.dp.toPx()

            translate(left = scrolledBy) {
                repeat(gridCount) { i ->
                    val y = gridHeightPx * i / (gridCount - 1) + topGraphPadding
                    drawLine(
                        color = legendColor,
                        start = Offset(3.5.dp.toPx(), y),
                        end = Offset(width, y),
                        strokeWidth = gridStrokeSize.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(
                                7.dp.toPx(),
                                5.dp.toPx()
                            )
                        )
                    )
                }

                val linePath = Path().apply {
                    data.forEachIndexed { index, value ->
                        val x = spacing * index
                        val y = gridHeightPx - (value / maxValue) * gridHeightPx + topGraphPadding
                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }

                drawPath(
                    path = linePath,
                    color = graphColor,
                    style = Stroke(width = graphStrokeSize.toPx(), cap = StrokeCap.Round)
                )

                data.forEachIndexed { index, value ->
                    val x = index * spacing
                    val y = gridHeightPx - (value / maxValue) * gridHeightPx + topGraphPadding
                    drawCircle(
                        color = canvasBackgroundColor,
                        radius = (pointsSize / 2).toPx(),
                        center = Offset(x, y),
                    )
                    drawCircle(
                        color = graphColor,
                        radius = (pointsSize.toPx() - graphStrokeSize.toPx()) / 2,
                        center = Offset(x, y),
                        style = Stroke(width = graphStrokeSize.toPx())
                    )
                    drawCenteredToPointText(
                        startPadding = x.toDp(),
                        topPadding = textTopPadding + topGraphPadding.toDp(),
                        canvasHeight = gridHeightPx.toDp(),
                        textMeasurer = textMeasurer,
                        textToDraw = labels[index].format(DateTimeFormatter.ofPattern("d.MM")),
                        textColor = legendColor,
                        textStyle = legendStyle
                    )
                }
                selectedIndex?.let { index ->
                    val x = (index * spacing)
                    println(index)
                    drawLine(
                        color = graphColor,
                        start = Offset(x, 0f),
                        end = Offset(x, gridHeightPx + 7.dp.toPx()),
                        strokeWidth = gridStrokeSize.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(
                                7.dp.toPx(),
                                5.dp.toPx()
                            )
                        )
                    )
                }
            }

        }
        selectedIndex?.let { index ->
            val xDp =
                with(LocalDensity.current) { (index * space.toPx() + scrolledBy).toDp()  }
            val gap = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .height(72.dp)
                    .offset(
                        x = xDp - 40.dp,
                        y = 0.dp
                    )
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
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
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
                            DateTimeFormatter.ofPattern("" +
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
    drawText(
        topLeft = Offset(
            startPadding.toPx() - text.size.width / 2,
            canvasHeight.toPx() + topPadding.toPx()
        ),
        textLayoutResult = text,
        color = textColor
    )
}

fun LocalDate.toDayMonthNumeric(formatter: DateTimeFormatter): String {
    return this.format(formatter)
}