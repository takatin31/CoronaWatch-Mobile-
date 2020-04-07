package com.example.coronawatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import kotlinx.android.synthetic.main.activity_login.*


import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import java.util.*

class LoginActivity : AppCompatActivity() {
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var facebook_btn = facebook_login
        var google_btn = google_login

        google_btn.setOnClickListener {
            //var home_intent = Intent(this, HomeActivity::class.java)
            //startActivity(home_intent)
        }


        facebook_btn.setOnClickListener {
            // Login
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        // var home_intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        // startActivity(home_intent)
                    }

                    override fun onCancel() {
                        Log.d("MainActivity", "Facebook onCancel.")

                    }

                    override fun onError(error: FacebookException) {
                        Toast.makeText(this@LoginActivity, "Hi there! This is a Error", Toast.LENGTH_SHORT).show()

                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
}

