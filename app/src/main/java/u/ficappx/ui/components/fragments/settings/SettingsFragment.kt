package u.ficappx.ui.components.fragments.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.NameNotFoundException
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.net.toUri
import u.ficappx.R



@Composable
fun SettingsFragment(p: PaddingValues){
    val context = LocalContext.current
    val settings = Settings(context)
    val version: String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
    }
    catch(e: NameNotFoundException){
        "?"
    }

    Box(modifier = Modifier.padding(p).padding(start = 8.dp, end = 8.dp)){
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item{
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                    Image(painter = painterResource(R.drawable.icon), "", Modifier
                        .size(200.dp, 200.dp)
                        .clip(
                            CircleShape
                        ))
                    Text("FicappX", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, "https://github.com/u-myds/FicappX".toUri())
                        context.startActivity(browserIntent)
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.github_mark), "", modifier = Modifier.size(24.dp, 24.dp))
                            Spacer(Modifier.size(4.dp, 0.dp))
                            Text("Исходный код")

                        }
                    }
                }

            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Использовать мобильное API (Нестабильно)", modifier = Modifier.weight(1f))
                    Switch(
                        checked = settings.use_mobile_api.value == 1,
                        onCheckedChange = {
                            settings.check("use_mobile_api")
                            println(settings.use_mobile_api.value)
                        }
                    )
                }

            }

        }
    }
}