package u.ficappx.components

import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

class HtmlUtlis {
    companion object {
        fun parseComment(html: String): AnnotatedString {
            val spanned = HtmlCompat.fromHtml(html.replace("\n", "<br>"),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            val text = spanned.toString()
            return buildAnnotatedString {
                append(text)
                val spans = spanned.getSpans(0, text.length, Any::class.java)
                for (span in spans) {
                    val start = spanned.getSpanStart(span)
                    val end = spanned.getSpanEnd(span)

                    when (span) {
                        is StyleSpan -> {
                            when (span.style) {
                                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                            }
                        }
                        is UnderlineSpan -> addStyle(
                            SpanStyle(textDecoration = TextDecoration.Underline), start, end
                        )
                        is ForegroundColorSpan -> addStyle(
                            SpanStyle(color = Color(span.foregroundColor)), start, end
                        )
                        is URLSpan -> {
                            addStyle(
                                SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                ), start, end
                            )
                            addStringAnnotation("URL", span.url, start, end)
                        }
                    }
                }

                Regex("<div class=\"quoted\">(.*?)</div>", RegexOption.IGNORE_CASE).findAll(html).forEach { match ->
                    val raw = HtmlCompat.fromHtml(match.value, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    val start = text.indexOf(raw)
                    if(start >= 0) {
                        val end = start + raw.length
                        addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                    }
                }

                Regex(">(.*?)\n", RegexOption.IGNORE_CASE).findAll(html).forEach { match ->
                    val raw = HtmlCompat.fromHtml(match.value, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    val start = text.indexOf(raw)
                    if(start >= 0) {
                        val end = start + raw.length
                        addStyle(SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold), start, end)
                    }
                }
                Regex("<center>(.*?)</center>", RegexOption.IGNORE_CASE).findAll(html).forEach { match ->
                    val rawCenteredText = HtmlCompat.fromHtml(match.value, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    val start = text.indexOf(rawCenteredText)
                    if (start >= 0) {
                        val end = start + rawCenteredText.length
                        addStyle(ParagraphStyle(textAlign = TextAlign.Center), start, end)
                    }
                }
            }
        }
    }
}