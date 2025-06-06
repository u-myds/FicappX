package u.ficappx.api

import u.ficappx.api.classes.Author
import u.ficappx.api.classes.Badge
import u.ficappx.api.classes.Fandom
import u.ficappx.api.classes.Fanfic
import u.ficappx.api.classes.Part
import u.ficappx.api.classes.SearchPageData
import u.ficappx.api.classes.Tag
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist

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
        private fun badgesFromDocument(document: Document): List<List<Badge>>{
            val fanfics = document.select("div.fanfic-badges")
            val badges = mutableListOf<MutableList<Badge>>()
            for(fanfic in fanfics){
                val badgesEach = mutableListOf<Badge>()
                for(badge in fanfic.select("div.badge-with-icon")){
                    var icon = ""
                    if (badge.select("use").isNotEmpty()){
                        icon = badge.select("use")[0].attr("href")
                    }

                    badgesEach.add(
                        Badge(
                        badge.text(),
                        icon
                    )
                    )
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

        fun fullParse(html: String): SearchPageData {
            val parceable = Jsoup.parse(html)
            val names = namesFromDocument(parceable)
            val urls = urlsFromDocument(parceable)
            val tags = tagsFromDocument(parceable)
            val authors = authorsFromDocument(parceable)
            val authorUrls = authorsUrlsFromDocument(parceable)
            val badges = badgesFromDocument(parceable)
            val descriptions = descriptionsFromDocument(parceable)
            val fanfics = mutableListOf<Fanfic>()
            val fandoms = fandomsFromDocument(parceable)
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
                        fandoms = fandoms[index]
                    )
                )
            }
            return SearchPageData(
                fanfics,
                pageFromDocument(document = parceable),
                lastPageFromDocument(document = parceable),
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
            var b = document.getElementById("content")?.html() ?: return null
            val outputSettings = Document.OutputSettings()
            outputSettings.prettyPrint(false)
            val text = Jsoup.clean(b, "", Safelist.none(), outputSettings)
            val textFinal = text.replace("&nbsp;", " ")
            return textFinal
        }


    }
}