package u.ficappx.ui.components.views

import u.ficappx.api.classes.Tag
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagView(tag: Tag, onClickTag: (Tag) -> (Unit)){
    Box(modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally).clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .alpha(0.8f)
        .clickable(
            onClick = {onClickTag(tag)}
        )

        ) {
        Text(tag.name, fontSize = 12.sp, modifier = Modifier.padding(4.dp, 2.dp))
    }
}