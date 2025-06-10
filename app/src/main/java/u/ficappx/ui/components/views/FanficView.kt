package u.ficappx.ui.components.views

import u.ficappx.FanficInfoActivity

import u.ficappx.ReadActivity
import u.ficappx.api.FicbookAPI
import u.ficappx.api.classes.Fanfic
import u.ficappx.api.classes.Tag
import u.ficappx.components.db.DBHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import u.ficappx.AuthorAccountAcitvity
import u.ficappx.R
import u.ficappx.api.mobile.FicbookMobileAPI
import u.ficappx.ui.components.fragments.settings.Settings


@Composable
fun FanficView(fanfic: Fanfic,onClickTag: (Tag) -> (Unit)){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = DBHelper(context)
    val settings = Settings(context)
    var isSaved by remember { mutableStateOf(db.exists(fanfic)) }
    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.secondaryContainer).fillMaxWidth().padding(4.dp, 4.dp)
            .clickable {
                coroutineScope.launch(Dispatchers.IO) {
                    if(settings.use_mobile_api.value == 1){
                        val fanficInfo = Intent(context, FanficInfoActivity::class.java)
                        val bundle = Bundle().apply { putParcelable("fanfic",fanfic)}
                        fanficInfo.putExtras(bundle)
                        context.startActivity(fanficInfo)
                    }
                    else{
                        if (FicbookAPI.fanficText(fanfic.url) != null){
                            val readActivity = Intent(context, ReadActivity::class.java)
                            val bundle = Bundle().apply { putString("url", fanfic.url)}
                            readActivity.putExtras(bundle)
                            context.startActivity(readActivity)
                        }
                        else{
                            val fanficInfo = Intent(context, FanficInfoActivity::class.java)
                            val bundle = Bundle().apply { putParcelable("fanfic",fanfic)}
                            fanficInfo.putExtras(bundle)
                            context.startActivity(fanficInfo)
                        }
                    }

                }


            }
    ){
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 0.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    for(b in fanfic.badges) {
                        if(!b.name.isDigitsOnly()) BadgeView(b)
                    }
                }
                Row(horizontalArrangement = Arrangement.End){
                        IconButton(onClick = {db.putOrDelete(fanfic); isSaved = db.exists(fanfic)}) {
                            Icon(painterResource(if(isSaved){R.drawable.bookmark_filled} else {R.drawable.bookmark}), "")
                        }
                }
            }

            Text(fanfic.name, style = MaterialTheme.typography.titleLarge)
            Text("Фэндом: ${fanfic.fandoms.joinToString { fandom -> fandom.name }}", style = MaterialTheme.typography.bodyMedium)
            Text("Aвтор: ${fanfic.author.name}", modifier = Modifier.clickable {
                val intent = Intent(context, AuthorAccountAcitvity::class.java)
                val bundle = Bundle().apply { putParcelable("author",fanfic.author)}
                intent.putExtras(bundle)
                context.startActivity(intent)
            }, style = MaterialTheme.typography.bodyLarge)
            FlowRow(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                fanfic.tags.forEach{ tag ->
                    TagView(tag) {
                        //Toast.makeText(context, "Смотрим на ${tag.name}", Toast.LENGTH_LONG).show()
                        onClickTag(it)
                    }
                }
            }




        }


    }
}