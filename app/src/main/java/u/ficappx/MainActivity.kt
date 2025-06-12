package u.ficappx

//import androidx.compose.ui.unit.Constraints
import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Headers
import okhttp3.OkHttpClient
import u.ficappx.api.FicbookAPI
import u.ficappx.api.classes.testFanfic
import u.ficappx.api.mobile.FicbookMobileAPI
import u.ficappx.background.NewPartsFanficWorker
import u.ficappx.background.NewPartsFanficWorker.Companion.notifyNewPart
import u.ficappx.components.fragments.SearchFragmentSaver
import u.ficappx.components.web.CookieJarC
import u.ficappx.ui.components.NavBar
import u.ficappx.ui.components.defined.AnimatedVisibilityFadeInOut
import u.ficappx.ui.components.defined.SmoothAppearAfter
import u.ficappx.ui.components.enums.FragmentState
import u.ficappx.ui.components.fragments.saves.SavesFragment
import u.ficappx.ui.components.fragments.search.SearchFragment
import u.ficappx.ui.components.fragments.settings.SettingsFragment
import u.ficappx.ui.theme.FicappXTheme
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    lateinit var cookiesPresentedAndValid: MutableState<Boolean>
    private val cookieJarC = CookieJarC()
    private val client = OkHttpClient.Builder().cookieJar(cookieJarC)
    private val headers = Headers.Builder().add("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
    var ficbookAPI: FicbookAPI? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val searchSaver = SearchFragmentSaver()

        setupNotifications()
        enableEdgeToEdge()
        setContent {
            RequestNotification()
            cookiesPresentedAndValid = remember { mutableStateOf(false) }
            var currentState by remember { mutableStateOf(FragmentState.SEARCH) }
            val context = LocalContext.current
            val mobileApi = FicbookMobileAPI()
            exceptionHandler(context)
            FicappXTheme() {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if(cookiesPresentedAndValid.value) {
                            NavBar(searchClicked = {currentState = FragmentState.SEARCH},
                                savedClicked =  {currentState = FragmentState.SAVED},
                                settingsClicked = {currentState = FragmentState.SETTINGS},
                                state = currentState
                                )
                        } }) { innerPadding ->
                                if (cookiesPresentedAndValid.value && ficbookAPI != null) {
                                    AnimatedVisibilityFadeInOut(
                                        visible = currentState == FragmentState.SEARCH
                                    ) {
                                        SearchFragment(ficbookAPI!!, searchSaver, innerPadding, mobileApi)
                                    }

                                    AnimatedVisibilityFadeInOut(
                                        visible = currentState == FragmentState.SAVED
                                    ) {
                                        SavesFragment(innerPadding)
                                    }

                                    AnimatedVisibilityFadeInOut(
                                        visible = currentState == FragmentState.SETTINGS
                                    ) {
                                        SettingsFragment(innerPadding)
                                    }

                                }
                                else{
                                    SetCookies()
                                }

                }
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun SetCookies(){

        val context = LocalContext.current
        val webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            loadUrl("https://ficbook.net/find-fanfics-846555")
        }
        webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                var text = ""
                webView.evaluateJavascript("(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"){
                    text = it
                }
                val cookieManager = CookieManager.getInstance()
                val cookies = cookieManager.getCookie(url)
                if (url == "https://ficbook.net/find-fanfics-846555" && cookies != null && "cf_clearance" in cookies.toString() &&
                    "Проверка Вашего браузера" !in text
                    ){
                    cookieJarC.addWebViewCookies(url, cookies.toString())
                    cookiesPresentedAndValid.value = true
                    headers.add("user-agent", view?.settings?.userAgentString!!)
                    ficbookAPI = FicbookAPI(client.build(), headers.build())
                    cookieManager.removeAllCookies(null)
                    cookieManager.flush()

                }
                super.onPageFinished(view, url)
            }
        }
        AndroidView({webView}, modifier = Modifier
            .size(0.dp, 0.dp)
            .alpha(0f))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.animateContentSize().fillMaxWidth()) {
                CircularProgressIndicator()
                Spacer(Modifier.size(0.dp, 8.dp))
                Text("Пожалуйста, подождите :)")
                SmoothAppearAfter(5) {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Может, нужен VPN?")
                        SmoothAppearAfter(4) {
                            Text("Ну, или это просто долгая загрузка")
                        }
                    }

                }
            }


        }


    }

    @Composable
    private fun RequestNotification(){
        val context = LocalContext.current
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}
        var hasNotificationPermission by remember {  mutableStateOf(false) }


        LaunchedEffect(Unit) {
            hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
            if (!hasNotificationPermission) {
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun exceptionHandler(context: Context) {
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e("Handler", "Uncaught exception on thread ${t.name}: ${e.message}", e)
            Toast.makeText(context, "Ошибка! Информация скопирована в буфер обмена", Toast.LENGTH_LONG).show()
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(e.message,e.stackTraceToString())
            clipboard.setPrimaryClip(clip)
            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }

    private fun setupNotifications(): PeriodicWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dataSync = PeriodicWorkRequest.Builder(
            NewPartsFanficWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NewPartsNotify",
            ExistingPeriodicWorkPolicy.KEEP,
            dataSync
        )
        return dataSync
    }
}
