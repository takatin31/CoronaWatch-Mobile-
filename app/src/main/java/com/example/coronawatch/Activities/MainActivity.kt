package com.example.coronawatch.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coronawatch.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val start_btn = start_button

        start_btn.setOnClickListener {
            var login_intent = Intent(this, LoginActivity::class.java)
            startActivity(login_intent)
        }
    }
}
