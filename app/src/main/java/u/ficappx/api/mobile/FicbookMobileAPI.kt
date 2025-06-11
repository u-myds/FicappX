package u.ficappx.api.mobile

import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.SecureRandom
import java.util.Base64

class FicbookMobileAPI {
    private var xkey = ""
    private val userAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36 ao2gvvnhkv0t22lo"
    private val apiKey = "AIzaSyBTmb0GKTjIOqHdFjR0G0RYjFkvJjjJWE0"
    private val projectID = "ficbook-app"
    private val appID = "1:743203295740:android:74d2660d761dfc70a16279"
    private val installUrl = "https://firebaseinstallations.googleapis.com/v1/projects/$projectID/installations"
    private val fetchUrl = "https://firebaseremoteconfig.googleapis.com/v1/projects/$projectID/namespaces/firebase:fetch"
    private val client = OkHttpClient.Builder().build()
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()



    private fun generateXKeyHeader(apiMobile: String): String {
        val unixTimestamp = Clock.System.now().epochSeconds
        val apiKeyNumber = apiMobile.toLongOrNull()
            ?: throw IllegalArgumentException("apiMobileKey is not a valid number.")

        val xorResult = unixTimestamp xor apiKeyNumber
        return xorResult.toString(16)
    }

    private fun generateFID() : String{
        val random = ByteArray(17)
        SecureRandom().nextBytes(random)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random)
    }

    private fun getInstall(fid: String): InstallResponse? {
        val installBody = InstallRequestBody(
            fid = fid,
            appId = appID,
            authVersion = "FIS_v2",
            sdkVersion = "a:17.0.1"
        )
        val installRequestBodyString = json.encodeToString(installBody)

            val installRequestBody = installRequestBodyString.toRequestBody(JSON_MEDIA_TYPE)


        val installRequest = Request.Builder()
            .url(installUrl)
            .addHeader("x-goog-api-key", apiKey)
            .addHeader("User-Agent", userAgent)
            .addHeader("Content-Type", "application/json")
            .post(installRequestBody)
            .build()

        val installResponse = client.newCall(installRequest).execute()
        if(!installResponse.isSuccessful) return null
        val installResponseObject = json.decodeFromString<InstallResponse>(installResponse.body!!.string())
        return installResponseObject
    }

    private fun getRemote(fid: String, authToken: String): String?{
        val remoteConfigBody = RemoteConfigRequestBody(
            app_instance_id = fid,
            app_instance_id_token = authToken,
            app_id = appID,
            sdk_version = "21.1.1"
        )
        val remoteConfigRequestBodyJson = json.encodeToString(remoteConfigBody).toRequestBody(JSON_MEDIA_TYPE)
        val remoteConfigRequest = Request.Builder()
            .url(fetchUrl)
            .addHeader("Authorization", "FIS_v2 $authToken")
            .addHeader("x-goog-api-key", apiKey)
            .addHeader("User-Agent", userAgent)
            .post(remoteConfigRequestBodyJson)
            .build()
        val remoteConfigResponse = client.newCall(remoteConfigRequest).execute()
        if (!remoteConfigResponse.isSuccessful) return null
        val configData = json.decodeFromString<RemoteConfigResponse>(remoteConfigResponse.body!!.string())
        return configData.entries["apiMobileKey"]
    }

    fun generate(){
        Clock.System.now().epochSeconds
        val fid = generateFID()
        val installResponseObject = getInstall(fid)

        val authToken = installResponseObject?.authToken?.token ?: return
        val apiMobile = getRemote(fid, authToken) ?: return

        xkey = generateXKeyHeader(apiMobile)
    }

    fun getParts(slug: String): FanficRequest? {
        generate()
        if(xkey == "") return null

        val url = "https://fanficlets.xyz/api_mobile/fanfic_parts"
        val body = """{"fanfic_slug": "${slug.replace("?source=premium&premiumVisit=1", "")}"}
        """.trimIndent()

        val partsRequest = Request.Builder()
            .url(url)
            .addHeader("User-Agent", userAgent)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Xkey", xkey)
            .post(body.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        val response = client.newCall(partsRequest).execute()
        val responsebody = response.body!!.string()

        return json.decodeFromString<FanficRequest>(responsebody)
    }

    fun getText(partID: String): PartTextRequest? {
        generate()

        if(xkey == "") return null
        val url = "https://fanficlets.xyz/api_mobile/fanfic_part_text"
        val body = """{
            "part_id": $partID
            }
        """.trimIndent()

        val partsRequest = Request.Builder()
            .url(url)
            .addHeader("User-Agent", userAgent)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Xkey", xkey)
            .post(body.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        val response = client.newCall(partsRequest).execute()
        val responsebody = response.body!!.string()

        return json.decodeFromString<PartTextRequest>(responsebody)
    }
}