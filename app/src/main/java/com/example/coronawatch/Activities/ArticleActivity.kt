package com.example.coronawatch.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.fragment_articles.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val articleId = intent.getIntExtra("articleId", -1)

        getArticle(articleId)
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
