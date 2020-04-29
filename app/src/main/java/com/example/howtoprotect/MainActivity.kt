package com.example.howtoprotect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var adapter: ListViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lv = findViewById(R.id.liste) as ListView
        ///////////

        var array = arrayOf(
            Image(R.drawable.first),
            Image(R.drawable.second),
            Image(R.drawable.third),
            Image(R.drawable.forth))

        imageList = ArrayList()

        for (i in array!!.indices) {
            val image = array!![i]

            imageList.add(image)
        }


        adapter = ListViewAdapter(this)
        lv.adapter = adapter





    }
    companion object {
        var imageList = ArrayList<Image>()
    }

}
