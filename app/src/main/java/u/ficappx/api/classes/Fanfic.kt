package u.ficappx.api.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Fanfic(
    val name: String,
    val author: Author,
    val tags: List<Tag>,
    val badges: List<Badge>,
    val url: String,
    val shortDescription: String,
    val fandoms: List<Fandom>
) : Parcelable
