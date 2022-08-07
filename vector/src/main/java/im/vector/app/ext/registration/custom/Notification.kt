package im.vector.app.ext.registration.custom

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import im.vector.app.VectorApplication.Companion.CHANNEL_ID
import im.vector.app.R


class Notification(var context: Context? ) {
    companion object {
        val NOTIFI_ID = 1
    }

    fun showNotifi( content: String) {
        var builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.gcom_logo_official)
            .setContentTitle("Telehealth Notification")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        with(NotificationManagerCompat.from(context!!)) {
            notify(NOTIFI_ID, builder)
        }



    }


}
