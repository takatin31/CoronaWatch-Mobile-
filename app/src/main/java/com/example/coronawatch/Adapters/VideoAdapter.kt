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
import com.example.coronawatch.DataClasses.VideoThumbnail
import com.example.coronawatch.R
import com.google.android.flexbox.FlexboxLayout
import com.squareup.picasso.Picasso


class VideoAdapter(val activity: FragmentActivity, val listArticle : ArrayList<VideoThumbnail>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>(){
    class VideoViewHolder(v : View) : RecyclerView.ViewHolder(v){
        val imageVideo = v.findViewById<ImageView>(R.id.video_image)
        val titleVideo = v.findViewById<TextView>(R.id.video_title)
        val descriptinoVideo = v.findViewById<TextView>(R.id.videoDescription)

        val thumbnailLayout = v.findViewById<LinearLayout>(R.id.videoThumbnailLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(LayoutInflater.from(activity).inflate(R.layout.video_thumbnail_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return listArticle.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {

        val videoId = listArticle[position].videoId
        val videoTitle = listArticle[position].videoTitle
        val videoImage = listArticle[position].videoUrl

        val videoDescription = listArticle[position].videoDesc



        holder.titleVideo.text = videoTitle

        holder.descriptinoVideo.text = videoDescription

        Picasso.get().load("https://images.unsplash.com/photo-1494253109108-2e30c049369b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80").into(holder.imageVideo)




        holder.thumbnailLayout.setOnClickListener {
            val articleIntent = Intent(activity, ArticleActivity::class.java)
            articleIntent.putExtra("videoId", videoId)
            activity.startActivity(articleIntent)
        }
    }




}