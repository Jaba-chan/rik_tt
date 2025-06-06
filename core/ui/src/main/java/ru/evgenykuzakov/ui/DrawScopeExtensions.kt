package ru.evgenykuzakov.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun DrawScope.drawCenteredVerticalText(
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

