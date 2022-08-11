package com.cbi.app.trs.features.alarms

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.cbi.app.trs.AndroidApplication
import com.cbi.app.trs.R
import com.cbi.app.trs.domain.eventbus.ShowPopupPostVideoEvent
import com.cbi.app.trs.domain.eventbus.ShowPopupReminderIAP
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.utils.AppConstants
import org.greenrobot.eventbus.EventBus
import java.security.SecureRandom

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type")
        when (type) {
            AppConstants.REMINDER_LOGIN -> {
                showNotification(context, "Daily Login", "Keep your streak going! Check out your new updates today!", Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
            }
            AppConstants.REMINDER_IAP -> {
                // sendNotification
                if (AndroidApplication.isActivityVisible()) {
                    EventBus.getDefault().postSticky(ShowPopupReminderIAP())
                } else {
                    showNotification(context, "Reminder", "Your subscription has ended. Please upgrade or renew your subscription to continue using the full version of the App.", Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                }
            }
            else -> {
                // sendNotification
                if (AndroidApplication.isActivityVisible()) {
                    EventBus.getDefault().postSticky(ShowPopupPostVideoEvent())
                } else {
                    showNotification(context, "Reminder", "It's time to do your post-workout mobilizations!", Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                }
            }
        }
    }

    private fun showNotification(context: Context, title: String?, body: String?, intent: Intent?) {
        val channelId = context.getString(R.string.channel_id)
        val channelName = "Virtual Mobility Coach"
        val random = SecureRandom()
        val notifyID = random.nextInt(9999 - 1000) + 1000
        val requestCode = 0
        val pendingIntent =
                PendingIntent.getActivity(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                )
        val sound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val noBuilder =
                NotificationCompat.Builder(context, channelId)
        noBuilder.setStyle(NotificationCompat.BigTextStyle(noBuilder))
                .setSmallIcon(R.drawable.login_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = "Push notification"
            val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            )
            channel.setShowBadge(false)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel)
        }
        noBuilder.setContentIntent(pendingIntent)
        // Set Vibrate, Sound and Light
        var defaults = 0
        defaults = defaults or Notification.DEFAULT_LIGHTS
        defaults = defaults or Notification.DEFAULT_VIBRATE
        defaults = defaults or Notification.DEFAULT_SOUND
        noBuilder.setDefaults(defaults)

        notificationManager.notify(notifyID, noBuilder.build()) //0 = ID of notification
    }

}