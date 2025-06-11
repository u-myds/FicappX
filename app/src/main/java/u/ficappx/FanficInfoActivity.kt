package u.ficappx

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import u.ficappx.api.FicbookAPI
import u.ficappx.api.classes.Comment
import u.ficappx.api.classes.Fanfic
import u.ficappx.api.classes.Part
import u.ficappx.api.mobile.FicbookMobileAPI
import u.ficappx.ui.components.fragments.settings.Settings
import u.ficappx.ui.components.views.CommentView
import u.ficappx.ui.components.views.PartView
import u.ficappx.ui.theme.FicappXTheme

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
        val context = LocalContext.current
        val settings = Settings(context)
        var loaded by remember { mutableStateOf(false) }
        var parts by remember { mutableStateOf(listOf<Part>()) }

        var comments by remember { mutableStateOf(listOf<Comment>()) }
        LaunchedEffect(Unit) {
            if(settings.use_mobile_api.value == 1) {
                coroutineScope.launch(Dispatchers.IO) {
                    val partsMobile = FicbookMobileAPI().getParts(fanfic.url.split("/").last())?.data?.parts
                    val converted = mutableListOf<Part>()
                    if(partsMobile != null){
                        for(part in partsMobile){
                            converted.add(part.convertToPart())
                        }
                        parts = converted
                    }
                    loaded = true
                }
            }
            else{
                coroutineScope.launch(Dispatchers.IO) {
                    val partsE = FicbookAPI.fanficParts(fanfic.url)
                    if(partsE != null) parts = partsE
                    loaded = true
                }
            }
            coroutineScope.launch(Dispatchers.IO) {
                val t = FicbookAPI.Comments.get(fanfic)
                if (t != null){
                    comments = t
                }
            }

        }
        SelectionContainer {
            Box(modifier = Modifier.padding(p)){
                if(parts.isNotEmpty()){
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(parts) {
                            PartView(it)
                            Spacer(Modifier.size(0.dp, 4.dp))
                        }
                        item {
                            Column(modifier = Modifier.fillMaxSize()) {
                                //CommentBlock(comments)
                            }

                        }
                    }
                }
                else {
                    if(loaded){
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    PartView(
                                        Part("***", fanfic.url, "")
                                    )
                                    //CommentsBlock(comments)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun CommentBlock(comments: List<Comment>){
        Text("Отзывы", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        for (comment in comments){
            CommentView(comment)
            Spacer(Modifier.size(0.dp, 4.dp))
        }
    }

}

