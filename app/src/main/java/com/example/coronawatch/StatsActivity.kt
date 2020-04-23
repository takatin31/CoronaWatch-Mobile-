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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        var countryCode = intent.getStringExtra("countryCode")
        var countryName = intent.getStringExtra("countryName")



        getCountryData(countryCode)

        countryNameView.text = countryName

        setBarChart()

        initComoboxes()

    }

    private fun getCountryData(countryCode : String){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/historyByCountry?cc=$countryCode"
        var dataHistory = mutableListOf<DailyData>()

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                var count : Int = response.getInt("count")
                var items = response.getJSONArray("items")

                for (i in 0 until count){
                    var item = items.getJSONObject(i)
                    Log.i("daaaate", item.get("date").toString())
                    var stringDate = item.getString("date").split("-")
                    var date = LocalDate.of(stringDate[0].toInt(), stringDate[1].toInt(), stringDate[2].toInt())

                    var data = item.getJSONArray("items").getJSONObject(0)
                    var nbrCases = data.getInt("totalConfirmed")
                    var nbrDeaths = data.getInt("totalDead")
                    var nbrRecovered = data.getInt("totalRecovered")
                    var dailyData = DailyData(date, nbrCases, nbrDeaths, nbrRecovered)
                    dataHistory.add(dailyData)
                }

                dataHistory.sort()

                var nbrCases = dataHistory.last().cases
                var nbrDeaths = dataHistory.last().deads
                var nbrCared = dataHistory.last().recovered



                nbrCasesView.text = nbrCases.toString()
                nbrDeathsView.text = nbrDeaths.toString()
                nbrCaredView.text = nbrCared.toString()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestNbrDeaths)
    }

    private fun setBarChart() {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(8f, 0f))
        entries.add(BarEntry(2f, 1f))
        entries.add(BarEntry(5f, 2f))
        entries.add(BarEntry(20f, 3f))
        entries.add(BarEntry(15f, 4f))
        entries.add(BarEntry(19f, 5f))

        val barDataSet = BarDataSet(entries, "Cells")

        val labels = ArrayList<String>()
        labels.add("18-Jan")
        labels.add("19-Jan")
        labels.add("20-Jan")
        labels.add("21-Jan")
        labels.add("22-Jan")
        labels.add("23-Jan")

        barChart.setDrawGridBackground(false)

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)


        val data = BarData(barDataSet)


        barChart.data = data // set the data and list of lables into chart


        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        barDataSet.color = resources.getColor(R.color.mapbox_blue)

        barChart.animateY(5000)



        val Lentries = ArrayList<Entry>()
        Lentries.add(BarEntry(8f, 0f))
        Lentries.add(BarEntry(2f, 1f))
        Lentries.add(BarEntry(5f, 2f))
        Lentries.add(BarEntry(20f, 3f))
        Lentries.add(BarEntry(15f, 4f))
        Lentries.add(BarEntry(19f, 5f))

        val lineDataSet = LineDataSet(Lentries, "Cells")

        val Ldata = LineData(lineDataSet)


        lineChart.data = Ldata // set the data and list of lables into chart


        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        barDataSet.color = resources.getColor(R.color.mapbox_blue)

        lineChart.animateY(5000)
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

            }
        }

        spinnerLineChart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }
    }
}
