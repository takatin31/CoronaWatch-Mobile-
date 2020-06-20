package com.example.coronawatch.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.coronawatch.DataClasses.Post
import com.example.coronawatch.R
import com.squareup.picasso.Picasso


class PostAdapter(val activity: FragmentActivity, val listPost : ArrayList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    class PostNoImageViewHolder(v : View) : RecyclerView.ViewHolder(v){
        val sourceView = v.findViewById<TextView>(R.id.postSrcView)
        val titleView = v.findViewById<TextView>(R.id.postTitleView)
        val resumeView = v.findViewById<TextView>(R.id.postResumeView)
        val linkView = v.findViewById<TextView>(R.id.postLinkView)
    }

    class PostWithImageViewHolder(v : View) : RecyclerView.ViewHolder(v){
        val sourceView = v.findViewById<TextView>(R.id.postSrcView)
        val titleView = v.findViewById<TextView>(R.id.postTitleView)
        val resumeView = v.findViewById<TextView>(R.id.postResumeView)
        val linkView = v.findViewById<TextView>(R.id.postLinkView)
        val dateView = v.findViewById<TextView>(R.id.postDateView)
        val imgView = v.findViewById<ImageView>(R.id.postImgView)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                return PostNoImageViewHolder(
                    LayoutInflater.from(activity).inflate(R.layout.post_no_image_layout, parent, false)
                )
            }
            1 -> {
                return PostWithImageViewHolder(
                    LayoutInflater.from(activity).inflate(R.layout.post_image_layout, parent, false)
                )
            }
            else -> return PostNoImageViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.post_no_image_layout, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return listPost.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = listPost[position]

        when (post.source) {
            "Facebook" -> {
                val noImgHolder = holder as PostNoImageViewHolder
                noImgHolder.titleView.text = post.title
                noImgHolder.sourceView.text = post.source
                noImgHolder.linkView.text = post.lien
                noImgHolder.resumeView.text = post.resume
            }
            else -> {
                val imgHolder = holder as PostWithImageViewHolder
                imgHolder.titleView.text = post.title
                imgHolder.sourceView.text = post.source
                imgHolder.linkView.text = post.lien
                imgHolder.resumeView.text = post.resume
                if (post.date != null){
                    imgHolder.dateView.text = post.date
                }
                if (post.img != null){
                    Picasso.get().load(post.img).into(imgHolder.imgView)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(listPost[position].source){
            "Facebook" -> {
                0
            }
            else -> {
                1
            }
        }
    }


}