package com.example.coronawatch.Services

import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.fcm.MessagingService

class NotificationsMessagingService : MessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //Log.i("messssss", remoteMessage.notification!!.body)
        Log.i("messssssqsdqsd", remoteMessage.data.toString())
    }
}