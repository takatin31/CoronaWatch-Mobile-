package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import java.util.ArrayList



class BlankFragment : Fragment(), SearchView.OnQueryTextListener {

    // Declare Variables
    private var list: ListView? = null
    private var adapter: ListViewAdapter? = null
    private var editsearch: SearchView? = null
    private var payList: Array<PayName>? = null

    override fun onQueryTextSubmit(query: String): Boolean
                 {
                    return false
                         }

    override fun onQueryTextChange(newText: String): Boolean
             {
            adapter!!.filter(newText)
             return false
               }


    override fun onCreate(savedInstanceState: Bundle?)
               {
        super.onCreate(savedInstanceState)

                }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
            ): View? {
    val  v:View= inflater.inflate(R.layout.fragment_blank, container, false)
        payList = arrayOf(
            PayName("unitedstate","80000",R.drawable.unitedstate),
            PayName("ispan","10215",R.drawable.spain),
            PayName("italy","587",R.drawable.italy),

            PayName("germany","5358",R.drawable.germany)
            ,   PayName("chaina","5436",R.drawable.chaina)
            ,  PayName("france","53436",R.drawable.france),
            PayName("iran","53653",R.drawable.iran),
            PayName("united kingdom","235",R.drawable.unitedkingdom),
            PayName("turkey","858",R.drawable.turkey)
            ,  PayName("switzerland","873",R.drawable.switzerland),
            PayName("belguim","356",R.drawable.belguim))

        // Locate the ListView in listview_main.xml
        list = v.findViewById(R.id.listview) as ListView

        BlankFragment.payeNamesArrayList = ArrayList()

        for (i in payList!!.indices)
        {
            val movieNames = payList!![i]
            // Binds all strings into an array
            BlankFragment.payeNamesArrayList.add(movieNames)
             }

        // Pass results to ListViewAdapter Class
        adapter = ListViewAdapter(context!!)

        // Binds the Adapter to the ListView
        list!!.adapter = adapter

        // Locate the EditText in listview_main.xml
        editsearch = v.findViewById(R.id.search) as SearchView
        editsearch!!.setOnQueryTextListener(this)

        list!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val itemValue = list!!.getItemAtPosition(position) as String

            val mov = payList!![position]
            val bundle = Bundle()
            //  bundle.putString("item", itemValue)
            bundle.putInt("amount", position)
            bundle.putString("name", mov.payName)
            bundle.putString("image", mov.imageName)

            val fragment = BlankFragment2()
            //
            val fragmentManager = activity!!.supportFragmentManager

            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contaner, fragment)
            fragment.arguments=bundle
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }


        return v
    }

companion object {
    var payeNamesArrayList = ArrayList<PayName>()
}
    }


