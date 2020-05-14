package com.example.coronawatch.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coronawatch.Activities.HomeActivity
import com.example.coronawatch.Activities.MainActivity
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.R

class ArticleAdapter(val activity: HomeActivity, val listArticle : ArrayList<ArticleThumbnail>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>(){
    class ArticleViewHolder(v : View) : RecyclerView.ViewHolder(v){
       /* val titleTache = v.findViewById<TextView>(R.id.TacheTitle)
        val dateTache = v.findViewById<TextView>(R.id.TacheId)
        val layoutTache = v.findViewById<RelativeLayout>(R.id.itemLayout)*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(LayoutInflater.from(activity).inflate(R.layout.article_thumbnail_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return listArticle.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val title = listArticle[position].title
        val tacheId = listArticle[position].id

        /*holder.titleTache.text = title
        holder.dateTache.text = tacheId.toString()

        holder.layoutTache.setOnClickListener {

        }*/
    }
}