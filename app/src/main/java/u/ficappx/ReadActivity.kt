package u.ficappx

import android.graphics.Typeface
import u.ficappx.api.FicbookAPI
import u.ficappx.ui.theme.FicappXTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import android.os.Build
import android.text.Html
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.ByteString.Companion.encodeUtf8
import u.ficappx.api.mobile.FicbookMobileAPI
import u.ficappx.ui.components.fragments.settings.Settings

class ReadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var url = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            { intent.getStringExtra("url") }
            else
            { intent.extras?.getString("url") }


            if (url == null) finishActivity(1)

            if (url != null) url = url.replace("#part_content", "")

            Log.d("FicNet", "url: $url")
            FicappXTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(url != null) FanficTextView(url, innerPadding)

                }
            }
        }
    }



    @Composable
    fun FanficTextView(url: String, p: PaddingValues){
        val coroutineScope = rememberCoroutineScope()
        var text by remember { mutableStateOf(buildAnnotatedString {  }) }
        var error by remember { mutableStateOf(false) }
        var context = LocalContext.current
        var settings = Settings(context)
        LaunchedEffect(Unit) {
            if(settings.use_mobile_api.value == 1){
                coroutineScope.launch(Dispatchers.IO) {
                    var mobileAPI = FicbookMobileAPI()
                    val textObject = mobileAPI.getText(url.split("/").last())
                    if (textObject != null) {
                        var textTmp = textObject.data.encodeUtf8().utf8()
                        text = parseHtmlWithCentering(textTmp)
                    }
                    else{
                        error = true
                    }
                }
            }
            else{
                coroutineScope.launch(Dispatchers.IO) {
                    Log.d("FicNet", url)
                    val tempText = FicbookAPI.fanficText(url)
                    if(tempText != null){
                        text = buildAnnotatedString {
                            append(tempText)
                        }
                    }
                    else{
                        error = true
                    }
                }
            }

        }
        SelectionContainer(modifier = Modifier.padding(p)) {
            Box(modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 2.dp)){
                if(error){
                    Text("Ошибка загрузки")
                }
                Text(text, modifier = Modifier.verticalScroll(rememberScrollState()), softWrap = true)
            }
        }

    }


    private fun parseHtmlWithCentering(html: String): AnnotatedString {
        val temp = html.replace("\n", "<br>")
        val spanned = HtmlCompat.fromHtml(temp, HtmlCompat.FROM_HTML_MODE_LEGACY)
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
