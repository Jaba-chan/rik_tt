package ru.evgenykuzakov.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HeadingCard(
    headingText: String? = null,
    underHeadingContent: (@Composable ColumnScope.() -> Unit)? = null,
    cardContent: @Composable ColumnScope.() -> Unit
) {
    Column {
        if (headingText != null) {
            H2Text(text = headingText)
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (underHeadingContent != null) {
            underHeadingContent()
            Spacer(modifier = Modifier.height(12.dp))
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize(),
            content = cardContent,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }

}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
    processIndicatorSize: Dp = 40.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(processIndicatorSize)
                .align(alignment = Alignment.Center)
        )
    }
}