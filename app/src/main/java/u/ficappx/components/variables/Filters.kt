package u.ficappx.components.variables

import u.ficappx.api.serialization.FandomSearch
import u.ficappx.api.serialization.TagSearch
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class Filters(
    var gen: MutableState<Boolean> = mutableStateOf(false),
    var get: MutableState<Boolean> = mutableStateOf(false),
    var slash: MutableState<Boolean> = mutableStateOf(false),
    var fslash: MutableState<Boolean> = mutableStateOf(false),
    var other: MutableState<Boolean> = mutableStateOf(false),
    var w: MutableState<Boolean> = mutableStateOf(false),
    var s: MutableState<Boolean> = mutableStateOf(false),
    var n: MutableState<Boolean> = mutableStateOf(false),

    var originals: MutableState<Boolean> = mutableStateOf(false),
    var fanfics: MutableState<Boolean> = mutableStateOf(false),

    var statusInProgress: MutableState<Boolean> = mutableStateOf(false),
    var statusFinished: MutableState<Boolean> = mutableStateOf(false),
    var statusFrozen: MutableState<Boolean> = mutableStateOf(false),

    var ratingG: MutableState<Boolean> = mutableStateOf(false),
    var ratingPG: MutableState<Boolean> = mutableStateOf(false),
    var ratingR: MutableState<Boolean> = mutableStateOf(false),
    var ratingNC17: MutableState<Boolean> = mutableStateOf(false),
    var ratingNC21: MutableState<Boolean> = mutableStateOf(false),

    var tags: MutableState<List<TagSearch>> = mutableStateOf(emptyList<TagSearch>()),
    var fandoms: MutableState<List<FandomSearch>> = mutableStateOf(emptyList<FandomSearch>()),
    var findInTransaled: MutableState<Boolean> = mutableStateOf(false),
)
