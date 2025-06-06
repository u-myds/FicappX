package u.ficappx.ui.components.fragments.saves

import u.ficappx.api.classes.Fanfic
import u.ficappx.components.db.DBHelper
import u.ficappx.ui.components.views.FanficView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavesFragment(p: PaddingValues){
    val db = DBHelper(LocalContext.current)



    var needUpdate by remember { mutableStateOf(true) }
    var fanfics by remember { mutableStateOf(listOf<Fanfic>()) }

    LaunchedEffect(needUpdate) {
        if (needUpdate){
            fanfics = db.getAllFanfics().reversed()
            needUpdate = false
        }
    }
    PullToRefreshBox(needUpdate, {needUpdate = true}) {
        Box(modifier = Modifier.padding(p)){
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(fanfics) { fanfic ->
                    FanficView(fanfic, onClickTag = {})
                }
            }
        }
    }


}