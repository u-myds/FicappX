package u.ficappx.api.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Badge(
    val name: String,
    val icon: String
) : Parcelable
