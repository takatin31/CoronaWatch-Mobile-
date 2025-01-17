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
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.coronawatch.Adapters.CommentAdapter
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.DataClasses.Comment
import com.example.coronawatch.Interfaces.Commentable
import com.example.coronawatch.R
import com.example.coronawatch.Request.ApiManager
import com.example.coronawatch.Request.FileUploadRequest
import com.example.coronawatch.Request.RequestHandler
import com.example.coronawatch.Testing.EspressoIdelingResource
import kotlinx.android.synthetic.main.activity_article.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ArticleActivity : AppCompatActivity(), Commentable {

    private var playbackPos = 0
    private lateinit var mediaController: MediaController
    lateinit var adapter: CommentAdapter
    lateinit var layoutManager : LinearLayoutManager
    val commentList = arrayListOf<Comment>()
    lateinit var apiManager : ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        apiManager = ApiManager(this)


        videoController.visibility = View.GONE

        val articleId = intent.getIntExtra("articleId", -1)

        getArticle(articleId)

        layoutManager = LinearLayoutManager(this)
        commentsRecycler.layoutManager = layoutManager

        adapter = CommentAdapter(this, commentList)
        commentsRecycler.adapter = adapter

        getComments(articleId)

        addCommentBtn.setOnClickListener {
            if (commentEditText.text.toString() != ""){
                val commentContent = commentEditText.text.toString()
                Log.i("this_is_comment", commentContent)
                val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
                val userId = pref.getInt("userId", -1)
                addComment(commentContent, userId, articleId)
                commentEditText.text.clear()
            }
        }
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

    fun getArticle(articleId: Int) {
        EspressoIdelingResource.increment()
        val getUrl = apiManager.getApiUrl() +  "article/$articleId"

        // Request a string response from the provided URL.
        val jsonRequestData = JsonObjectRequest(
            Request.Method.GET, getUrl, null,
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
                EspressoIdelingResource.decrement()
            },
            Response.ErrorListener {
                EspressoIdelingResource.decrement()

                Log.d("Error", "Request error")
            })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestData)
    }

    override fun getComments(itemId : Int){
        EspressoIdelingResource.increment()
        val getUrl = apiManager.getApiUrl() + "CommentArticle/article/$itemId"
        //commentsProgressBar.visibility = View.VISIBLE
        // Request a string response from the provided URL.
        val jsonRequestData = JsonObjectRequest(
            Request.Method.GET, getUrl, null,
            Response.Listener { response ->

                commentList.clear()
                val items = response.getJSONObject("items").getJSONArray("rows")
                for (i in 0 until items.length()){
                    val item = items.getJSONObject(i)
                    val commentId = item.getInt("commentArticleId")
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
                EspressoIdelingResource.decrement()
            },
            Response.ErrorListener {
                EspressoIdelingResource.decrement()
                Log.d("Error", "Request error")
            })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestData)
    }

    override fun addComment(content : String, userId : Int, itemId : Int){
        val postURL: String =  apiManager.getApiUrl() + "CommentArticle"
        Log.i("addedPostUrl", postURL)
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
                params["articleArticleId"] = itemId.toString()
                params["utilisateurUtilisateurId"] = userId.toString()

                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}
