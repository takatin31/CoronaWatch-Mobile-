package com.example.coronawatch.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.DataClasses.Comment
import com.example.coronawatch.DataClasses.Notification
import com.example.coronawatch.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class NotificationAdapter (val activity: FragmentActivity, val listNotifications : ArrayList<Notification>) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    class NotificationViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var userName = v.findViewById<TextView>(R.id.userNameView)
        var userImage = v.findViewById<CircleImageView>(R.id.`userImageٍView`)
        var NotificationDate = v.findViewById<TextView>(R.id.notificationDateView)
        var NotificationContent = v.findViewById<TextView>(R.id.notificationContentView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.notification_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listNotifications.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val Notification = listNotifications[position]

    }


}