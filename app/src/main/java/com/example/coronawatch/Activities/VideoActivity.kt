package com.example.coronawatch.Activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import com.example.coronawatch.DataClasses.VideoThumbnail
import com.example.coronawatch.R
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.activity_article.videoView
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    private lateinit var mediaController: MediaController
    private var descriptionContainerBool = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val video = intent.getSerializableExtra("video") as VideoThumbnail

        mediaController = MediaController(this)
        mediaController.setAnchorView(videoController)
        val uri = Uri.parse(video.videoUrl)
        videoView.setVideoURI(uri)
        videoView.setMediaController(mediaController)

        videoTitle.text = video.videoTitle
        videoDescription.text = video.videoDesc
        videoDate.text = video.date

        videoTitleContainer.setOnClickListener {
            descriptionContainerBool = !descriptionContainerBool
            if (descriptionContainerBool){
                videoDescriptionContainer.visibility = View.VISIBLE
                arrowDescription.setImageResource(R.drawable.ic_arrow_up)
                divider4.visibility = View.GONE
            }else{
                videoDescriptionContainer.visibility = View.GONE
                arrowDescription.setImageResource(R.drawable.ic_arrow_down)
                divider4.visibility = View.VISIBLE
            }


        }

    }
}
