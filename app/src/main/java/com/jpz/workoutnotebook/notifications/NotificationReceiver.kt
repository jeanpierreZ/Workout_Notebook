package com.jpz.workoutnotebook.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity
import com.jpz.workoutnotebook.fragments.editactivity.EditCalendarFragment.Companion.WORKOUT_NAME
import com.jpz.workoutnotebook.notifications.Notification.Companion.CHANNEL_ID
import org.koin.core.KoinComponent
import org.koin.core.inject


class NotificationReceiver : BroadcastReceiver(), KoinComponent {

    private val notification: Notification by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        // Create the notification channel if on API 26+
        context?.let { notification.createNotificationChannel(it) }

        // Show the notification with intent data from EditCalendarFragment
        context?.let {
            intent?.getStringExtra(WORKOUT_NAME)?.let { workoutName ->
                showNotification(it, workoutName)
            }
        }
    }

    private fun showNotification(context: Context, workoutName: String) {
        val title = context.getString(R.string.app_name)
        val text = context.getString(R.string.notification_text, workoutName)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an explicit intent to launch MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        // Set the content and channel of the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_fitness_center_24)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Notify the builder
        notificationManager.notify(0, builder.build())
    }
}