package com.example.coronawatch.Adapters


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.coronawatch.Activities.VideoActivity
import com.example.coronawatch.DataClasses.VideoThumbnail
import com.example.coronawatch.R
import com.squareup.picasso.Picasso
import java.io.File
import java.io.OutputStream


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
        val video = listArticle[position]
        val videoId = listArticle[position].videoId
        val videoTitle = listArticle[position].videoTitle
        val videoImage = listArticle[position].bitmap

        val videoDescription = listArticle[position].videoDesc

       /* if (videoImage == null){
            video.getBitmap()
        }*/

        holder.titleVideo.text = videoTitle

        holder.descriptinoVideo.text = videoDescription



        /*if (videoImage != null){
            loadBitmapByPicasso(activity, videoImage, holder.imageVideo)
        }*/

        holder.thumbnailLayout.setOnClickListener {
            val videoIntent = Intent(activity, VideoActivity::class.java)
            videoIntent.putExtra("video", listArticle[position])
            activity.startActivity(videoIntent)
        }
    }


    private fun loadBitmapByPicasso(
        pContext: Context,
        pBitmap: Bitmap,
        pImageView: ImageView
    ) {
        try {
            val uri: Uri =
                Uri.fromFile(File.createTempFile("temp_file_name", ".jpg", pContext.getCacheDir()))
            val outputStream: OutputStream? = pContext.getContentResolver().openOutputStream(uri)
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream!!.close()
            Picasso.get().load(uri).into(pImageView)
        } catch (e: java.lang.Exception) {
            Log.e("LoadBitmapByPicasso", e.message)
        }
    }




}