package u.ficappx.api

import u.ficappx.api.classes.Author
import u.ficappx.api.classes.Badge
import u.ficappx.api.classes.Fandom
import u.ficappx.api.classes.Fanfic
import u.ficappx.api.classes.Part
import u.ficappx.api.classes.PageData
import u.ficappx.api.classes.Tag
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.safety.Safelist
import u.ficappx.api.classes.AuthorInfo
import u.ficappx.api.classes.BadgeType
import u.ficappx.api.classes.Comment

const val DEFAULT_AVATAR = "https://assets.teinon.net/assets/design/default_avatar.png"
class Parser {
    object Search{
        private fun namesFromDocument(document: Document): List<String> {
            val names = mutableListOf<String>()
            for (element in document.select("a.visit-link")){
                names.add(element.text())
            }
            return names
        }
        private fun fandomsFromDocument(document: Document): List<List<Fandom>>{
            val fandomsAll = mutableListOf<MutableList<Fandom>>()
            for(element in document.select(".fanfic-main-info")){
                val f = mutableListOf<Fandom>()
                val u = element.select(".ic_fandom")
                if (u.isNullOrEmpty()){
                    f.add(Fandom("-", ""))
                }
                else{
                    for(n in u){
                        for(p in n.parent()!!.select("a")){
                            f.add(Fandom(p.text(), p.attr("href")))
                        }
                    }
                }

                fandomsAll.add(f)
            }
            return fandomsAll
        }

        private fun urlsFromDocument(document: Document): List<String>{
            val elements = document.select("a.visit-link")

            val urls = mutableListOf<String>()
            for (element in elements){
                urls.add(element.attr("href"))
            }
            return urls
        }

        private fun tagsFromDocument(document: Document): List<List<Tag>>{
            val fanficArticle = document.select("div.fanfic-main-info")
            val tags = mutableListOf<MutableList<Tag>>()
            for (element in fanficArticle){
                val tagEach = mutableListOf<Tag>()
                element.select("a.tag").forEach{
                    tagEach.add(
                        Tag(it.text(), it.attr("href"))
                    )
                }
                tags.add(tagEach)
            }
            return tags
        }

        private fun authorsFromDocument(document: Document): List<String>{
            val authorsData = document.select("span.author")
            val authors = mutableListOf<String>()
            for(author in authorsData){
                authors.add(author.select("a")[0].text())
            }
            return authors
        }

        private fun authorsUrlsFromDocument(document: Document): List<String>{
            val authorsData = document.select("span.author")
            val authors = mutableListOf<String>()
            for(author in authorsData){
                authors.add(author.select("a")[0].attr("href"))
            }
            return authors
        }

        private fun getType(e: Element): BadgeType {
            var type: BadgeType = BadgeType.STATUS
            if(e.hasClass("direction")){
                type = BadgeType.DIRECTION
            }
            else if (e.hasClass("badge-like")){
                type = BadgeType.LIKES
            }
            else if(e.hasClass("badge-reward")){
                type = BadgeType.TROPHY
            }
            else if(
                e.hasClass("badge-rating-PG-13") ||
                e.hasClass("badge-rating-NC-17") ||
                e.hasClass("badge-rating-G") ||
                e.hasClass("badge-rating-R") ||
                e.hasClass("badge-rating-NC-21")
            ) {
                type = BadgeType.RATING
            }

            return type
        }

        private fun badgesFromDocument(document: Document): List<List<Badge>>{
            val fanfics = document.select("div.fanfic-badges")
            val badges = mutableListOf<MutableList<Badge>>()
            for(fanfic in fanfics){
                val badgesEach = mutableListOf<Badge>()
                for(badge in fanfic.select("div.badge-with-icon")){
                    val type = getType(badge)

                    badgesEach.add(Badge(badge.text(), type))
                }
                badges.add(badgesEach)
            }
            return badges
        }

        private fun pageFromDocument(document: Document): Int {
            val t = document.select("span.active")
            if (t.isNotEmpty()){
                return t[0].text().toInt()
            }
            return 1
        }

        private fun lastPageFromDocument(document: Document): Int{
            val elements = document.select("nav.pagination")
            if(elements.isNullOrEmpty()){
                return 1
            }
            var max = 0
            for(i in elements[0]){
                val temp = i.text().toIntOrNull()
                if(temp != null){
                    if (temp > max){
                        max = temp
                    }
                }
            }
            return max
        }

        fun descriptionsFromDocument(document: Document) : List<String>{
            val t = mutableListOf<String>()
            for (description in document.select(".fanfic-description")){
                t.add(description.text())
            }
            return t
        }

        fun fullParse(html: String): PageData {
            val parseable = Jsoup.parse(html)
            val names = namesFromDocument(parseable)
            val urls = urlsFromDocument(parseable)
            val tags = tagsFromDocument(parseable)
            val authors = authorsFromDocument(parseable)
            val authorUrls = authorsUrlsFromDocument(parseable)
            val badges = badgesFromDocument(parseable)
            val descriptions = descriptionsFromDocument(parseable)
            val fanfics = mutableListOf<Fanfic>()
            val fandoms = fandomsFromDocument(parseable)
            println(fandoms)
            for(index in names.indices){
                fanfics.add(
                    Fanfic(
                        name = names[index],
                        author = Author(authors[index], authorUrls[index]),
                        tags = tags[index],
                        badges = badges[index],
                        url = urls[index],
                        shortDescription = descriptions[index],
                        fandoms = fandoms[index],
                    )
                )
            }
            return PageData(
                fanfics,
                pageFromDocument(document = parseable),
                lastPageFromDocument(document = parseable),
                nothingFound = "Не удалось найти ничего с указанными вами параметрами." in html
            )
        }
    }

    object FanficPage{
        fun getParts(html: String): List<Part>? {
            val document = Jsoup.parse(html)
            val parts = mutableListOf<Part>()
            for(element in document.select("li.part")){
                parts.add(
                    Part(
                        element.select("a.part-link")[0].text(),
                        element.select("a.part-link")[0].attr("href"),
                        element.select("span")[0].attr("title")
                    )
                )
            }
            return parts.ifEmpty {
                null
            }

        }

        fun getText(html: String): String? {
            val document = Jsoup.parse(html)
            document.outputSettings().prettyPrint(false)
            val b = document.getElementById("content")?.html() ?: return null
            val outputSettings = Document.OutputSettings()
            outputSettings.prettyPrint(false)
            val text = Jsoup.clean(b, "", Safelist.none(), outputSettings)
            val textFinal = text.replace("&nbsp;", " ")
            return textFinal
        }
    }

    object AuthorPage {
        fun getName(document: Document): String{
            return document.select(".user-name").first()?.text() ?: "?"
        }

        fun getImageUrl(document: Document): String {
            return document.select("div.avatar-cropper").first()?.select("img")?.first()?.attr("src") ?: DEFAULT_AVATAR
        }

        fun fullParse(html: String): AuthorInfo{
            val parseable = Jsoup.parse(html)
            val name = getName(parseable)
            val imageUrl = getImageUrl(parseable)
            val fanfics = Search.fullParse(html)
            return AuthorInfo(name, imageUrl, fanfics)
        }
    }

    object Comments {
        fun _get(document: Document): List<Comment>{
            val comments = mutableListOf<Comment>()
            for(element in document.select("article.comment-container")) {
                val name = element.select("a.js-comment-author").first()?.text() ?: "Неизвестно"
                val authorUrl = element.select("div.avatar-cropper").first()?.select("a")?.first()?.attr("href") ?: ""
                val avatar = element.select("div.avatar-cropper").first()?.select("img")?.attr("src") ?: DEFAULT_AVATAR
                val postDate = element.select("time.comment-date").first()?.text() ?: ""
                val textUnescaped = element.select("div.comment-message").first()?.html() ?: ""
                comments.add(
                    Comment(
                        Author(name, authorUrl),
                        avatar,
                        postDate,
                        textUnescaped
                    )
                )
            }
            return comments
        }

        fun get(html: String): List<Comment>{
            val parseable = Jsoup.parse(html)

            return _get(parseable)
        }
    }
}