package com.example.coronawatch

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    lateinit var mContext:Context
    var detached : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this is just a test because there is no data
        val urlNbrDeaths = "${resources.getString(R.string.host)}/api/v0/dataZone"

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths =JsonObjectRequest(Request.Method.GET, urlNbrDeaths, null,
            Response.Listener { response ->
                if (progressBarNbrCared != null && nbrCaredView != null){
                    progressBarNbrDeaths.visibility = View.INVISIBLE
                    nbrDeathsView.text = response.get("count").toString()
                }
            },
            Response.ErrorListener { Log.d("Error", "Request error") })


        val urlNbrInfected = "http://coronawatch-api-v0.herokuapp.com/api/v0/dataZone"
        // Request a string response from the provided URL.
        val jsonRequestNbrInfected = JsonObjectRequest(Request.Method.GET, urlNbrInfected, null,
            Response.Listener { response ->
                if (progressBarNbrInfected != null && nbrInfectedView != null) {
                    progressBarNbrInfected.visibility = View.INVISIBLE
                    nbrInfectedView.text = response.get("count").toString()
                }
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        val urlNbrCared = "http://coronawatch-api-v0.herokuapp.com/api/v0/dataZone"
        // Request a string response from the provided URL.
        val jsonRequestNbrCared = JsonObjectRequest(Request.Method.GET, urlNbrCared, null,
            Response.Listener { response ->
                if (progressBarNbrCared != null && nbrCaredView != null) {
                    progressBarNbrCared.visibility = View.INVISIBLE
                    nbrCaredView.text = response.get("count").toString()
                }
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/groupByCountry"


        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths2 = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                Log.i("yadra??", response.toString())
                var count : Int = response.getInt("count")
                var items = response.getJSONArray("items")
                Log.i("yadra??", count.toString())
                for (i in 0 until count){
                    var item = items.getJSONObject(i)
                    var nbrCases : Int = item.getInt("totalActive")
                    Log.i("casesss", nbrCases.toString())
                }
            },
            Response.ErrorListener { Log.d("Error", "Request error") })



        if (!detached){
            RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrDeaths)
            RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrInfected)
            RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrCared)
            RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrDeaths2)
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        detached = false
    }

    override fun onDetach() {
        super.onDetach()
        detached = true
    }



}
