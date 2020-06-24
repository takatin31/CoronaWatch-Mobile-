package com.example.coronawatch.Activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.R
import com.example.coronawatch.Request.FileUploadRequest
import com.example.coronawatch.Request.RequestHandler
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.activity_user_sante.*

class UserSanteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sante)

        newDialog(hearLayout, heartRateTextView)
        newDialog(bloodLayout, bloodRateTextView)
        newDialog(heightLayout, heightTextView)
        newDialog(weightLayout, weightTextView)
        newDialog(temperatureLayout, temperatureTextView)
        newDialog(ageLayout, ageTextView)

        getData()

        returnBtn.setOnClickListener {
            finish()
        }

        saveBtn.setOnClickListener {
            saveData()
        }

    }

    fun getData(){
        val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
        val userId = pref.getInt("userId", 0)
        val urlRiskZoneData = "${resources.getString(R.string.host)}/api/v0/dataSante/utilisateur/$userId"

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlRiskZoneData, null,
            Response.Listener { response ->
                val rows = response.getJSONObject("items").getJSONArray("rows")
                if (rows.length() > 0){
                    val data = rows.getJSONObject(0)
                    heartRateTextView.text = data.getDouble("rythmeCardiaque").toString()
                    bloodRateTextView.text = data.getDouble("tension").toString()
                    heightTextView.text = data.getDouble("taille").toString()
                    weightTextView.text = data.getDouble("poids").toString()
                    ageTextView.text = data.getDouble("age").toString()
                    temperatureTextView.text = data.getDouble("temperature").toString()
                }
                val listTemp = arrayListOf<Double>()
                val listDates = arrayListOf<String>()
                for (i in 0 until rows.length()){
                    val datObj = rows.getJSONObject(rows.length()-1-i)
                    listTemp.add(datObj.getDouble("temperature"))
                    val date = datObj.getString("createdAt").split("T")[0]
                    listDates.add(date)
                }
                initChart(listDates, listTemp)

            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(this).addToRequestQueue(jsonRequestNbrDeaths)
    }

    fun saveData(){
        val postURL: String = "${resources.getString(R.string.host)}/api/v0/dataSante"

        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                Log.i("success", "data posted succefully")
                Toast.makeText(this, "لقد تم الحفظ بنجاح", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
                Log.i("error", "error while posting data")
                Toast.makeText(this, "حدث خلل اثناء الحفظ يرجى اعادة المحاولة", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)
                val userId = pref.getInt("userId", 0)

                var params = HashMap<String, String>()
                params["poids"] = weightTextView.text.toString()
                params["temperature"] = temperatureTextView.text.toString()
                params["rythmeCardiaque"] = heartRateTextView.text.toString()
                params["taille"] = heightTextView.text.toString()
                params["age"] = ageTextView.text.toString()
                params["tension"] = bloodRateTextView.text.toString()
                params["utilisateurUtilisateurId"] = userId.toString()

                return params
            }

        }
        Volley.newRequestQueue(this).add(request)
    }

    fun initChart(
        listDates: ArrayList<String>,
        listTemp: ArrayList<Double>
    ) {
        val lentries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        lentries.add(BarEntry(0f, 0f))
        var j = 2f
        for (i in 0 until listTemp.size){
            lentries.add(BarEntry(j, listTemp[i].toFloat()))
            j += 1
        }

        val lineDataSet = LineDataSet(lentries, "Cells")

        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(listDates)

        lineDataSet.enableDashedLine(10f, 5f, 0f)

        val Ldata = LineData(lineDataSet)

        lineDataSet.color = ContextCompat.getColor(this, R.color.cases_color)

        lineDataSet.fillColor = ContextCompat.getColor(this, R.color.cases_color)

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

    fun newDialog(layout : RelativeLayout, valueTextView : TextView){
        layout.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.sante_dialog_layout)

            val saveBtn = dialog.findViewById<Button>(R.id.saveBtn)

            saveBtn.setOnClickListener {
                val newV = dialog.findViewById<EditText>(R.id.editText).text.toString()
                if (newV == ""){
                    Toast.makeText(this, "الرجاء ملء المعطيات", Toast.LENGTH_SHORT).show()
                }else{
                    valueTextView.text = newV
                    dialog.cancel()
                }
            }
            dialog.show()
        }
    }

}
