package u.ficappx.components.web

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.concurrent.ConcurrentHashMap

class CookieJarC : CookieJar {
    private val cookiesByHost = ConcurrentHashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val hostCookies = cookiesByHost.computeIfAbsent(url.host) { mutableListOf() }
        hostCookies.addAll(cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val hostCookies = cookiesByHost[url.host] ?: return emptyList()
        return hostCookies
    }

    fun addWebViewCookies(urlString: String, cookiesString: String?) {
        if (cookiesString.isNullOrEmpty()) {
            return
        }

        val httpUrl = urlString.toHttpUrlOrNull() ?: return

        val domain = httpUrl.host
        val path = httpUrl.encodedPath

        val cookiesToAdd = ArrayList<Cookie>()


        cookiesString.split(";").forEach { cookiePair ->
            val parts = cookiePair.trim().split("=", limit = 2)
            if (parts.size == 2) {
                val name = parts[0]
                val value = parts[1]

                val cookie = Cookie.Builder()
                    .name(name)
                    .value(value)
                    .domain(domain)
                    .path(path)

                    .expiresAt(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 365L * 10L)
                    .build()

                cookiesToAdd.add(cookie)
            }
        }


        if (cookiesToAdd.isNotEmpty()) {
            val domainCookies = cookiesByHost.getOrPut(domain) { ArrayList() }
            cookiesToAdd.forEach { newCookie ->
                domainCookies.removeIf { it.name == newCookie.name }
                domainCookies.add(newCookie)
            }

        }
    }
}
