package com.example.coronawatch.Activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.example.coronawatch.R
import com.example.coronawatch.Request.FileUploadRequest
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*


class LoginActivity : AppCompatActivity() {
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var facebook_btn = facebook_login
        var google_btn = google_login

        google_btn.setOnClickListener {
            var home_intent = Intent(this, HomeActivity::class.java)
            startActivity(home_intent)
        }


        facebook_btn.setOnClickListener {
            // Login
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        val accessToken = AccessToken.getCurrentAccessToken()
                        Log.i("tooooooken", accessToken.token)
                        loginUser(accessToken.token, "facebook")
                    }

                    override fun onCancel() {
                        Log.d("MainActivity", "Facebook onCancel.")

                    }

                    override fun onError(error: FacebookException) {
                        Log.i("erroooor", error.toString())
                        Toast.makeText(this@LoginActivity, "Hi there! This is a Error", Toast.LENGTH_SHORT).show()

                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun loginUser(extern_token : String, method : String){
        val postURL = "${resources.getString(R.string.host)}/api/v0/auth/authUtilisateur/$method/login"


        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                Log.i("success", "user Loged in")
                var responseString = String(it.data)
                var jsonResponse = JSONObject(responseString)
                var auth = jsonResponse.getBoolean("auth")
                if (auth){
                    saveUser(jsonResponse)
                    var home_intent = Intent(this, HomeActivity::class.java)
                    startActivity(home_intent)
                }
            },
            Response.ErrorListener {
                Log.i("response", it.toString())
                Log.i("error", "error while logging")
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                var headers: MutableMap<String, String> = mutableMapOf()
                headers["access_token"] = extern_token
                return headers
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val byteArrayOutputStream = ByteArrayOutputStream()
                return byteArrayOutputStream.toByteArray()
            }


        }
        Volley.newRequestQueue(this).add(request)

    }

    private fun saveUser(userJsonObject : JSONObject){

        val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
        val editor = pref.edit()

        editor.putString("token", userJsonObject.getString("token"))
        val user = userJsonObject.getJSONObject("user")
        editor.putInt("userId", user.getInt("id"))
        editor.putString("userEmail", user.getString("email"))

        editor.commit()
    }
}

