package u.ficappx.api.serialization

import kotlinx.serialization.Serializable

@Serializable
data class SearchTagsResult(
    val result: Boolean,
    val data: TagData,
)

@Serializable
data class TagData(
    val more: Boolean,
    val total: Int,
    val tags: List<TagSearch>
)
@Serializable
data class TagSearch(
    val id: Int,
    val title: String,
    val description: String,
    val category: String
)