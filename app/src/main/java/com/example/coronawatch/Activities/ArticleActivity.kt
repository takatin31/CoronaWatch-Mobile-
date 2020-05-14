package com.example.coronawatch.Activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import kotlinx.android.synthetic.main.activity_article.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ArticleActivity : AppCompatActivity() {

    private var playbackPos = 0
    private lateinit var mediaController: MediaController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        videoController.visibility = View.GONE

        val articleId = intent.getIntExtra("articleId", -1)

        getArticle(articleId)


    }

    fun playVideo(videoUrl : String){
        mediaController = MediaController(this)

        val uri = Uri.parse(videoUrl)
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener{
            mediaController.setAnchorView(videoController)
            videoView.setMediaController(mediaController)
            videoView.seekTo(playbackPos)
            videoView.start()
        }
    }

    private fun getArticle(articleId: Int) {
        val urlData = "${resources.getString(R.string.host)}/api/v0/article/$articleId"

        // Request a string response from the provided URL.
        val jsonRequestData = JsonObjectRequest(
            Request.Method.GET, urlData, null,
            Response.Listener { response ->
                val title = response.getString("titre")
                var date = response.getString("updatedAt")
                val content = response.getString("contenu")
                var localDateTime: LocalDateTime = LocalDateTime.parse(date.replace("Z", ""))
                var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                date = formatter.format(localDateTime)

                val listTags = arrayListOf<String>()
                val tags = response.getJSONArray("tags")
                for (i in 0 until  tags.length()){
                    listTags.add(tags.getJSONObject(i).getString("description"))
                }

                val videoUrl = response.getString("videoUrl")
                if (videoUrl == ""){
                    videoController.visibility = View.GONE
                }else{
                    videoController.visibility = View.VISIBLE
                    playVideo(videoUrl)
                }

                for (tag in listTags){

                    val tagView = TextView(this)
                    tagView.setPadding(20, 8, 20, 8)

                    val params: LinearLayout.LayoutParams =
                        LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    params.setMargins(10, 5, 10, 5)
                    tagView.layoutParams = params

                    tagView.background = resources.getDrawable(R.drawable.tag_shape)
                    tagView.text = tag
                    tagsContainer.addView(tagView)

                }

                if (progressBarArticle != null){
                    progressBarArticle.visibility = View.INVISIBLE
                }

                articleTitle.text = title
                articleDate.text = date

                articleContent.setMarkDownText(content)
                articleContent.isOpenUrlInBrowser = true


            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestData)
    }
}
