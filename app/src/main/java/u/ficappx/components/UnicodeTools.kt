package abv.logopek.ficapp

class UnicodeTools {

    companion object{

        fun decodeUnicode(input: String): String {
            val unicodePattern = "\\\\u([0-9a-fA-F]{4})".toRegex()
            return unicodePattern.replace(input) { matchResult ->
                val unicode = matchResult.groups[1]?.value
                if (unicode != null) {
                    // Преобразуем Unicode в символ
                    val charCode = unicode.toInt(16) // Преобразуем из шестнадцатеричной системы
                    charCode.toChar().toString()
                } else {
                    matchResult.value
                }
            }

        }
    }

}