package com.example.shopinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DetailScreen(item: String, onBack: () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { full -> full / 4 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { full -> full / 4 }) + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Detail Item", style = MaterialTheme.typography.titleLarge)
            Text(text = item, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 8.dp))

            Button(onClick = { onBack() }, modifier = Modifier.padding(top = 16.dp)) {
                Text(text = stringResource(R.string.kembali2))
            }
        }
    }
}
