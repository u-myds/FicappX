package u.ficappx.components.db

import u.ficappx.api.classes.Author
import u.ficappx.api.classes.Badge
import u.ficappx.api.classes.Fandom
import u.ficappx.api.classes.Tag
import kotlinx.serialization.json.Json

class Converters {
    companion object {
        fun fromTagList(tags: List<Tag>) : String{
            return Json.encodeToString(tags)
        }
        fun toTagList(json: String) : List<Tag>{
            return Json.decodeFromString<List<Tag>>(json)
        }

        fun fromBadgeList(badges: List<Badge>) : String{
            return Json.encodeToString(badges)
        }
        fun toBadgeList(json: String) : List<Badge> {
            return Json.decodeFromString<List<Badge>>(json)
        }

        fun fromFandomList(fandoms: List<Fandom>) : String{
            return Json.encodeToString(fandoms)
        }
        fun toFandomList(json: String) : List<Fandom> {
            return Json.decodeFromString<List<Fandom>>(json)
        }
        fun fromAuthor(author: Author) : String{
            return Json.encodeToString(author)
        }
        fun toAuthor(json: String) : Author {
            return Json.decodeFromString<Author>(json)
        }
    }



}