package u.ficappx.components.fragments

import u.ficappx.api.classes.Fanfic
import u.ficappx.api.classes.Tag
import u.ficappx.components.variables.FiltersSaver
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState

class SearchFragmentSaver {
    var isFirstTimeCreated = true

    var fanfics: MutableList<Fanfic> = mutableListOf()
    var query: String = ""
    var tag: Tag = Tag("AU", "/tags/1683")
    var page: Int = 1

    var filtersSaver = FiltersSaver()
    @OptIn(ExperimentalMaterial3Api::class)
    var filterState: SheetState? = null

    fun fanfics(new: Collection<Fanfic>){
        fanfics.clear()
        fanfics.addAll(new)
    }

    fun query(new: String){
        query = new
    }

    fun page(new: Int){
        page = new
    }

    fun tag(new: Tag){
        tag = new
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun filterState(newState: SheetState){
        filterState = newState
    }

    fun filterSaver(newFiltersSaver: FiltersSaver){
        filtersSaver = newFiltersSaver
    }

}