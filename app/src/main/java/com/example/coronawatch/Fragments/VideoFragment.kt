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
import com.example.coronawatch.Adapters.VideoAdapter
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.Controllers.PaginationScrollListener
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.DataClasses.VideoThumbnail

import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import kotlinx.android.synthetic.main.fragment_articles.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.video_thumbnail_layout.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class VideoFragment : Fragment() {
    lateinit var adapter: VideoAdapter
    lateinit var layoutManager : LinearLayoutManager
    val thumbnailVideoList = arrayListOf<VideoThumbnail>()
    //le contexte de l'activity
    lateinit var mContext:Context
    var detached : Boolean = true
    var isLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(activity)
        videoRecycler.layoutManager = layoutManager

        adapter = VideoAdapter(activity!!, thumbnailVideoList)
        videoRecycler.adapter = adapter


        getListVideosThumbnail()
    }



    fun getListVideosThumbnail(){
        val urlData = "${resources.getString(R.string.host)}/api/v0/video"

        // Request a string response from the provided URL.
        val jsonRequestData = JsonObjectRequest(
            Request.Method.GET, urlData, null,
            Response.Listener { response ->
                val items = response.getJSONArray("rows")
                if (items.length() == 0){

                }else{

                    for (i in 0 until items.length()){
                        val item = items.getJSONObject(i)
                        val videoId = item.getInt("videoId")
                        var dateVideo = item.getString("dateVideo")
                        val videoTitle = ArabicController.decode_str(item.getString("titre"))
                        val videoDesc = ArabicController.decode_str(item.getString("description"))
                        val videoUrl = "${resources.getString(R.string.host)}/"+item.getString("videoUrl")

                        var localDateTime: LocalDateTime = LocalDateTime.parse(dateVideo.replace("Z", ""))
                        var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                        dateVideo = formatter.format(localDateTime)
                        val videoThumbnail = VideoThumbnail(videoId, videoUrl, videoTitle, videoDesc, dateVideo, false)
                        thumbnailVideoList.add(videoThumbnail)
                    }

                    adapter.notifyDataSetChanged()
                }
                if (loadingVideoProgressBar != null){
                    loadingVideoProgressBar.visibility = View.INVISIBLE
                }
                isLoading = false

            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestData)
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
