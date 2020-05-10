package com.example.coronawatch.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.blongho.country_data.World
import com.example.coronawatch.DataClasses.DailyData
import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.activity_stats.*
import java.time.LocalDate


class StatsActivity : AppCompatActivity() {

    var dataHistory = mutableListOf<DailyData>()
    var options = arrayListOf("cases", "deaths", "recovered")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        World.init(this)

        var isCountry = intent.getBooleanExtra("isCountry", false)
        val isRiskZone = intent.getBooleanExtra("isDangerZone", false)

        if (isRiskZone){
            dangerZoneView.visibility = View.VISIBLE
            val riskZoneId = intent.getIntExtra("riskZoneId", -1)
            val degre = intent.getIntExtra("degre", 0)
            val reason = intent.getStringExtra("reason")
            degreValueView.text = degre.toString()
            reasonValueView.text = reason

            if (degre == 1){
                dataView.visibility = View.GONE
                chartsScrollView.visibility = View.GONE
            }

            getRiskZoneData(riskZoneId)
        }else{
            dangerZoneView.visibility = View.GONE
            if (isCountry){
                var countryCode = intent.getStringExtra("countryCode")
                var countryName = intent.getStringExtra("countryName")
                val flag : Int = World.getFlagOf(countryCode)
                countryImageView.setImageResource(flag)
                countryNameView.text = countryName
                getCountryData(countryCode)
            }else{
                var zoneId = intent.getIntExtra("zoneId", -1)
                getZoneData(zoneId)
            }
            initComoboxes()
        }





    }


    //recuperer les données d'un pays
    private fun getCountryData(countryCode : String){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/historyByCountry?cc=$countryCode"

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                var count : Int = response.getInt("count")
                var items = response.getJSONArray("items")

                for (i in 0 until count){
                    var item = items.getJSONObject(i)
                    var stringDate = item.getString("date").replace("O", "").split("-")
                    Log.i("stringDate", item.getString("date"))
                    var date = LocalDate.of(stringDate[0].toInt(), stringDate[1].toInt(), stringDate[2].toInt())
                    Log.i("date date", date.toString())
                    var data = item.getJSONArray("items").getJSONObject(0)
                    var nbrCases = data.getInt("totalConfirmed")
                    var nbrDeaths = data.getInt("totalDead")
                    var nbrRecovered = data.getInt("totalRecovered")
                    var dailyData =
                        DailyData(
                            date,
                            nbrCases,
                            nbrDeaths,
                            nbrRecovered
                        )
                    dataHistory.add(dailyData)
                }

                dataHistory.sort()
                setCharts(options[0], 0)
                setCharts(options[0], 1)

                var nbrCases = 0
                var nbrDeaths = 0
                var nbrCared = 0

                if (dataHistory.size > 0){
                    nbrCases = dataHistory.last().cases
                    nbrDeaths = dataHistory.last().deads
                    nbrCared = dataHistory.last().recovered
                    lastUpdateView.text = dataHistory.last().date.toString()
                }

                nbrCasesView.text = nbrCases.toString()
                nbrDeathsView.text = nbrDeaths.toString()
                nbrCaredView.text = nbrCared.toString()
            },
            Response.ErrorListener {
                Log.d("Error", "Request error")
                nbrCasesView.text = "0"
                nbrDeathsView.text = "0"
                nbrCaredView.text = "0"
            })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestNbrDeaths)
    }

    private fun getRiskZoneData(riskZoneId : Int){
        Log.i("thisiszonerisk", riskZoneId.toString())
        val urlRiskZoneData = "${resources.getString(R.string.host)}/api/v0/zoneRisque/$riskZoneId"

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlRiskZoneData, null,
            Response.Listener { response ->
                val item = response.getJSONObject("item")
                val zoneData = item.getInt("zoneZoneId")
                val cause = item.getString("cause")
                val degre = item.getInt("degre")
                if (degre > 1){
                    getZoneData(zoneData)
                }else{
                    var stringDate = item.getString("updatedAt").split("T")[0].split("-")
                    var date = LocalDate.of(stringDate[0].toInt(), stringDate[1].toInt(), stringDate[2].toInt())
                    lastUpdateView.text = date.toString()
                    val countryCode : String = item.getJSONObject("zone").getString("counrtyCode")
                    val flag : Int = World.getFlagOf(countryCode)
                    countryImageView.setImageResource(flag)
                    countryNameView.text = item.getJSONObject("zone").getString("city")
                }

            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestNbrDeaths)
    }

    //recuperer les données sur une zone
    private fun getZoneData(zoneId : Int){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/$zoneId"

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                var items = response.getJSONArray("dataZones")
                countryNameView.text = response.getString("city")
                val countryCode : String = response.getString("counrtyCode")
                val flag : Int = World.getFlagOf(countryCode)
                countryImageView.setImageResource(flag)
                val count = items.length()
                for (i in 0 until count){
                    var item = items.getJSONObject(i)
                    var stringDate = item.getString("dateDataZone").split("T")[0].split("-")
                    var date = LocalDate.of(stringDate[0].toInt(), stringDate[1].toInt(), stringDate[2].toInt())
                    var nbrCases = item.getInt("totalConfirmed")
                    var nbrDeaths = item.getInt("totalDead")
                    var nbrRecovered = item.getInt("totalRecovered")
                    var dailyData =
                        DailyData(
                            date,
                            nbrCases,
                            nbrDeaths,
                            nbrRecovered
                        )
                    dataHistory.add(dailyData)
                }

                dataHistory.sort()
                setCharts(options[0], 0)
                setCharts(options[0], 1)

                var nbrCases = 0
                var nbrDeaths = 0
                var nbrCared = 0

                if (dataHistory.size > 0){
                    nbrCases = dataHistory.last().cases
                    nbrDeaths = dataHistory.last().deads
                    nbrCared = dataHistory.last().recovered
                    lastUpdateView.text = dataHistory.last().date.toString()
                }

                nbrCasesView.text = nbrCases.toString()
                nbrDeathsView.text = nbrDeaths.toString()
                nbrCaredView.text = nbrCared.toString()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestNbrDeaths)
    }

    //afficher les graphes (barchart et linechart)
    private fun setCharts(option : String, idChart : Int) {

        val idColor = getOptionColor(option)

        if (idChart == 0){
            //cette partie concerne le graph barchart
            // on recupere et on affiche les donnes
            val entries = ArrayList<BarEntry>()
            var i = 0f
            for (data in dataHistory){
                entries.add(BarEntry(i, data.getOptionData(option).toFloat()))
                Log.i("dateee", data.date.toString())
                i += 1
            }

            val barDataSet = BarDataSet(entries, "Cells")

            barChart.setDrawGridBackground(false)

            val data = BarData(barDataSet)

            barChart.data = data // set the data and list of lables into chart

            //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
            barDataSet.color = ContextCompat.getColor(this, idColor)

            barChart.animateY(1000)

            barChart.description.isEnabled = false
            barChart.description.textSize = 0f
            barChart.axisLeft.axisMinimum = 0f
            barChart.axisRight.axisMinimum = 0f
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barChart.data.isHighlightEnabled = false
            barChart.invalidate()
            barChart.axisLeft.setDrawGridLines(false)
            barChart.xAxis.setDrawGridLines(false)
            barChart.axisRight.isEnabled = false
        }else{
            //cette partie concerne le graph linechart
            // on recupere et on affiche les donnes
            val lentries = ArrayList<Entry>()
            val labels = ArrayList<String>()
            lentries.add(BarEntry(0f, 0f))
            var i = 2f
            for (data in dataHistory){
                lentries.add(BarEntry(i, data.getOptionData(option).toFloat()))
                i += 1
            }

            val lineDataSet = LineDataSet(lentries, "Cells")

            lineDataSet.enableDashedLine(10f, 5f, 0f)

            val Ldata = LineData(lineDataSet)

            lineDataSet.color = ContextCompat.getColor(this, idColor)

            lineDataSet.fillColor = ContextCompat.getColor(this, idColor)

            lineDataSet.setDrawFilled(true)

            lineChart.data = Ldata // set the data and list of lables into chart

            lineChart.animateY(1000)

            lineChart.description.isEnabled = false
            lineChart.description.textSize = 0f
            lineChart.axisLeft.axisMinimum = 0f
            lineChart.axisRight.axisMinimum = 0f
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.data.isHighlightEnabled = false
            lineChart.invalidate()
            lineChart.axisLeft.setDrawGridLines(false)
            lineChart.xAxis.setDrawGridLines(false)
            lineChart.axisRight.isEnabled = false
        }

    }


    //recuperer la couleur selon l'option chosiie
    private fun getOptionColor(option : String) : Int {
        return when (option) {
            "cases" -> {
                R.color.cases_color
            }
            "deaths" -> {
                R.color.deaths_color
            }
            else -> {
                R.color.recovered_color
            }
        }
    }

    //initialiser les combobox
    private fun initComoboxes(){
        val spinnerBarChart: Spinner = spinnerBarChartView
        val spinnerLineChart: Spinner = spinnerLineChartView

        ArrayAdapter.createFromResource(
            this,
            R.array.graph_menu,
            R.layout.spinner_item_layout
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item_layout)
            spinnerBarChart.adapter = adapter
            spinnerLineChart.adapter = adapter
        }

        spinnerBarChart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setCharts(options[position], 0)
            }
        }

        spinnerLineChart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setCharts(options[position], 1)
            }
        }
    }
}
