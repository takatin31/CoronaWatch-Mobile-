package com.example.coronawatch.DataClasses

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import java.io.Serializable

class VideoThumbnail(val videoId : Int, val videoUrl : String, val videoTitle : String, val videoDesc : String, val date : String, var bitmap : Boolean) : Serializable {

    /*fun getBitmap(){
        try {
            val bitmap =
                retriveVideoFrameFromVideo(videoUrl)
            if (bitmap != null) {
                this.bitmap = bitmap
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap())
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }*/

}