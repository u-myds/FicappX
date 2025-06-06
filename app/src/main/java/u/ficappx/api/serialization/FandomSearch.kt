package u.ficappx.api.serialization

import kotlinx.serialization.Serializable

@Serializable
data class SearchFandomResult(
    val result: Boolean,
    val data: FandomData,
)

@Serializable
data class FandomData(
    val more: Boolean,
    val result: List<FandomSearch>
)
@Serializable
data class FandomSearch(
    val id: Int,
    val title: String,
)