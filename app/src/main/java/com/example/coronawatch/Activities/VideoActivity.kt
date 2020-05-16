package com.example.coronawatch.Activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import com.example.coronawatch.DataClasses.VideoThumbnail
import com.example.coronawatch.R
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.activity_article.videoView
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    private lateinit var mediaController: MediaController


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

    }
}
