package ru.evgenykuzakov.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.evgenykuzakov.designsystem.theme.bodyMediumSemibold


@Composable
fun StatisticScreen(
    paddingValues: PaddingValues
) {

    val list =
        mutableListOf(R.string.by_days, R.string.by_weeks, R.string.by_months, R.string.by_days)
    Column(
        modifier = Modifier
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
        DefaultVerticalSpacer()

        PeriodSelector(
            list,
            1,
            {},
            true
        )
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
    Spacer(modifier = Modifier.height(32.dp))
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
fun HeadingCard(
    headingText: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        H2Text(text = headingText)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .wrapContentHeight(),
            content = content
        )
    }

}

@Composable
private fun ObserversContent() {
}






























}