package u.ficappx

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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        var text by remember { mutableStateOf("") }
        var error by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                Log.d("FicNet", url)
                val tempText = FicbookAPI.fanficText(url)
                if(tempText != null){
                    text = tempText
                }
                else{
                    error = true
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


}
