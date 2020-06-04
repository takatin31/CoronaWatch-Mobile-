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
import com.example.coronawatch.Controllers.PaginationScrollListener
import com.example.coronawatch.DataClasses.ArticleThumbnail

import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import com.example.coronawatch.Testing.EspressoIdelingResource
import kotlinx.android.synthetic.main.fragment_articles.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ArticlesFragment : Fragment() {

    lateinit var adapter: ArticleAdapter
    lateinit var layoutManager : LinearLayoutManager
    val thumbnailArticleList = arrayListOf<ArticleThumbnail>()
    //le contexte de l'activity
    lateinit var mContext:Context
    var detached : Boolean = true
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var currentPage : Int = 0
    var urlData = ""
    var created = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("urrrrrrrrrrl123", urlData+"kjhkh")
        if (urlData == "")
        urlData = "${resources.getString(R.string.host)}/api/v0/article/pages"
        Log.i("urrrrrrrrrrl456", urlData)

        layoutManager = LinearLayoutManager(activity)
        articleRecycler.layoutManager = layoutManager

        adapter = ArticleAdapter(activity!!, thumbnailArticleList)
        articleRecycler.adapter = adapter

        articleRecycler.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                //you have to call loadmore items to get more data
                getMoreItems()
            }
        })

        created = true
        start()
    }

    fun start(){
        isLoading = true
        getListArticlesThumbnail(1)
    }


    fun getMoreItems() {

        if (!isLastPage && !isLoading){
            isLoading = true
            Log.i("currentPage", currentPage.toString())
            getListArticlesThumbnail(currentPage+1)
        }

    }

    fun getListArticlesThumbnail(page : Int){

        EspressoIdelingResource.increment()
        val newList = arrayListOf<ArticleThumbnail>()
        val getUrl = "$urlData/$page"


        // Request a string response from the provided URL.
        val jsonRequestData = JsonObjectRequest(
            Request.Method.GET, getUrl, null,
            Response.Listener { response ->
                Log.i("respppppp", response.toString())
                val items = response.getJSONArray("rows")
                if (items.length() == 0){
                    isLastPage = true
                }else{

                    Log.i("currentPage", currentPage.toString())
                    for (i in 0 until items.length()){
                        val item = items.getJSONObject(i)
                        val articleId = item.getInt("articleId")
                        var dateArticle = item.getString("dateArticle")
                        val articleTitle = item.getString("titre")
                        val articleDesc = item.getString("sous_titre")
                        val articleImage = item.getString("imageUrl")
                        val listTags = arrayListOf<String>()
                        val tags = item.getJSONArray("tags")
                        for (i in 0 until  tags.length()){
                            listTags.add(tags.getJSONObject(i).getString("description"))
                        }
                        var localDateTime: LocalDateTime = LocalDateTime.parse(dateArticle.replace("Z", ""))
                        var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                        dateArticle = formatter.format(localDateTime)
                        val articleThumbnail = ArticleThumbnail(articleId, articleImage, articleTitle, articleDesc, dateArticle, listTags, 0 )
                        newList.add(articleThumbnail)
                    }

                    if (loadingArticleProgressBar != null){
                        loadingArticleProgressBar.visibility = View.INVISIBLE
                    }

                    adapter.addData(newList)

                }
                currentPage = page
                isLoading = false
                EspressoIdelingResource.decrement()


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
