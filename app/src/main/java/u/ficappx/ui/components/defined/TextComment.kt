package u.ficappx.ui.components.defined

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import u.ficappx.components.HtmlUtlis

@Composable
fun TextComment(html: String){
    val text = HtmlUtlis.parseComment(html)
    Text(text)
}