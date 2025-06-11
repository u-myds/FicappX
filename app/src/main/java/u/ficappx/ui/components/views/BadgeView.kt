package u.ficappx.ui.components.views



import u.ficappx.api.classes.Badge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import u.ficappx.R
import u.ficappx.api.classes.BadgeType

@Composable
fun BadgeView(badge: Badge){
    var isIcon by remember { mutableStateOf(false) }
    var badgeIconId by remember{ mutableIntStateOf(R.drawable.check_circle) }
    LaunchedEffect(Unit) {
        if(badge.type == BadgeType.STATUS) {
            when(badge.name){
                "Заморожен" -> {badgeIconId = R.drawable.pause; isIcon = true}
                "Завершён" -> {badgeIconId = R.drawable.check_circle; isIcon = true}
                "В процессе" -> {badgeIconId = R.drawable.hourglass; isIcon = true}
            }
        }
        if(badge.type == BadgeType.LIKES){
            badgeIconId = R.drawable.thumb_up
            isIcon = true
        }
        if(badge.type == BadgeType.TROPHY){
            badgeIconId = R.drawable.trophy
            isIcon = true
        }


    }
    Box(modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally).clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.primaryContainer)
        .alpha(0.8f)
    ) {
        FlowRow(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(2.dp, 2.dp)){
            if(isIcon){
                Icon(painterResource(badgeIconId), "")
            }
            Text(badge.name, fontSize = 12.sp, modifier = Modifier.padding(4.dp, 0.dp))
        }
    }
}