package com.example.coronawatch.Activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.blongho.country_data.World
import com.example.coronawatch.Adapters.CountryAdapter
import com.example.coronawatch.DataClasses.Country
import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import kotlinx.android.synthetic.main.activity_countries.*
import java.util.*

class CountriesActivity : AppCompatActivity() {

    lateinit var adapter: CountryAdapter
    lateinit var layoutManager : LinearLayoutManager
    var countriesList = mutableListOf<Country>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countries)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = CountryAdapter(this, countriesList)
        recyclerView.adapter = adapter

        World.init(this)

        initData()

        returnBtn.setOnClickListener {
            finish()
        }
    }

    fun initData(){
        val urlRiskZoneData = "${resources.getString(R.string.host)}/api/v0/zone/groupByCountry?sort=infected"

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlRiskZoneData, null,
            Response.Listener { response ->
                val items = response.getJSONArray("items")
                for (i in 0 until items.length()){
                    val item = items.getJSONObject(i)
                    val countryCode = item.getString("counrtyCode")
                    val nbrCases = item.getInt("totalConfirmed")
                    val loc = Locale("ar", countryCode)
                    val countryName = loc.displayCountry
                    countriesList.add(Country(countryCode, countryName, nbrCases))
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestNbrDeaths)
    }
}
