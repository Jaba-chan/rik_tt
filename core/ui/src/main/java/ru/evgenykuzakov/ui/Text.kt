package ru.evgenykuzakov.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import ru.evgenykuzakov.designsystem.theme.bodyMediumSemibold

@Composable
fun Body2Semibold(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMediumSemibold,
        color = color
    )
}

@Composable
fun H2Text(
    text: String,
    color: Color =  MaterialTheme.colorScheme.onBackground
) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayMedium,
        color = color
    )
}

@Composable
fun Footnot13Med(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface){
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color
    )
}