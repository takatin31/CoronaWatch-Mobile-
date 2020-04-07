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
        val urlNbrDeaths = "http://coronawatch-api-v0.herokuapp.com/api/v0/dataZone"

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths =JsonObjectRequest(Request.Method.GET, urlNbrDeaths, null,
            Response.Listener { response ->
                progressBarNbrDeaths.visibility = View.INVISIBLE
                nbrDeathsView.text = response.get("count").toString()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })


        val urlNbrInfected = "http://coronawatch-api-v0.herokuapp.com/api/v0/dataZone"
        // Request a string response from the provided URL.
        val jsonRequestNbrInfected = JsonObjectRequest(Request.Method.GET, urlNbrInfected, null,
            Response.Listener { response ->
                progressBarNbrInfected.visibility = View.INVISIBLE
                nbrInfectedView.text = response.get("count").toString()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        val urlNbrCared = "http://coronawatch-api-v0.herokuapp.com/api/v0/dataZone"
        // Request a string response from the provided URL.
        val jsonRequestNbrCared = JsonObjectRequest(Request.Method.GET, urlNbrCared, null,
            Response.Listener { response ->
                progressBarNbrCared.visibility = View.INVISIBLE
                nbrCaredView.text = response.get("count").toString()
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        if (!detached){
            RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrDeaths)
            RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrInfected)
            RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrCared)
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
