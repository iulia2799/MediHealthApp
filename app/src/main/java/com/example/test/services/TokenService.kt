package com.example.test.services

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.test.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TokenService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("NEW_TOKEN",newToken)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.body?.let {
            Log.d("MESSAE", it)
            if(message.notification?.title?.isNotEmpty() == true) {
                val title = message.notification!!.title
                val body = message.notification!!.body
                val channelId: String = NotificationManager.IMPORTANCE_DEFAULT.toString()
                val notificationBuilder = NotificationCompat.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.background)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                val notificationId = 1
                val notificationManager = NotificationManagerCompat.from(this)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                if (title != null) {
                    Log.d("MESSAESUCCES",title)
                }
                notificationManager.notify(notificationId,notificationBuilder.build())
            }
        }

    }

}