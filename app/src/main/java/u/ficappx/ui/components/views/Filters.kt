package u.ficappx.ui.components.views

import u.ficappx.api.FicbookAPI
import u.ficappx.api.serialization.FandomSearch
import u.ficappx.api.serialization.TagSearch
import u.ficappx.components.fragments.SearchFragmentSaver
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filters(visible: MutableState<Boolean>, state: SheetState, searchSaver: SearchFragmentSaver, callback: () -> (Unit)){
    val coroutineScope = rememberCoroutineScope()

    val filters by remember { mutableStateOf(searchSaver.filtersSaver.filters) }

    val byDirection: List<@Composable () -> Unit> = listOf(
        { FilterChip(filters.gen.value, { filters.gen.value = !filters.gen.value }, label = { Text("Джен") }) },
        { FilterChip(filters.get.value, { filters.get.value = !filters.get.value }, label = { Text("Гет") }) },
        { FilterChip(filters.slash.value, { filters.slash.value = !filters.slash.value }, label = { Text("Слэш") }) },
        { FilterChip(filters.fslash.value, { filters.fslash.value = !filters.fslash.value }, label = { Text("Фемслэш") })},
        { FilterChip(filters.other.value, { filters.other.value = !filters.other.value }, label = { Text("Другое") }) },
        { FilterChip(filters.w.value, { filters.w.value = !filters.w.value }, label = { Text("Смешанная") }) },
        { FilterChip(filters.s.value, { filters.s.value = !filters.s.value }, label = { Text("Статья") }) },
        { FilterChip(filters.n.value, { filters.n.value = !filters.n.value }, label = { Text("Не определено") }) }
    )

    val byState: List<@Composable () -> Unit> = listOf(
        { FilterChip(filters.statusFrozen.value, {filters.statusFrozen.value = !filters.statusFrozen.value}, label = { Text("Заморожен") }) },
        { FilterChip(filters.statusInProgress.value, {filters.statusInProgress.value = !filters.statusInProgress.value}, label = { Text("В процессе") }) },
        { FilterChip(filters.statusFinished.value, {filters.statusFinished.value = !filters.statusFinished.value}, label = { Text("Завершён") }) },
        )

    var byRating: List<@Composable () -> Unit> = listOf(
        { FilterChip(filters.ratingG.value, {filters.ratingG.value = !filters.ratingG.value}, label = { Text("G") }) },
        { FilterChip(filters.ratingPG.value, {filters.ratingPG.value = !filters.ratingPG.value}, label = { Text("PG-13") }) },
        { FilterChip(filters.ratingR.value, {filters.ratingR.value = !filters.ratingR.value}, label = { Text("R") }) },
        { FilterChip(filters.ratingNC17.value, {filters.ratingNC17.value = !filters.ratingNC17.value}, label = { Text("NC-17") }) },
        { FilterChip(filters.ratingNC21.value, {filters.ratingNC21.value = !filters.ratingNC21.value}, label = { Text("NC-21") }) },
    )

    var tagGotten = remember { mutableStateOf(listOf<TagSearch>()) }
    var queryTag by remember { mutableStateOf("") }
    var pageTag by remember { mutableIntStateOf(1) }

    var fandomGotted = remember { mutableStateOf(listOf<FandomSearch>()) }
    var queryFandom by remember { mutableStateOf("") }
    var pageFandom by remember { mutableIntStateOf(1) }

    val lazyListStateTags = rememberLazyListState()
    val isScrolledToEndTags by remember {
        derivedStateOf {
            val lastVisibleItem = lazyListStateTags.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index == lazyListStateTags.layoutInfo.totalItemsCount - 1 &&
                    lastVisibleItem.offset + lastVisibleItem.size <= lazyListStateTags.layoutInfo.viewportEndOffset
        }
    }

    val lazyListStateFandoms = rememberLazyListState()
    val isScrolledToEndFandoms by remember {
        derivedStateOf {
            val lastVisibleItem = lazyListStateFandoms.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index == lazyListStateFandoms.layoutInfo.totalItemsCount - 1 &&
                    lastVisibleItem.offset + lastVisibleItem.size <= lazyListStateFandoms.layoutInfo.viewportEndOffset
        }
    }

    LaunchedEffect(isScrolledToEndTags) {
        if(isScrolledToEndTags) {
            pageTag += 1
            coroutineScope.launch(Dispatchers.IO) {
                Log.d("FicNet", "called onValueChange")
                FicbookAPI.TagApi.getByQuery(queryTag, pageTag ,callback = { tags ->
                    tagGotten.value = tagGotten.value.plus(tags)
                }
                )
            }
        }
    }

    LaunchedEffect(isScrolledToEndFandoms) {
        if(isScrolledToEndFandoms) {
            pageFandom += 1
            coroutineScope.launch(Dispatchers.IO) {
                Log.d("FicNet", "called onValueChange")
                FicbookAPI.FandomApi.getByQuery(queryFandom, pageFandom ,callback = { tags ->
                    fandomGotted.value = fandomGotted.value.plus(tags)
                }
                )
            }
        }
    }
    ModalBottomSheet(
        onDismissRequest = {callback();visible.value = false},
        sheetState = state,
    ) {
        Column(Modifier.fillMaxSize().imePadding().verticalScroll(
            rememberScrollState()
        )) {
            Box(){
                Column {
                    Text("Метки")
                    if (filters.tags.value.isNotEmpty()){
                        LazyRow(verticalAlignment = Alignment.CenterVertically) {
                            item{
                                Text("Выбраны")
                                Spacer(Modifier.size(8.dp, 0.dp))
                            }
                            items(filters.tags.value){ tag ->
                                FilterChip(selected = true,
                                    onClick = {
                                        filters.tags.value = filters.tags.value.minus(tag)
                                    },
                                    label = {Text(tag.title)}
                                )
                                Spacer(Modifier.size(4.dp, 0.dp))
                            }
                        }
                    }

                    Box(){
                        OutlinedTextField(queryTag,
                            {
                                queryTag = it;
                                coroutineScope.launch(Dispatchers.IO) {
                                    Log.d("FicNet", "called onValueChange")
                                    FicbookAPI.TagApi.getByQuery(queryTag, pageTag,callback = { tags ->
                                        tagGotten.value = tags
                                    }
                                    )
                                }
                            },
                            placeholder = { Text("Введите для поиска") }
                        )
                    }

                    if(tagGotten.value.isNotEmpty()){
                        LazyRow(state = lazyListStateTags) {
                            items(tagGotten.value){ tag ->
                                FilterChip(
                                    selected = tag in filters.tags.value,
                                    label = { Text(tag.title) },
                                    onClick = {
                                        if (tag in filters.tags.value){
                                            filters.tags.value = filters.tags.value.minus(tag)
                                        }
                                        else{
                                            filters.tags.value = filters.tags.value.plus(tag)
                                        }
                                    }
                                )
                                Spacer(Modifier.size(4.dp, 0.dp))
                            }
                        }
                    }
                }

            }

            Box(){
                Column {
                    Text("Фандомы")
                    if (filters.fandoms.value.isNotEmpty()){
                        LazyRow(verticalAlignment = Alignment.CenterVertically) {
                            item{
                                Text("Выбраны")
                                Spacer(Modifier.size(8.dp, 0.dp))
                            }
                            items(filters.fandoms.value){ fandom ->
                                FilterChip(selected = true,
                                    onClick = {
                                        filters.fandoms.value = filters.fandoms.value.minus(fandom)
                                    },
                                    label = {Text(fandom.title)}
                                )
                                Spacer(Modifier.size(4.dp, 0.dp))
                            }
                        }
                    }

                    Box(){
                        OutlinedTextField(queryFandom,
                            {
                                queryFandom = it;
                                coroutineScope.launch(Dispatchers.IO) {
                                    Log.d("FicNet", "called onValueChange")
                                    FicbookAPI.FandomApi.getByQuery(queryFandom, pageFandom, callback = { fandoms ->
                                        fandomGotted.value = fandoms
                                    }
                                    )
                                }
                            },
                            placeholder = { Text("Введите для поиска") }
                        )
                    }

                    if(fandomGotted.value.isNotEmpty()){
                        LazyRow(state = lazyListStateFandoms) {
                            items(fandomGotted.value){ fandom ->
                                FilterChip(
                                    selected = fandom in filters.fandoms.value,
                                    label = { Text(fandom.title) },
                                    onClick = {
                                        if (fandom in filters.fandoms.value){
                                            filters.fandoms.value = filters.fandoms.value.minus(fandom)
                                        }
                                        else{
                                            filters.fandoms.value = filters.fandoms.value.plus(fandom)
                                        }
                                    }
                                )
                                Spacer(Modifier.size(4.dp, 0.dp))
                            }
                        }
                    }
                }

            }



            Text("Направление")
            LazyRow {
                items(byDirection){ filterDirection ->
                    filterDirection()
                    Spacer(Modifier.size(4.dp, 0.dp))
                }
            }
            Text("Статус")
            LazyRow {
                items(byState) { filterState ->
                    filterState()
                    Spacer(Modifier.size(4.dp, 0.dp))
                }
            }
            Text("Рейтинг")
            LazyRow {
                items(byRating) { filterRating ->
                    filterRating()
                    Spacer(Modifier.size(4.dp))
                }
            }


        }
    }
}