package com.example.test.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.test.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TokenService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("NEW_TOKEN", newToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.body?.let {
            Log.d("MESSAGE", it)
            if (message.notification?.title?.isNotEmpty() == true) {
                val title = message.notification!!.title
                val body = message.notification!!.body
                var notificationId = System.nanoTime().toInt()
                val channelId = "channel_id"
                val channelName = "My app"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val notificationBuilder =
                    NotificationCompat.Builder(this, channelId).setContentTitle(title)
                        .setContentText(body).setSmallIcon(R.drawable.notif)

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val channel = NotificationChannel(channelId, channelName, importance)
                channel.description = "In app notifications"

                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(notificationId, notificationBuilder.build())
                notificationId += 1
            }
        }

    }

}