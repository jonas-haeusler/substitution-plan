package de.jonashaeusler.vertretungsplan.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.data.entities.Event
import de.jonashaeusler.vertretungsplan.ui.main.MainActivity

class SubstitutionNotification(private val context: Context) {

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(context, context.getString(R.string.notification_channel_substitutions_id))
                .setSmallIcon(R.drawable.ic_exam)
                .setContentIntent(PendingIntent.getActivity(
                        context,
                        1,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_CANCEL_CURRENT
                ))
    }

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        createNotificationChannel()
    }

    fun setEvents(events: List<Event>): SubstitutionNotification {
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle()
                .setBigContentTitle(context.resources.getQuantityString(
                        R.plurals.notification_substitutions_title, events.size, events.size
                )).bigText(events.joinToString("\n") {
                    "${it.title}: ${it.text}"
                })
        )

        return this
    }

    fun show() = notificationManager.notify(1, notificationBuilder.build())

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_substitutions_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("channel_substitutions", name, importance)

            notificationManager.createNotificationChannel(channel)
        }
    }
}
