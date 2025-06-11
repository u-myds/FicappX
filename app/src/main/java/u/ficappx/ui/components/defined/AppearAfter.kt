package u.ficappx.ui.components.defined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun AppearAfter(timeSeconds: Long, content: @Composable () -> Unit) {
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
    if(appeared){
        content()
    }

}