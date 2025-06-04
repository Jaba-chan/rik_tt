package ru.evgenykuzakov.rik_tt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.evgenykuzakov.designsystem.theme.Rik_ttTheme
import ru.evgenykuzakov.designsystem.theme.bodyMediumSemibold
import ru.evgenykuzakov.statistic.StatisticScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Rik_ttTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                   StatisticScreen(
                       paddingValues = innerPadding
                   )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    Button(
        onClick = {},
    ) {
        Text(
            text = stringResource(R.string.by_days),
            style = MaterialTheme.typography.bodyMediumSemibold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Rik_ttTheme {
        Greeting("Android")
    }
}

