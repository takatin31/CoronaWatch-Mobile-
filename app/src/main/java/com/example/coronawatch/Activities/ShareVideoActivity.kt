package com.example.coronawatch.Activities

import android.R.attr.bitmap
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.R
import com.example.coronawatch.Request.FileDataPart
import com.example.coronawatch.Request.FileUploadRequest
import kotlinx.android.synthetic.main.activity_share_video.*
import org.json.JSONObject
import java.io.IOException
import java.nio.ByteBuffer


class ShareVideoActivity : AppCompatActivity() {

    private val GALLERY = 1
    private var CAMERA = 2
    private lateinit var mediaController: MediaController
    var uri: Uri? = null
    private var imageData: ByteArray? = null
    var cpt = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_video)
        val postURL = "http://192.168.1.55:8081/api/v0/video/upload-video"

        returnBtn.setOnClickListener {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
        }

        addVideoBtn.setOnClickListener {
            showPictureDialog()
        }

        deleteVideoBtn.setOnClickListener {
            videoView.suspend()
            videoView.visibility = View.GONE
            deleteVideoBtn.visibility = View.GONE
            addVideoBtn.visibility = View.VISIBLE
        }

        shareBtn.setOnClickListener {
            progressBarSending.visibility = View.VISIBLE
            createImageData(uri!!)
            uploadVideo(postURL)
        }
    }

    private fun showPictureDialog() {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
            "Select video from gallery",
            "Record video from camera"
        )
        pictureDialog.setItems(pictureDialogItems,
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> chooseVideoFromGallary()
                    1 -> takeVideoFromCamera()
                }
            })
        pictureDialog.show()
    }

    fun chooseVideoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takeVideoFromCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("result", "" + resultCode)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY) {
            Log.d("what", "gale")
            if (data != null) {
                uri = data.data
                val selectedVideoPath: String = getPath(uri)
                Log.d("path", selectedVideoPath)
                videoView.visibility = View.VISIBLE
                deleteVideoBtn.visibility = View.VISIBLE
                addVideoBtn.visibility = View.GONE
                mediaController = MediaController(this)
                videoView.setMediaController(mediaController)
                videoView.setVideoURI(uri)
                videoView.requestFocus()


            }
        } else if (requestCode == CAMERA) {
            if (data != null) {
                uri = data.data
                val recordedVideoPath: String = getPath(uri)

                videoView.visibility = View.VISIBLE
                deleteVideoBtn.visibility = View.VISIBLE
                addVideoBtn.visibility = View.GONE
                mediaController = MediaController(this)
                videoView.setMediaController(mediaController)
                videoView.setVideoURI(uri)
                videoView.requestFocus()

            }

        }else {
            Log.d("what", "cancel")
            return
        }
    }

    fun getPath(uri: Uri?): String {
        val projection =
            arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) { // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else ""
    }

    private fun uploadVideo(postURL : String) {

        imageData?: return
        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                Log.i("response", it.statusCode.toString() + "   "+it.data)
                val responseString = String(it.data)
                val responseObject = JSONObject(responseString)
                if (responseObject.getString("message") == "success"){
                    progressBarSending.visibility = View.GONE

                    Toast.makeText(this, "تم رفع الفيديو بنجاح", Toast.LENGTH_LONG).show()
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                }

            },
            Response.ErrorListener {

            }
        ) {


            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()


                val theString = "{\"utilisateurUtilisateurId\":\"1\"," +
                        "\"titre\":\"${ArabicController.encode_str(aVideoTitle.text.toString())}\"," +
                        "\"description\":\"${ArabicController.encode_str(descriptionVideo.text.toString())}\"" +
                        "}"

                params["jsonData"] = theString
                Log.i("jsonData", theString)

                return params
            }

            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["videoFile"] =
                    FileDataPart(
                        ".mp4",
                        imageData!!,
                        "mp4"
                    )

                return params
            }

        }
        Volley.newRequestQueue(this).add(request)
    }

    fun uploadThumbnail(urlVideo : String, idVideo : Int){
        val postURL: String = "${resources.getString(R.string.host)}/api/v0/video/upload-thambnial/$idVideo"
        Log.i("videoUrlll", urlVideo)
        val imgBitmap = retriveVideoFrameFromVideo(urlVideo)
        if (imgBitmap != null){

            val size: Int = imgBitmap.getRowBytes() * imgBitmap.getHeight()
            val byteBuffer: ByteBuffer = ByteBuffer.allocate(size)
            imgBitmap.copyPixelsToBuffer(byteBuffer)
            val byteArray = byteBuffer.array()

            val request = object : FileUploadRequest(
                Method.POST,
                postURL,
                Response.Listener {

                },
                Response.ErrorListener {
                }
            ) {
                override fun getByteData(): MutableMap<String, FileDataPart> {
                    var params = HashMap<String, FileDataPart>()
                    params["videoFile"] =
                        FileDataPart(
                            "image.png",
                            byteArray,
                            "png"
                        )
                    return params
                }
            }
            Volley.newRequestQueue(this).add(request)
        }


    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
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
    }
}
