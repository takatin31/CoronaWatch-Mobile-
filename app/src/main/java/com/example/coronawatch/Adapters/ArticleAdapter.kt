package com.example.coronawatch.Adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.R
import com.squareup.picasso.Picasso
import java.net.URL


class ArticleAdapter(val activity: FragmentActivity, val listArticle : ArrayList<ArticleThumbnail>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>(){
    class ArticleViewHolder(v : View) : RecyclerView.ViewHolder(v){
        val imageArticle = v.findViewById<ImageView>(R.id.article_image)
        val titleArticle = v.findViewById<TextView>(R.id.article_title)
        val dateArticle = v.findViewById<TextView>(R.id.articleDate)
        val descriptinoArticle = v.findViewById<TextView>(R.id.articleDescription)
        val nbrCommentsArticle = v.findViewById<TextView>(R.id.article_number_comments)
        val showComments = v.findViewById<LinearLayout>(R.id.show_comments_trigger)
        val editComment = v.findViewById<EditText>(R.id.article_comment_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(LayoutInflater.from(activity).inflate(R.layout.article_thumbnail_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return listArticle.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        val articleId = listArticle[position].id
        val articleTitle = listArticle[position].title
        val articleImage = listArticle[position].image
        val articleNbrComment = listArticle[position].nbrComments
        val articleDate =  listArticle[position].date
        val articleDescription = listArticle[position].description

        holder.titleArticle.text = articleTitle
        holder.nbrCommentsArticle.text = articleNbrComment.toString()

        holder.dateArticle.text = articleDate
        holder.descriptinoArticle.text = articleDescription

        Picasso.get().load("https://images.unsplash.com/photo-1494253109108-2e30c049369b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80").into(holder.imageArticle)


        holder.showComments.setOnClickListener {

        }
    }
}