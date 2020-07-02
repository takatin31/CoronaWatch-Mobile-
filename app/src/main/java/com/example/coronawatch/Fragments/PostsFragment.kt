package com.example.coronawatch.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.coronawatch.Adapters.PostAdapter
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.DataClasses.Post
import com.example.coronawatch.DataClasses.VideoThumbnail
import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_video.*
import org.json.JSONArray
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class PostsFragment : Fragment() {

    lateinit var adapter: PostAdapter
    lateinit var layoutManager : LinearLayoutManager
    val postsList = arrayListOf<Post>()
    //le contexte de l'activity
    lateinit var mContext:Context
    var detached : Boolean = true
    var isLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(activity)
        postRecycler.layoutManager = layoutManager

        adapter = PostAdapter(activity!!, postsList)
        postRecycler.adapter = adapter

        getData()

    }

    fun getData(){

        val urlData = "${resources.getString(R.string.host)}/api/v0/publication"

        // Request a string response from the provided URL.
        val jsonRequestData = JsonObjectRequest(
            Request.Method.GET, urlData, null,
            Response.Listener { response ->
                val items = response.getJSONArray("rows")

                for (i in 0 until items.length()){
                    val post = items.getJSONObject(i)
                    val title = post.getString("titre")
                    val source = post.getString("source")
                    val resume = post.getString("resume")
                    val lien = post.getString("lien")
                    if (source == "Facebook"){
                        postsList.add(Post(title, resume, source, lien, null, null))
                    }else{
                        var date = post.getString("datePublication")
                        val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val result: Date = df1.parse(date)
                        val formatter = SimpleDateFormat("yyyy-MM-dd")
                        //var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        date = formatter.format(result)
                        //date = result.toString()
                        val img = post.getString("imageUrl")
                        postsList.add(Post(title, resume, source, lien, img, date))
                    }
                }

                if (loadingPostsProgressBar != null){
                    loadingPostsProgressBar.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestData)

    }

    private fun loadJson(context: Context, file : String): String? {
        var input: InputStream? = null
        var jsonString: String

        try {
            // Create InputStream
            input = context.assets.open(file)

            val size = input.available()

            // Create a buffer with the size
            val buffer = ByteArray(size)

            // Read data from InputStream into the Buffer
            input.read(buffer)

            // Create a json String
            jsonString = String(buffer)
            return jsonString;
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            // Must close the stream
            input?.close()
        }

        return null
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
