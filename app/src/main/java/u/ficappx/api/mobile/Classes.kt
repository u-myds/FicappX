package u.ficappx.api.mobile
import kotlinx.serialization.Serializable
import okio.ByteString.Companion.encodeUtf8
import u.ficappx.api.classes.Part

@Serializable
data class InstallRequestBody(
    val fid: String,
    val appId: String,
    val authVersion: String,
    val sdkVersion: String
)

@Serializable
data class InstallResponse(
    val fid: String,
    val authToken: AuthToken,
    val refreshToken: String
)

@Serializable
data class AuthToken(
    val token: String,
    val expiresIn: String
)

@Serializable
data class RemoteConfigRequestBody(
    val app_instance_id: String,
    val app_instance_id_token: String,
    val app_id: String,
    val sdk_version: String
)

@Serializable
data class RemoteConfigResponse(
    val state: String,
    val entries: Map<String, String> // Remote Config entries are usually string-to-string maps
)


@Serializable
data class PartMobile(
    val id: Int,
    var part_title: String
) {
    fun utf8(){
        part_title = part_title.encodeUtf8().utf8()
    }
    fun convertToPart(): Part {
        utf8()
        return Part(part_title, id.toString(), "")
    }
}

@Serializable
data class FanficMobile(
    val fanfic_title: String,
    val parts: List<PartMobile>
)

@Serializable
data class FanficRequest(
    val result: Boolean,
    val data: FanficMobile
)

@Serializable
data class PartTextRequest(
    val result: Boolean,
    val data: String
)