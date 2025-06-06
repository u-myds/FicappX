package u.ficappx.ui.components.fragments.settings

import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import u.ficappx.R


@Composable
fun SettingsFragment(p: PaddingValues){
    val context = LocalContext.current
    val version: String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
    }
    catch(e: NameNotFoundException){
        "?"
    }

    Box(contentAlignment = Alignment.Center){
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.drawable.icon), "", Modifier
                .size(300.dp, 300.dp)
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
            Column(verticalArrangement = Arrangement.Bottom) {
                Text("Версия: $version")
            }
        }
    }
}