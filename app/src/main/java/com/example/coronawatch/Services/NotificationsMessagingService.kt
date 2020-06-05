package com.example.coronawatch.Services

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.example.coronawatch.Activities.HomeActivity
import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.fcm.MessagingService

class NotificationsMessagingService : MessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null){
            val title = remoteMessage.notification!!.title
            val content = remoteMessage.notification!!.body
            val i = Intent()
            i.action = NotificationActions.newNotification.actionCode
            i.putExtra("title", title)
            i.putExtra("content", content)
            i.setClass(this, NotificationReceiver::class.java)
            sendBroadcast(i)
        }


    }
}