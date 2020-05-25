package com.example.coronawatch.Activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.coronawatch.Adapters.CommentAdapter
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.DataClasses.Comment
import com.example.coronawatch.DataClasses.VideoThumbnail
import com.example.coronawatch.Interfaces.Commentable
import com.example.coronawatch.R
import com.example.coronawatch.Request.FileUploadRequest
import com.example.coronawatch.Request.RequestHandler
import kotlinx.android.synthetic.main.activity_video.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VideoActivity : AppCompatActivity(), Commentable {

    private lateinit var mediaController: MediaController
    private var descriptionContainerBool = false
    lateinit var adapter: CommentAdapter
    lateinit var layoutManager : LinearLayoutManager
    val commentList = arrayListOf<Comment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val video = intent.getSerializableExtra("video") as VideoThumbnail

        mediaController = MediaController(this)
        mediaController.setAnchorView(videoContainer)
        val uri = Uri.parse(video.videoUrl)
        videoView.setVideoURI(uri)
        videoView.setMediaController(mediaController)

        videoTitle.text = video.videoTitle
        videoDescription.text = video.videoDesc
        videoDate.text = video.date

        layoutManager = LinearLayoutManager(this)
        videoRecycler.layoutManager = layoutManager

        adapter = CommentAdapter(this, commentList)
        videoRecycler.adapter = adapter

        getComments(video.videoId)

        addCommentBtn.setOnClickListener {
            if (commentEditText.text.toString() != ""){
                val commentContent = commentEditText.text.toString()
                Log.i("this_is_comment", commentContent)
                addComment(commentContent, 1, video.videoId)
                commentEditText.text.clear()
            }
        }

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

    override fun getComments(itemId : Int){
        val urlData = "${resources.getString(R.string.host)}/api/v0/CommentVideo/video/$itemId"
        commentsProgressBar.visibility = View.VISIBLE
        // Request a string response from the provided URL.
        val jsonRequestData = JsonObjectRequest(
            Request.Method.GET, urlData, null,
            Response.Listener { response ->
                commentList.clear()
                val items = response.getJSONObject("items").getJSONArray("rows")
                for (i in 0 until items.length()){
                    val item = items.getJSONObject(i)
                    val commentId = item.getInt("commentVideoId")
                    val commentContent = item.getString("contenu")
                    var commentDate = item.getString("updatedAt")
                    var localDateTime: LocalDateTime = LocalDateTime.parse(commentDate.replace("Z", ""))
                    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                    commentDate = formatter.format(localDateTime)
                    val user = item.getJSONObject("utilisateur")
                    val commentUserName = user.getString("username")
                    val commentUserImg = "${resources.getString(R.string.host)}/"+user.getString("profileImageUrl")
                    val comment = Comment(commentId, commentContent, commentDate, commentUserName, commentUserImg)
                    commentList.add(comment)
                }
                commentsProgressBar.visibility = View.GONE
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestData)
    }

    override fun addComment(content : String, userId : Int, itemId : Int){
        val postURL: String = "${resources.getString(R.string.host)}/api/v0/CommentVideo"

        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                Log.i("success", "comment posted succefully")
                getComments(itemId)
            },
            Response.ErrorListener {
                Log.i("error", "error while posting comment")
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["contenu"] = ArabicController.encode_str(content)
                params["videoVideoId"] = itemId.toString()
                params["utilisateurUtilisateurId"] = userId.toString()

                return params
            }



        }
        Volley.newRequestQueue(this).add(request)
    }


}
