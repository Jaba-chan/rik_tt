package ru.evgenykuzakov.statistic.placeholders

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.statistic.R
import ru.evgenykuzakov.ui.Body2Semibold
import ru.evgenykuzakov.ui.H2Text

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
internal fun PeriodSelector(
    options: List<Int>,
    selectedPos: Int,
    onSelected: (Int) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(selectedPos) {
        listState.animateScrollToItem(selectedPos)
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(options) { index, _ ->
            val isSelected = selectedPos == index
            if (isSelected) {
                Button(
                    onClick = {
                        onSelected(index)
                    },
                ) {
                    Body2Semibold(
                        text = stringResource(options[index]),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { onSelected(index) },
                ) {
                    Body2Semibold(text = stringResource(options[index]))
                }
            }

        }

    }
}

@Composable
internal fun ObserversContent(
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
internal fun UserContent(
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