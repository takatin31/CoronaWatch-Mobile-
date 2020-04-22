package com.example.coronawatch



import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.facebook.internal.Mutable
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import kotlinx.android.synthetic.main.activity_stats.*
import kotlinx.android.synthetic.main.fragment_map.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class StatsActivity : AppCompatActivity() {

    var dataHistory = mutableListOf<DailyData>()
    var options = arrayListOf("cases", "deaths", "recovered")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        var isCountry = intent.getBooleanExtra("isCountry", false)




        if (isCountry){
            var countryCode = intent.getStringExtra("countryCode")
            var countryName = intent.getStringExtra("countryName")
            countryNameView.text = countryName
            getCountryData(countryCode)
        }else{
            var zoneId = intent.getIntExtra("zoneId", -1)
            getZoneData(zoneId)
        }





        initComoboxes()

    }

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
                    var dailyData = DailyData(date, nbrCases, nbrDeaths, nbrRecovered)
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
                }

                nbrCasesView.text = nbrCases.toString()
                nbrDeathsView.text = nbrDeaths.toString()
                nbrCaredView.text = nbrCared.toString()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestNbrDeaths)
    }


    private fun getZoneData(zoneId : Int){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/$zoneId"


        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                var items = response.getJSONArray("dataZones")
                countryNameView.text = response.getString("city")
                val count = items.length()
                for (i in 0 until count){
                    var item = items.getJSONObject(i)
                    var stringDate = item.getString("dateDataZone").split("T")[0].split("-")
                    var date = LocalDate.of(stringDate[0].toInt(), stringDate[1].toInt(), stringDate[2].toInt())
                    var nbrCases = item.getInt("totalConfirmed")
                    var nbrDeaths = item.getInt("totalDead")
                    var nbrRecovered = item.getInt("totalRecovered")
                    var dailyData = DailyData(date, nbrCases, nbrDeaths, nbrRecovered)
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
                }

                nbrCasesView.text = nbrCases.toString()
                nbrDeathsView.text = nbrDeaths.toString()
                nbrCaredView.text = nbrCared.toString()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestNbrDeaths)
    }

    private fun setCharts(option : String, idChart : Int) {


        if (idChart == 0){

            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()
            var i = 2f
            for (data in dataHistory){
                entries.add(BarEntry(i, data.getOptionData(option).toFloat()))
                labels.add(data.date.toString())
                i += 2
            }

            val barDataSet = BarDataSet(entries, "Cells")


            barChart.setDrawGridBackground(false)

            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)


            val data = BarData(barDataSet)


            barChart.data = data // set the data and list of lables into chart


            //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
            barDataSet.color = resources.getColor(R.color.mapbox_blue)

            barChart.animateY(1000)

            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            barChart.description.isEnabled = false
            barChart.description.textSize = 0f
            barChart.axisLeft.axisMinimum = 0f
            barChart.axisRight.axisMinimum = 0f
            barChart.data.isHighlightEnabled = false
            barChart.invalidate()
            barChart.axisLeft.setDrawGridLines(false)
            barChart.xAxis.setDrawGridLines(false)
            barChart.axisRight.isEnabled = false
        }else{

            val Lentries = ArrayList<Entry>()
            Lentries.add(BarEntry(0f, 0f))
            val labels = ArrayList<String>()
            var i = 2f
            for (data in dataHistory){
                Lentries.add(BarEntry(i, data.getOptionData(option).toFloat()))
                labels.add(data.date.toString())
                i += 2
            }


            val lineDataSet = LineDataSet(Lentries, "Cells")

            val Ldata = LineData(lineDataSet)

            lineDataSet.color = resources.getColor(R.color.darkblue)


            lineChart.data = Ldata // set the data and list of lables into chart


            lineChart.animateY(1000)

            lineChart.description.isEnabled = false
            lineChart.description.textSize = 0f
            lineChart.axisLeft.axisMinimum = 0f
            lineChart.axisRight.axisMinimum = 0f
            lineChart.data.isHighlightEnabled = false
            lineChart.invalidate()
            lineChart.axisLeft.setDrawGridLines(false)
            lineChart.xAxis.setDrawGridLines(false)
            lineChart.axisRight.isEnabled = false
        }





    }

    private fun initComoboxes(){
        val spinnerBarChart: Spinner = spinnerBarChartView
        val spinnerLineChart: Spinner = spinnerLineChartView

        ArrayAdapter.createFromResource(
            this,
            R.array.graph_menu,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
