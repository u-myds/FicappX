package u.ficappx.api.classes

data class Comment(
    val author: Author,
    val authorAvatar: String,
    val postDate: String,
    val textUnescaped: String
)
