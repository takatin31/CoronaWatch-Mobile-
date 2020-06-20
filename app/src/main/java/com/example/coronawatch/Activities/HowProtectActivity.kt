package com.example.coronawatch.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coronawatch.Adapters.ImageViewAdapter
import com.example.coronawatch.DataClasses.Image
import com.example.coronawatch.R
import kotlinx.android.synthetic.main.activity_how_protect.*

class HowProtectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_protect)

        var array = arrayListOf(
            Image(R.drawable.un),
            Image(R.drawable.deux),
            Image(R.drawable.trois),
            Image(R.drawable.quatre)
            )

        val adapter = ImageViewAdapter(this, array)
        liste.adapter = adapter

        returnBtn.setOnClickListener {
            finish()
        }
    }


}
