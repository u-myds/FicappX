package u.ficappx.components.variables

class FiltersSaver {
    var filters = Filters()
    fun getAsPairs(): MutableList<Pair<String, String>> {
        val d = mutableListOf<Pair<String, String>>()
        if (filters.gen.value) d.add(Pair("directions[]", "1"))
        if (filters.get.value) d.add(Pair("directions[]", "2"))
        if (filters.slash.value) d.add(Pair("directions[]", "3"))
        if (filters.fslash.value) d.add(Pair("directions[]", "4"))
        if (filters.other.value) d.add(Pair("directions[]", "7"))
        if (filters.w.value) d.add(Pair("directions[]", "6"))
        if (filters.s.value) d.add(Pair("directions[]", "5"))
        if (filters.n.value) d.add(Pair("directions[]", "8"))

        if(filters.originals.value) d.add(Pair("work_types[]", "originals"))
        if(filters.fanfics.value) d.add(Pair("work_types[]", "fandom"))

        if(filters.statusInProgress.value) d.add(Pair("statuses[]", "1"))
        if(filters.statusFinished.value) d.add(Pair("statuses[]", "2"))
        if(filters.statusFrozen.value) d.add(Pair("statuses[]", "3"))

        if(filters.ratingG.value) d.add(Pair("ratings[]", "5"))
        if(filters.ratingPG.value) d.add(Pair("ratings[]", "6"))
        if(filters.ratingR.value) d.add(Pair("ratings[]", "7"))
        if(filters.ratingNC17.value) d.add(Pair("ratings[]", "8"))
        if(filters.ratingNC21.value) d.add(Pair("ratings[]", "9"))

        for(tag in filters.tags.value){
            d.add(Pair("tags_include[]", tag.id.toString()))
        }
        for(fandom in filters.fandoms.value) {
            d.add(Pair("fandom_ids[]", fandom.id.toString()))
        }

        d.add(Pair("tags_search_options", "1"))
        d.add(Pair("fandoms_search_options", "2"))
        return d
    }

    fun set(filtersNew: Filters){
        filters = filtersNew
    }




}