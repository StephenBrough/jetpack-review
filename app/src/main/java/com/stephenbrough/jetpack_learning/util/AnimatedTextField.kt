package com.stephenbrough.jetpack_learning.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedTextField(
    modifier: Modifier = Modifier,
    errorMessage: String?,
    animationDuration: Int = 400,
) {

    // Manually handle some of the state here so that when we clear the error state
    // the error text doesn't instantly disappear
    var isVisible by remember { mutableStateOf(false) }
    var currentMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(errorMessage) {
        when {
            !errorMessage.isNullOrBlank() -> {
                isVisible = true
                currentMessage = errorMessage
            }

            !isVisible -> {
                isVisible = false
                delay(animationDuration.toLong())
                currentMessage = null
            }
        }
    }

    Box(
        modifier = Modifier.height(16.dp)
    ) {
        AnimatedVisibility(
            visible = !errorMessage.isNullOrBlank(),
            enter = slideInVertically(
                animationSpec = tween(animationDuration)
            ) + fadeIn(tween(animationDuration)),
            exit = slideOutVertically(
                animationSpec = tween(animationDuration)
            ) + fadeOut(tween(animationDuration)),
        ) {
            Text(
                text = currentMessage ?: "",
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }
    }

}
