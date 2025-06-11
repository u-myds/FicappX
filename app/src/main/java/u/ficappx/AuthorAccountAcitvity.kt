package u.ficappx

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import u.ficappx.api.FicbookAPI
import u.ficappx.api.classes.Author
import u.ficappx.api.classes.AuthorInfo
import u.ficappx.ui.theme.FicappXTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import u.ficappx.ui.components.defined.AnimatedVisibilityFadeInOut
import u.ficappx.ui.components.fragments.search.SearchState
import u.ficappx.ui.components.views.FanficView
import u.ficappx.ui.components.views.Pagination

class AuthorAccountAcitvity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val author = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        { intent.getParcelableExtra("author", Author::class.java) }
        else
        { intent.extras?.getParcelable("author") }
        if (author == null) finishActivity(1)
        setContent {
            FicappXTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthorView(author!!, innerPadding)
                }
            }
        }
    }

    @Composable
    fun AuthorView(author: Author, p: PaddingValues){
        val coroutineScope = rememberCoroutineScope()
        var readyData by remember { mutableStateOf<AuthorInfo?>(null) }
        var page by remember { mutableIntStateOf(1) }
        var lastPage by remember { mutableIntStateOf(1) }
        var state by remember { mutableStateOf(SearchState.LOADING) }
        val lazyState = rememberLazyListState()
        val performSearch: (String, Int) -> Unit = { url, pagenew ->
            coroutineScope.launch { lazyState.animateScrollToItem(0) }
            coroutineScope.launch(Dispatchers.IO) {

                readyData = FicbookAPI.AuthorApi.fullInfo(url, pagenew)
                if(readyData != null){
                    lastPage = readyData!!.pageData.lastPage
                    page = readyData!!.pageData.currentPage
                }
                state = SearchState.READY

            }
        }
        LaunchedEffect(Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                readyData = FicbookAPI.AuthorApi.fullInfo(author.url, page)
                if(readyData != null){
                    lastPage = readyData!!.pageData.lastPage
                    page = readyData!!.pageData.currentPage

                }
                state = SearchState.READY
            }
        }
        AnimatedVisibilityFadeInOut(state == SearchState.LOADING) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.size(0.dp, 8.dp))

                }

            }
        }
        AnimatedVisibilityFadeInOut(state == SearchState.READY) {
            Box(modifier = Modifier.padding(p)){
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), state = lazyState) {
                    if(readyData != null){
                        item("AuthorImage") {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {

                                SubcomposeAsyncImage(model = ImageRequest.Builder(LocalContext.current)
                                    .data(readyData!!.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                    contentDescription = null,
                                    loading = { CircularProgressIndicator() },
                                    onError = {  },
                                    onSuccess = {  },
                                    modifier = Modifier.size(100.dp, 100.dp).clip(CircleShape))
                                Spacer(Modifier.size(16.dp, 0.dp))
                                Text(readyData!!.name, fontSize = 24.sp, style = MaterialTheme.typography.bodyMedium)

                            }
                        }
                        items(readyData!!.pageData.fanfics) {
                            FanficView(it){}
                        }
                        item(key = "pagination_control") {
                            Pagination(
                                page, lastPage,
                                {
                                    state = SearchState.LOADING
                                    page = it
                                    performSearch(author.url, page)
                                },
                                modifier = Modifier,
                            )
                        }
                    }
                }
            }
        }



    }
}
