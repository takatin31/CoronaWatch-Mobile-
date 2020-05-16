package com.example.coronawatch.Adapters

import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.coronawatch.Activities.ArticleActivity
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.R
import com.google.android.flexbox.FlexboxLayout
import com.squareup.picasso.Picasso


class ArticleAdapter(val activity: FragmentActivity, val listArticle : ArrayList<ArticleThumbnail>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>(){
    class ArticleViewHolder(v : View) : RecyclerView.ViewHolder(v){
        val imageArticle = v.findViewById<ImageView>(R.id.article_image)
        val titleArticle = v.findViewById<TextView>(R.id.article_title)
        val dateArticle = v.findViewById<TextView>(R.id.articleDate)
        val descriptinoArticle = v.findViewById<TextView>(R.id.articleDescription)
        val nbrCommentsArticle = v.findViewById<TextView>(R.id.article_number_comments)
        val showComments = v.findViewById<LinearLayout>(R.id.show_comments_trigger)
        val editComment = v.findViewById<EditText>(R.id.article_comment_box)
        val thumbnailLayout = v.findViewById<LinearLayout>(R.id.articleThumbnailLayout)
        val tagsContainer = v.findViewById<FlexboxLayout>(R.id.tagsContainer)
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
        val listTags = listArticle[position].tags

        holder.tagsContainer.removeAllViews()

        for (tag in listTags){

            val tagView = TextView(activity)
            tagView.setPadding(20, 8, 20, 8)

            val params: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(10, 5, 10, 5)
            tagView.layoutParams = params

            tagView.background = activity.resources.getDrawable(R.drawable.tag_shape)
            tagView.text = tag
            holder.tagsContainer.addView(tagView)

        }

        holder.titleArticle.text = articleTitle
        holder.nbrCommentsArticle.text = articleNbrComment.toString()

        holder.dateArticle.text = articleDate
        holder.descriptinoArticle.text = articleDescription

        Picasso.get().load("${activity.resources.getString(R.string.host)}/$articleImage").into(holder.imageArticle)


        holder.showComments.setOnClickListener {

        }

        holder.thumbnailLayout.setOnClickListener {
            val articleIntent = Intent(activity, ArticleActivity::class.java)
            articleIntent.putExtra("articleId", articleId)
            activity.startActivity(articleIntent)
        }
    }

    fun addData(listItems: ArrayList<ArticleThumbnail>) {
        var size = this.listArticle.size
        this.listArticle.addAll(listItems)
        var sizeNew = this.listArticle.size
        notifyItemRangeChanged(size, sizeNew)
    }


}