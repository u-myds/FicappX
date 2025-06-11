package u.ficappx.ui.components.defined


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay


@Composable
fun SmoothAppearAfter(timeSeconds: Long, enterTime: Int = 400, exitTime: Int = 400, content: @Composable () -> Unit) {
    var timeElapsed by remember { mutableFloatStateOf(0.0f) }
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = appeared) {
        if (!appeared) {
            while (timeElapsed < timeSeconds) {
                delay(1000L)
                timeElapsed += 1f
            }
            appeared = true
        }
    }
    AnimatedVisibility(
        visible = appeared,
        enter = fadeIn(animationSpec = tween(enterTime)),
        exit = fadeOut(animationSpec = tween(exitTime))
    ) {
        content()
    }

}