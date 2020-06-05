package com.example.coronawatch.Services

enum class NotificationActions(val actionCode : String) {
    dataUserChanged("DATAUSERCHANGED"),
    newNotification("NEWNOTIFICATIONRECEIVED")
}