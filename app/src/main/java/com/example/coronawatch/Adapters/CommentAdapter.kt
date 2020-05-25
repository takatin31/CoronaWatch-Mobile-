package com.example.coronawatch.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.DataClasses.Comment
import com.example.coronawatch.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


class CommentAdapter (val activity: FragmentActivity, val listComments : ArrayList<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var userName = v.findViewById<TextView>(R.id.userName)
        var userImage = v.findViewById<CircleImageView>(R.id.userImage)
        var commentDate = v.findViewById<TextView>(R.id.commentDate)
        var commentContent = v.findViewById<TextView>(R.id.commentContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.comment_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listComments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = listComments[position]
        holder.userName.text = ArabicController.decode_str(comment.userName)
        holder.commentContent.text = ArabicController.decode_str(comment.content)
        holder.commentDate.text = comment.date
        Picasso.get().load(comment.userPic).into(holder.userImage)
    }


}



