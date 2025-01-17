package com.example.coronawatch.Activities


import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.R
import com.example.coronawatch.Request.FileUploadRequest
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class LoginActivity : AppCompatActivity() {
    private var callbackManager: CallbackManager? = null
    val RC_SIGN_IN = 101
    val GOOGLE_PERMISSION = 204
    lateinit var gso : GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
        val connected = pref.getBoolean("isConnected", false)

        if (connected){
            var home_intent = Intent(this, HomeActivity::class.java)
            startActivity(home_intent)
        }else{
            gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id_google_auth))
                    .requestEmail()
                    .build()


            var facebook_btn = facebook_login
            var google_btn = google_login

            google_btn.setOnClickListener {
                googleSignIn()
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
                            loginUserFacebook(accessToken.token)
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
    }

    private fun googleSignIn(){
        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null){
            GoogleSignIn.getClient(this, gso).signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        if (resultCode == RESULT_OK) {
            if (GOOGLE_PERMISSION == requestCode) {
                googleSignIn()
            }
        }
    }

    private fun loginUserFacebook(extern_token : String){
        var postURL = "${resources.getString(R.string.host)}/api/v0/auth/authUtilisateur/facebook/login"

        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                Log.i("success", "user Loged in")
                var responseString = String(it.data)
                var jsonResponse = JSONObject(responseString)
                var auth = jsonResponse.getBoolean("auth")
                if (auth){
                    saveUser(jsonResponse, "facebook")
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

    private fun loginUserGoogle(extern_token : String){
        var postURL = "${resources.getString(R.string.host)}/api/v0/auth/authUtilisateur/google/login-id"


        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                Log.i("success", "user Loged in")
                var responseString = String(it.data)
                var jsonResponse = JSONObject(responseString)
                var auth = jsonResponse.getBoolean("auth")
                if (auth){
                    saveUser(jsonResponse, "google")
                    var home_intent = Intent(this, HomeActivity::class.java)
                    startActivity(home_intent)
                    finish()
                }
            },
            Response.ErrorListener {
                //val err = String(it.networkResponse.data)
                Log.i("response", it.toString())
                Log.i("error", "error while logging")
            }
        ){

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val dataOutputStream = DataOutputStream(byteArrayOutputStream)
                val jsonUser = JSONObject()
                jsonUser.put("id_token", extern_token)
                dataOutputStream.writeBytes(jsonUser.toString())
                return byteArrayOutputStream.toByteArray()
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun saveUser(userJsonObject : JSONObject, method: String){

        Log.i("userToken", userJsonObject.getString("token"))

        val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
        val editor = pref.edit()

        editor.putBoolean("isConnected", true)
        editor.putString("method", method)

        editor.putString("token", userJsonObject.getString("token"))
        val user = userJsonObject.getJSONObject("user")
        editor.putInt("userId", user.getInt("id"))
        editor.putString("userEmail", user.getString("email"))
        editor.putString("userNom", ArabicController.decode_str(user.getString("nom")))
        editor.putString("userPrenom", ArabicController.decode_str(user.getString("prenom")))
        editor.putString("userName", ArabicController.decode_str(user.getString("username")))
        var urlResponse = user.getString("profileImageUrl")
        if (urlResponse != "" && urlResponse != "null"){
            if (!urlResponse.contains("https")){
                urlResponse = resources.getString(R.string.host) + "/"+ urlResponse
            }
        }

        editor.putString("userPic", urlResponse)

        var birthDate = user.getString("dateNaissance")
        if (birthDate != "null" && birthDate != ""){
            var localDateTime: LocalDateTime = LocalDateTime.parse(birthDate.replace("Z", ""))
            var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            birthDate = formatter.format(localDateTime)
        }

        editor.putString("userBirth", birthDate)
        editor.putString("userGender", ArabicController.decode_str(user.getString("gender")))

        editor.commit()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Log.i("accouuuuu", account.toString())
            Log.i("token", account!!.idToken)
            val token = account!!.idToken

            if (token != null){
                loginUserGoogle(token)
            }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(
                "Logging failed",
                "signInResult:failed code=" + e.statusCode
            )
            Toast.makeText(this, "Hi there! This is a Error", Toast.LENGTH_SHORT).show()

        }
    }
}

