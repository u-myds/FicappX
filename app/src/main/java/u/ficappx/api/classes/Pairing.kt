package u.ficappx.api.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Pairing(
    val name: String,
    val url: String
) : Parcelable
