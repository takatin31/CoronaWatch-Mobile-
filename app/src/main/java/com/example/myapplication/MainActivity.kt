package com.example.myapplication


import android.content.Intent
import android.graphics.Movie
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.get

import java.util.ArrayList

class MainActivity : AppCompatActivity() {


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = BlankFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contaner, fragment)
        transaction.commit()

        }
    }


