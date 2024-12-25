
package app.olauncher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import app.olauncher.MainViewModel
import androidx.compose.runtime.Composable
import app.olauncher.MainViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.olauncher.MainViewModel

@Composable
fun HomeView(viewModel: MainViewModel, appNames: List<String> = listOf(), onClick: (Int) -> Unit = {}, onLongClick: (Int) -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.Button
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

for (i in appNames.indices) {
                Text(
                    text = appNames[i],
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = { onClick(i) },
                            onLongClick = { onLongClick(i) }
                        )
                        .padding(8.dp),
                    textAlign = TextAlign.Center

                )
            }

        }
    }
}
