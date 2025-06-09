package u.ficappx

import u.ficappx.api.FicbookAPI
import u.ficappx.api.classes.Fanfic
import u.ficappx.api.classes.Part
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import u.ficappx.ui.theme.FicappXTheme
import u.ficappx.ui.components.views.PartView
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import u.ficappx.api.mobile.FicbookMobileAPI
import u.ficappx.api.mobile.PartMobile
import u.ficappx.ui.components.fragments.settings.Settings

class FanficInfoActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val fanfic = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        { intent.getParcelableExtra("fanfic", Fanfic::class.java) }
        else
        { intent.extras?.getParcelable("fanfic") }
        if (fanfic == null) finishActivity(1)

        setContent {
            FicappXTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    View(innerPadding, fanfic!!)
                }
            }
        }
    }
    @Composable
    fun View(p: PaddingValues, fanfic: Fanfic){
        val coroutineScope = rememberCoroutineScope()
        var context = LocalContext.current
        var settings = Settings(context)
        var parts by remember { mutableStateOf(listOf<Part>()) }
        var text by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            if(settings.use_mobile_api.value == 1) {
                coroutineScope.launch(Dispatchers.IO) {
                    var partsMobile = FicbookMobileAPI().getParts(fanfic.url.split("/").last())?.data?.parts
                    var converted = mutableListOf<Part>()
                    if(partsMobile != null){
                        for(part in partsMobile){
                            converted.add(part.convertToPart())
                        }
                        parts = converted
                    }
                }
            }
            else{
                coroutineScope.launch(Dispatchers.IO) {
                    val partsE = FicbookAPI.fanficParts(fanfic.url)
                    val textE = FicbookAPI.fanficText(fanfic.url)
                    if(partsE != null) parts = partsE
                    if(textE != null) text = textE
                }
            }

        }
        SelectionContainer {
            Box(modifier = Modifier.padding(p)){
                if(parts.isNotEmpty()){
                    LazyColumn {
                        items(parts) {
                            PartView(it)
                            Spacer(Modifier.size(0.dp, 4.dp))
                        }
                    }
                }
                if(text.isNotEmpty()){
                    Text(text, modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()))
                }
            }
        }

    }

}

