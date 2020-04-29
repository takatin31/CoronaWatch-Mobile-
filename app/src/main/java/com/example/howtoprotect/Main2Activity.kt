package com.example.howtoprotect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class Main2Activity : AppCompatActivity() {
    private var adapter: ListViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val lv = findViewById(R.id.liste) as ListView
        ///////////

        var array = arrayOf(
            Image(R.drawable.un),
            Image(R.drawable.deux),
            Image(R.drawable.trois),
            Image(R.drawable.quatre))

        MainActivity.imageList = ArrayList()

        for (i in array!!.indices) {
            val image = array!![i]

            MainActivity.imageList.add(image)
        }


        adapter = ListViewAdapter(this)
        lv.adapter = adapter





    }
    companion object {
        var imageList = ArrayList<Image>()
    }
}
