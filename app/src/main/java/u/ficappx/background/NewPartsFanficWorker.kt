package u.ficappx.background

import u.ficappx.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import u.ficappx.api.Parser
import u.ficappx.api.classes.Fanfic
import u.ficappx.components.db.DBHelper

const val SCHEME = "https"
const val HOST = "ficbook.net"
class NewPartsFanficWorker(val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try{
                val db = DBHelper(context)
                val fanfics = db.getAllFanfics()
                val client = OkHttpClient()
                for(fanfic in fanfics) {
                    val url = HttpUrl.Builder().scheme(SCHEME).host(HOST).addPathSegments(
                        fanfic.url.replaceFirst("/", "")
                    ).build()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    if(!response.isSuccessful) Result.failure()
                    val parts = Parser.FanficPage.getParts(response.body!!.string())?.size ?: 1
                    if(parts > fanfic.partsCount) {
                        notifyNewPart(context, fanfic)
                        db.deleteFanfic(fanfic)
                        db.insert(fanfic.apply { partsCount = parts })
                    }

                }
                Result.success()
            }
            catch (e: Exception){
                Result.retry()
            }
        }


    }

    private fun notifyNewPart(context: Context, fanfic: Fanfic){
        println("Called notify!")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "new_parts_channel"
        val channelName = "Новая часть"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Уведомления о новых частях"
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Новая часть")
            .setContentText("Вышла новая часть \"${fanfic.name}\" автора ${fanfic.author.name}")
            .setSmallIcon(R.drawable.icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}