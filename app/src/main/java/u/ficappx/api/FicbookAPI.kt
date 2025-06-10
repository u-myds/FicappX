package u.ficappx.api

import u.ficappx.api.classes.Part
import u.ficappx.api.classes.PageData
import u.ficappx.api.serialization.FandomSearch

import u.ficappx.api.serialization.SearchFandomResult
import u.ficappx.api.serialization.SearchTagsResult
import u.ficappx.api.serialization.TagSearch
import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import u.ficappx.api.classes.AuthorInfo

const val SCHEME = "https"
const val HOST = "ficbook.net"
class FicbookAPI(private val client: OkHttpClient, private val headers: Headers) {
    fun fanfics(query: String, page: Int = 1, includes: List<Pair<String, String>>?= null): PageData?{
        val url = HttpUrl.Builder().scheme(SCHEME).host(HOST).addPathSegment("find-fanfics-846555")
            .addQueryParameter("title", query)
            .addQueryParameter("p", page.toString())
        if(includes != null){
            for(include in includes){
                url.addQueryParameter(include.first, include.second)
            }
        }
        val urlB = url.build()

        var response = client.newCall(
            Request.Builder().url(urlB).headers(headers).build()
        ).execute()

        if(!response.isSuccessful) return null
        val body = response.body!!.string()
        val pageData = Parser.Search.fullParse(body)
        Log.d("FicNet","Fics got!")
        return pageData

    }

    companion object{

        fun fanficText(fanficUrl: String): String? {

            val tClient = OkHttpClient.Builder().build()
            val url = HttpUrl.Builder().scheme(SCHEME).host(HOST).addPathSegments(fanficUrl
                .replaceFirst("/", "")
                .replace("?source=premium&premiumVisit=1", "")
            ).build()
            Log.d("FicRead",url.toString())
            val response = tClient.newCall(
                Request.Builder().url(url).build()
            ).execute()

            if(!response.isSuccessful) return null

            var body = response.body!!.string().trimIndent()


            val text = Parser.FanficPage.getText(body)
            if (text != null) {
                Log.d("FicRead", text.toString())
            }
            return text
        }

        fun fanficParts(fanficUrl: String): List<Part>?{

            val tClient = OkHttpClient.Builder().build()
            val url = HttpUrl.Builder().scheme(SCHEME).host(HOST).addPathSegments(fanficUrl
                .replaceFirst("/", "")
                .replace("?source=premium&premiumVisit=1", "")
            ).build()
            Log.d("FicRead",url.toString())
            val response = tClient.newCall(
                Request.Builder().url(url).build()
            ).execute()
            if(!response.isSuccessful) return null

            val body = response.body!!.string()
            val parts = Parser.FanficPage.getParts(body)

            return parts
        }
    }

    object TagApi{
        fun getByQuery(query: String, page: Int = 1, callback: (List<TagSearch>) -> (Unit)){
            val form = FormBody.Builder().add("title", query).add("page", page.toString()).build()


            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url("https://ficbook.net/tags/search").post(form).build()
            val response = client.newCall(request).execute()
            Log.d("FiCNet",response.isSuccessful.toString())
            val body = response.body!!.string()
            Log.d("FicNet", body)
            val js = Json { ignoreUnknownKeys = true }
            val responseParsed = js.decodeFromString<SearchTagsResult>(body)
            callback(responseParsed.data.tags)
        }
    }

    object FandomApi{
        fun getByQuery(query: String, page: Int = 1, callback: (List<FandomSearch>) -> (Unit)){
            val form = FormBody.Builder()
                .add("q", query)
                .add("page", page.toString())
                .add("show_universes", "true")
                    .build()


            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url("https://ficbook.net/fandoms/search").post(form).build()
            val response = client.newCall(request).execute()
            Log.d("FiCNet",response.isSuccessful.toString())
            val body = response.body!!.string()
            Log.d("FicNet", body)
            val js = Json { ignoreUnknownKeys = true }
            val responseParsed = js.decodeFromString<SearchFandomResult>(body)
            callback(responseParsed.data.result)
        }
    }

    object AuthorApi{
        fun fullInfo(url: String, page: Int = 1) : AuthorInfo?{
            val client = OkHttpClient.Builder().build()
            val urlBuilded = HttpUrl.Builder().scheme(SCHEME).host(HOST).addPathSegments(url.replaceFirst("/", ""))
                .addPathSegment("profile")
                .addPathSegment("works")
                .addQueryParameter("p", page.toString())
                .build()
            val request = Request.Builder().url(urlBuilded).build()
            val response = client.newCall(request).execute()
            if(!response.isSuccessful) {
                println("Null on Author on url: ${urlBuilded.toString()}")
                return null
            }
            val body = response.body!!.string()
            return Parser.AuthorPage.fullParse(body)
        }
    }

}