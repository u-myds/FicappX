package u.ficappx.ui.components.defined

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable

@Composable
fun AnimatedVisibilityFadeInOut(visible: Boolean, content: @Composable AnimatedVisibilityScope.() -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(250)),
        exit = fadeOut(animationSpec = tween(250)),
        content = content
    )
}