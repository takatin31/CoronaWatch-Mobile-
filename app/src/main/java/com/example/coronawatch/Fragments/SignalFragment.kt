package com.example.coronawatch.Fragments


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.R
import com.example.coronawatch.Request.FileDataPart
import com.example.coronawatch.Request.FileUploadRequest
import kotlinx.android.synthetic.main.activity_share_video.*
import kotlinx.android.synthetic.main.fragment_signal.*
import java.io.IOException





class SignalFragment : Fragment() {

    var uri: Uri? = null
    private val IMAGE_CAPTURE_CODE = 1001
    private var imageData: ByteArray? = null
    var isVideo : Boolean = false
    // Voici l'adresse de l'api qu'on utilise pour l'upload


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var photoBtn = pictureBtnView
        var vidopBtn = videoBtnView

        photoBtn.setOnClickListener {
            if (signalTitle.text.toString() == "" || descriptionSignal.text.toString() == "" ){
                Toast.makeText(activity, "الرجاء ملء كل الخانات", Toast.LENGTH_SHORT).show()
            }else{
                isVideo = false
                openCamera(MediaStore.ACTION_IMAGE_CAPTURE)
            }

        }

        vidopBtn.setOnClickListener {
            if (signalTitle.text.toString() == "" || descriptionSignal.text.toString() == "" ){
                Toast.makeText(activity, "الرجاء ملء كل الخانات", Toast.LENGTH_SHORT).show()
            }else{
                isVideo = true
                openCamera(MediaStore.ACTION_VIDEO_CAPTURE)
            }
        }

    }

    private fun openCamera(cameraIntentString: String) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Case")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        uri = activity!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(cameraIntentString)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK){

            if (uri != null) {
                createImageData(uri!!)
                progressBar.visibility = View.VISIBLE
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        val postURL: String = "http://192.168.1.55:8081/api/v0/signalement/"
        imageData?: return
        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                progressBar.visibility = View.GONE
                successResult.visibility = View.VISIBLE
                Toast.makeText(activity, "تم الابلاغ بنجاح", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
                progressBar.visibility = View.GONE
                successResult.visibility = View.VISIBLE
                Toast.makeText(activity, "يبدو ان هناك خطبا ما", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                if (isVideo){
                    params["videoFile"] =
                        FileDataPart(
                            "file.mp4",
                            imageData!!,
                            "mp4"
                        )
                }else{
                    params["imageFile"] =
                        FileDataPart(
                            "file.png",
                            imageData!!,
                            "png"
                        )
                }

                return params
            }

            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                val pref = activity!!.getSharedPreferences(resources.getString(R.string.shared_pref),0)
                val userId = pref.getInt("userId", 0)
                val titre = ArabicController.encode_str(signalTitle.text.toString())
                val info = ArabicController.encode_str(descriptionSignal.text.toString())

                val theString = "{\"lien\": \"null\",\n" +
                        "\"source\": \"user\",\n" +
                        "\"titre\": \"$titre\",\n" +
                        "\"info\": \"$info\",\n" +
                        "\"imageUrl\": \"null\",\n" +
                        "\"videoUrl\": \"null\",\n" +
                        "\"status\": \"signaled\",\n" +
                        "\"utilisateurUtilisateurId\": $userId}"

                params["jsonData"] = theString
                Log.i("jsonData", theString)

                return params
            }
        }
        Volley.newRequestQueue(activity).add(request)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = activity!!.contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }



}
