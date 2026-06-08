package com.inspiredandroid.kai

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.inspiredandroid.kai.shared.R
import org.koin.java.KoinJavaComponent.inject

/**
 * Intent extra read by MainActivity when the user taps a heartbeat notification. The
 * receiver forwards the signal to `DataRepository.requestOpenHeartbeat()` so the
 * ChatViewModel observer can load the heartbeat conversation.
 */
const val EXTRA_OPEN_HEARTBEAT = "com.inspiredandroid.kai.OPEN_HEARTBEAT"

/**
 * Dedicated high-importance channel for proactive ChatMTF messages. Android keeps
 * a channel's importance after first creation, so this intentionally does not
 * reuse the older generic AI notification channel.
 */
private const val CHANNEL_ID = "chatmtf_proactive_messages"

/**
 * Fixed ID so a new heartbeat report replaces any earlier unread one in the tray
 * instead of piling up. The app only ever has one pending heartbeat conversation.
 */
private const val HEARTBEAT_NOTIFICATION_ID = 9002

actual fun sendHeartbeatNotification(title: String, body: String) {
    val context: Context by inject(Context::class.java)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    ensureChannel(notificationManager)

    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(EXTRA_OPEN_HEARTBEAT, true)
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        HEARTBEAT_NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val sentAt = System.currentTimeMillis()
    val senderName = title.ifBlank { "ChatMTF" }
    val me = Person.Builder()
        .setName("我")
        .build()
    val sender = Person.Builder()
        .setName(senderName)
        .setImportant(true)
        .build()
    val style = NotificationCompat.MessagingStyle(me)
        .setConversationTitle(senderName)
        .addMessage(
            NotificationCompat.MessagingStyle.Message(
                body,
                sentAt,
                sender,
            ),
        )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(senderName)
        .setContentText(body)
        .setStyle(style)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(Notification.CATEGORY_MESSAGE)
        .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        .setWhen(sentAt)
        .setShowWhen(true)
        .addPerson(sender)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(HEARTBEAT_NOTIFICATION_ID, notification)
}

private fun ensureChannel(manager: NotificationManager) {
    if (manager.getNotificationChannel(CHANNEL_ID) != null) return
    val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    manager.createNotificationChannel(
        NotificationChannel(CHANNEL_ID, "ChatMTF 消息", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "ChatMTF 主动消息和长期记忆提醒"
            enableVibration(true)
            vibrationPattern = longArrayOf(0L, 80L, 60L, 120L)
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            setShowBadge(true)
            setSound(sound, audioAttributes)
        },
    )
}
