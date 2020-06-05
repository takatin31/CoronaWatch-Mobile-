package com.example.coronawatch.Fragments


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.coronawatch.Adapters.ArticleAdapter
import com.example.coronawatch.Adapters.NotificationAdapter
import com.example.coronawatch.Adapters.VideoAdapter
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.Controllers.PaginationScrollListener
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.DataClasses.Notification
import com.example.coronawatch.DataClasses.VideoThumbnail

import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import kotlinx.android.synthetic.main.fragment_articles.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_video.*

import kotlinx.android.synthetic.main.video_thumbnail_layout.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NotificationFragment : Fragment() {
    lateinit var adapter: NotificationAdapter
    lateinit var layoutManager : LinearLayoutManager
    val notificationsList = arrayListOf<Notification>()
    //le contexte de l'activity
    lateinit var mContext:Context
    var detached : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(activity)
        notificationRecycler.layoutManager = layoutManager

        adapter = NotificationAdapter(activity!!, notificationsList)
        notificationRecycler.adapter = adapter


        getListNotifications()
    }


    fun getListNotifications(){

        if (!detached){
            val pref = mContext.getSharedPreferences(resources.getString(R.string.shared_pref),0)

            val userId = pref.getInt("userId", -1)
            val urlData = "${resources.getString(R.string.host)}/api/v0/notification/user?userId=$userId"

            // Request a string response from the provided URL.
            val jsonRequestData = JsonObjectRequest(
                Request.Method.GET, urlData, null,
                Response.Listener { response ->
                    if (response.getString("message") == "success"){
                        val items = response.getJSONObject("items").getJSONArray("rows")
                        if (items.length() > 0){
                            for (i in 0 until items.length()){
                                val item = items.getJSONObject(i)
                                val notificationId = item.getInt("notificationId")
                                var notificationTitle = item.getString("title")
                                val notificationContent = ArabicController.decode_str(item.getString("contenu"))
                                var notificationDate = ArabicController.decode_str(item.getString("createdAt"))

                                var localDateTime: LocalDateTime = LocalDateTime.parse(notificationDate.replace("Z", ""))
                                var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                                notificationDate = formatter.format(localDateTime)
                                val notification = Notification(notificationId, notificationTitle, notificationContent, notificationDate, false)
                                notificationsList.add(notification)
                            }

                            if (loadingNotifProgressBar != null){
                                loadingNotifProgressBar.visibility = View.GONE
                            }

                            adapter.notifyDataSetChanged()
                        }
                    }
                },
                Response.ErrorListener { Log.d("Error", "Request error") })

            RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestData)
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        detached = false
    }

    override fun onDetach() {
        super.onDetach()
        detached = true
    }
}
