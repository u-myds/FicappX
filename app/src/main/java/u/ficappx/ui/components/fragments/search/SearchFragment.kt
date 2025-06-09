package u.ficappx.ui.components.fragments.search

import u.ficappx.api.FicbookAPI
import u.ficappx.api.classes.Fanfic
import u.ficappx.api.classes.Tag
import u.ficappx.components.db.DBHelper
import u.ficappx.components.fragments.SearchFragmentSaver
import u.ficappx.ui.components.defined.AnimatedVisibilityFadeInOut
import u.ficappx.ui.components.views.FanficView
import u.ficappx.ui.components.views.Filters
import u.ficappx.ui.components.views.Pagination
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import u.ficappx.api.mobile.FicbookMobileAPI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFragment(ficbookAPI: FicbookAPI, searchSaver: SearchFragmentSaver, p: PaddingValues, mobileAPI: FicbookMobileAPI? = null) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }

    val fanfics = remember { mutableStateListOf<Fanfic>() }
    var page by remember { mutableIntStateOf(1) }
    var lastPage by remember { mutableIntStateOf(1) }
    var nothingFound by remember { mutableStateOf(false) }

    var state by remember { mutableStateOf(SearchState.READY) }
    // var searchType by remember { mutableStateOf(SearchType.QUERY) }
    var lastTag by remember { mutableStateOf<Tag?>(null) }

    var sheetState = rememberModalBottomSheetState()
    var isFilterVisible = remember { mutableStateOf(false) }

    val db = DBHelper(LocalContext.current)

    val lazyState = rememberLazyListState()
    val performSearch: (String, Int) -> Unit = { newQuery, newPage ->
        state = SearchState.LOADING
        coroutineScope.launch { lazyState.animateScrollToItem(0) }
        coroutineScope.launch(Dispatchers.IO) {
                var j: MutableList<Pair<String, String>>? = null
                if (lastTag != null){
                    j = mutableListOf(Pair("tags_include[]", lastTag?.url!!.split("/").last()))
                }
                val final = mutableListOf<Pair<String, String>>()
                final.addAll(searchSaver.filtersSaver.getAsPairs())
                if (j != null) {
                    final.addAll(j)
                }
                val parsed = ficbookAPI.fanfics(newQuery, newPage, final)
                if (parsed != null) {
                    fanfics.clear()
                    fanfics.addAll(parsed.fanfics)
                    lastPage = parsed.lastPage
                    nothingFound = parsed.nothingFound
                    searchSaver.fanfics(parsed.fanfics)
                    searchSaver.query(newQuery)
                    searchSaver.page(newPage)
                }
                state = SearchState.READY

        }
    }

    val searchOnClick: () -> Unit = {
        focusManager.clearFocus()
        coroutineScope.launch(Dispatchers.IO) {
            nothingFound = false
            lastTag = null
            page = 1
            performSearch(query, page)
        }
    }


    if(searchSaver.isFirstTimeCreated){
        performSearch("", 1)
        searchSaver.isFirstTimeCreated = false
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            mobileAPI?.generate()
        }
        if (searchSaver.fanfics.isNotEmpty()) fanfics.addAll(searchSaver.fanfics)
        if (searchSaver.page != 1) page = searchSaver.page
        if (searchSaver.filterState != null) {
            sheetState = searchSaver.filterState!!
        }

    }

    Column(modifier = Modifier.fillMaxSize().padding(bottom = p.calculateBottomPadding(), top = 0.dp, start = 0.dp)) {
        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp).clip(RoundedCornerShape(16.dp))) {
            SearchBar(
                inputField = { SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        searchOnClick()
                    },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { isFilterVisible.value = true }) {
                                Icon(Icons.AutoMirrored.Filled.List, "")
                            }
                            IconButton(onClick = { searchOnClick() }) {
                                Icon(Icons.Default.Search, "")
                            }

                        }
                    },
                    expanded = false,
                    onExpandedChange = { } ,
                    placeholder = {Text("Поиск", fontWeight = FontWeight.Bold)}
                ) },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier.fillMaxWidth(0.95f).clip(RoundedCornerShape(16.dp)),
                content = {}
            )

        }
        Box(modifier = Modifier.fillMaxWidth().weight(1f)){
            AnimatedVisibilityFadeInOut(visible = state == SearchState.READY) {
                Column {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()){
                        if(nothingFound) {
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Ничего не нашли :(", fontSize = 32.sp)
                                Text("Что-то другое?", fontSize = 24.sp)
                            }

                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), state = lazyState) {
                        items(fanfics) { fanfic ->
                            FanficView(fanfic) { tag ->
                                lastTag = tag
                                page = 1
                                performSearch(query, page)
                            }
                        }
                        item(key = "pagination_control") {
                            Pagination(
                                page, lastPage,
                                {
                                    page = it
                                    performSearch(query, page)
                                },
                                modifier = Modifier,
                            )
                        }
                    }

                }

            }
            AnimatedVisibilityFadeInOut(visible = state == SearchState.LOADING) {
                Box(modifier = Modifier.fillMaxSize()){
                    Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

    }

    if(isFilterVisible.value) {
        Filters(isFilterVisible, sheetState, searchSaver) {
            page = 1
            performSearch(query, page)
        }
    }
}