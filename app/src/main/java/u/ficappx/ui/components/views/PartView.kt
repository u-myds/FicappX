package u.ficappx.ui.components.views

import u.ficappx.ReadActivity
import u.ficappx.api.classes.Part
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PartView(part: Part){
    val context = LocalContext.current
    Box(modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.secondaryContainer).fillMaxWidth().clickable {
        val fanficInfo = Intent(context, ReadActivity::class.java)
        val bundle = Bundle().apply { putString("url", part.url) }
        fanficInfo.putExtras(bundle)
        context.startActivity(fanficInfo)
    }){
        Column(modifier = Modifier.padding(4.dp, 4.dp)) {
            Text(part.name, style = MaterialTheme.typography.titleLarge)
            Text(part.postDate, style = MaterialTheme.typography.bodySmall)
        }

    }
}