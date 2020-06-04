package com.example.coronawatch.Activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.R
import com.example.coronawatch.Request.FileDataPart
import com.example.coronawatch.Request.FileUploadRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.`userImageٍView`
import kotlinx.android.synthetic.main.activity_profile.returnBtn
import kotlinx.android.synthetic.main.activity_profile.userNameView
import kotlinx.android.synthetic.main.activity_share_video.*
import kotlinx.android.synthetic.main.drawer_header.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.fragment_signal.*
import kotlinx.android.synthetic.main.home_layout.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class ProfileActivity : AppCompatActivity() {

    var uri: Uri? = null
    val IMAGE_CAPTURE_CODE = 1001
    private var imageData: ByteArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initUserData()

        ArrayAdapter.createFromResource(
            this,
            R.array.gender_list,
            R.layout.spinner_item_layout
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item_layout)
            spinnerGender.adapter = adapter
        }

        userBirthDateView.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var dayS = "$dayOfMonth"
                var monthS = "${monthOfYear+1}"
                if (dayOfMonth < 10){
                    dayS = "0$dayOfMonth"
                }

                if (monthOfYear < 9){
                    monthS = "0${monthOfYear+1}"
                }

                userBirthDateView.setText(""+ year + "-"+ monthS + "-" + dayS )
            }, year, month, day)

            dpd.show()
        }

        userImageContainer.setOnClickListener {
            showPictureDialog()
            //openCamera(MediaStore.ACTION_IMAGE_CAPTURE)
        }

        returnBtn.setOnClickListener {
            finish()
        }

        saveBtn.setOnClickListener {
            val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
            val userId = pref.getInt("userId", -1)
            saveUserData(userId)
        }
    }

    fun initUserData(){
        val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
        val userNom = pref.getString("userNom", "")
        val userPrenom = pref.getString("userPrenom", "")
        val userName = pref.getString("userName", "")
        val userBirthDate = pref.getString("userBirth", "")
        val userGender = pref.getString("userGender", "")
        val userPic = pref.getString("userPic", "")

        userNomView.setText(userNom)
        userPrenomView.setText(userPrenom)
        userNameView.setText(userName)
        userBirthDateView.setText(userBirthDate)

        val genderIndex = resources.getStringArray(R.array.gender_list).indexOf(userGender)
        if (genderIndex > 0){
            spinnerGender.setSelection(genderIndex)
        }

        if (userPic != ""){
            Picasso.get().load(userPic).into(`userImageٍView`)
        }
    }

    fun saveUserData(userId : Int){
        //val postURL = "https://ptsv2.com/t/4qqou-1590430852/post"
        val postURL = "${resources.getString(R.string.host)}/api/v0/utilisateur/$userId"
        val userName = ArabicController.encode_str(userNameView.text.toString())
        val nom = ArabicController.encode_str(userNomView.text.toString())
        val prenom = ArabicController.encode_str(userPrenomView.text.toString())
        val birthDate = userBirthDateView.text.toString()
        val gender = ArabicController.encode_str(spinnerGender.selectedItem.toString())
        Log.i("daaaaaaaaaate", birthDate)
        val request = object : FileUploadRequest(
            Method.PATCH,
            postURL,
            Response.Listener {
                var responseString = String(it.data)
                var jsonResponse = JSONObject(responseString)
                var msg = jsonResponse.getString("message")
                if (msg == "success"){
                    Log.i("success", "user_updated")
                    val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
                    val editor = pref.edit()
                    editor.putString("userNom", userName)
                    editor.putString("userPrenom", prenom)
                    editor.putString("userName", nom)
                    editor.putString("userBirth", birthDate)
                    editor.putString("userGender", gender)
                    editor.commit()
                }

            },
            Response.ErrorListener {
                val err = String(it.networkResponse.data)
                Log.i("errrrrrrrrrrr", err)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                var headers: MutableMap<String, String> = mutableMapOf()
                headers["Accept"] = "*/*"
                headers["Cache-Control"] = "no-cache"
                return headers
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val dataOutputStream = DataOutputStream(byteArrayOutputStream)
                val jsonUser = JSONObject()
                jsonUser.put("username", userName)
                jsonUser.put("nom", nom)
                jsonUser.put("prenom", prenom)
                jsonUser.put("gender", gender)
                jsonUser.put("dateNaissance", birthDate)
                dataOutputStream.writeBytes(jsonUser.toString())
                return byteArrayOutputStream.toByteArray()
            }

        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun showPictureDialog() {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
            "Select image from gallery",
            "Record image from camera"
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
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, IMAGE_CAPTURE_CODE)
    }

    private fun takeVideoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, IMAGE_CAPTURE_CODE)
    }


    private fun openCamera(cameraIntentString: String) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Case")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(cameraIntentString)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //called when image was captured from camera intent
        Log.i("okee", "oeeeeeeeenoo"+ resultCode)
        if (resultCode == Activity.RESULT_OK){
            Log.i("okee", "oeeeeeeeennnn")
            if (data != null) {
                Log.i("okeeeee", data.toString())
                uri = data.data
                createImageData(uri!!)
                val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
                val userId = pref.getInt("userId", -1)
                if (userId != -1){
                    uploadImage(userId)
                }else{
                    Toast.makeText(this, "حدث خلل في التطبيق", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun uploadImage(userId : Int) {
        val postURL: String = "${resources.getString(R.string.host)}/api/v0/utilisateur/profile-image/$userId"
        imageData?: return
        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                var responseString = String(it.data)
                var jsonResponse = JSONObject(responseString)
                var msg = jsonResponse.getString("message")
                if (msg == "success"){
                    val urlResponse = jsonResponse.getJSONObject("content").getString("url")
                    val imgUrl = resources.getString(com.example.coronawatch.R.string.host) + "/"+ urlResponse
                    Log.i("picurrrrl", imgUrl)
                    Picasso.get().load(imgUrl).into(`userImageٍView`)
                    val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
                    val editor = pref.edit()

                    editor.putString("userPic", imgUrl)
                    editor.commit()
                }

            },
            Response.ErrorListener {
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["profilePictureFile"] =
                    FileDataPart(
                        "image.png",
                        imageData!!,
                        "png"
                    )
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }
}
