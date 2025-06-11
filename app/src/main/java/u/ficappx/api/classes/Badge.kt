package u.ficappx.api.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

enum class BadgeType{
    DIRECTION,
    RATING,
    STATUS,
    LIKES,
    TROPHY
}


@Parcelize
@Serializable
data class Badge(
    val name: String,
    val type: BadgeType
) : Parcelable
