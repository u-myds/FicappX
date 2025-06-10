package u.ficappx.api.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PageData(
    val fanfics: List<Fanfic>,
    val currentPage: Int,
    val lastPage: Int,
    val nothingFound: Boolean
) : Parcelable
