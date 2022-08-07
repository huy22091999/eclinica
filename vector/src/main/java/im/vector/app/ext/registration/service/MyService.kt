package im.vector.app.ext.registration.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import im.vector.app.VectorApplication
import im.vector.app.ext.data.model.ItemNotification
import im.vector.app.ext.data.model.Notification
import im.vector.app.ext.data.network.PatientAPI
import im.vector.app.ext.data.network.RemoteDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.TimeUnit


open class MyService : Service() {
    var body = Notification(pageIndex = 0, 10)
    lateinit var api: PatientAPI
    var timer = Timer()

    companion object {
        val NOTIFI_ID = 2
        val TAG = "NotifiService"
        private var notifi: ItemNotification = ItemNotification()
        var oldnoti_count = 0
        var newnoti_count = 0

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    // default interval for syncing data
    val DEFAULT_SYNC_INTERVAL = (10 * 1000).toLong()

    // task to be run here

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onCreate() {
        super.onCreate()
//        mCompositeDisposable = CompositeDisposable()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                oldnoti_count = newnoti_count
                // Your Code
                var notifiApi = RemoteDataSource().buildApi(PatientAPI::class.java, this@MyService)
                    .searchNotificationuseCall(body)

                notifiApi.enqueue(object : Callback<ItemNotification?> {
                    override fun onResponse(
                        call: Call<ItemNotification?>,
                        response: Response<ItemNotification?>
                    ) {
                        try {
                            var res = response.body()
                            if (res!!.items!!.isNotEmpty()) {
                                newnoti_count = res.total!!
                                if (oldnoti_count != newnoti_count && oldnoti_count != 0 && newnoti_count != 0) {
                                    res.items?.get(0)?.content.let { sendNotifi(it!!) }

                                }

                            }
                        } catch (e: NullPointerException) {
                            e.message
                        }

                    }

                    override fun onFailure(call: Call<ItemNotification?>, t: Throwable) {
                        t.message
                    }
                })

            }

        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(20))

        return START_NOT_STICKY
    }

    fun sendNotifi(content: String) {
        var builder = NotificationCompat.Builder(this, VectorApplication.CHANNEL_ID)
            .setSmallIcon(im.vector.app.R.drawable.gcom_logo_official)
            .setContentTitle("Telehealth Notification")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFI_ID, builder)
        }
        startForeground(NOTIFI_ID, builder)

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        oldnoti_count = 0
        newnoti_count = 0

    }
}





