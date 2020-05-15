package com.example.coronawatch.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.coronawatch.DataClasses.DailyData
import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.fragment_home.*
import java.time.LocalDate
import kotlin.math.floor


class HomeFragment : Fragment() {

    //le contexte de l'activity
    lateinit var mContext:Context
    var detached : Boolean = true
    private val PERMISSION_CODE: Int = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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
        val urlData = "${resources.getString(R.string.host)}/api/v0/zone/historyByCountry?cc=DZ"

        // Request a string response from the provided URL.
        val jsonRequestData =JsonObjectRequest(Request.Method.GET, urlData, null,
            Response.Listener { response ->
                if (progressBarNbrCared != null && nbrCaredView != null
                    && progressBarNbrInfected != null && nbrInfectedView != null
                    && progressBarNbrDeaths != null && nbrDeathsView != null ){

                    progressBarNbrCared.visibility = View.INVISIBLE
                    progressBarNbrInfected.visibility = View.INVISIBLE
                    progressBarNbrDeaths.visibility = View.INVISIBLE

                    var dataHistory = mutableListOf<DailyData>()

                    var count : Int = response.getInt("count")
                    var items = response.getJSONArray("items")

                    for (i in 0 until count){
                        var item = items.getJSONObject(i)
                        var stringDate = item.getString("date").replace("O", "").split("-")

                        var date = LocalDate.of(stringDate[0].toInt(), stringDate[1].toInt(), stringDate[2].toInt())

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

                    var nbrCases = 0
                    var nbrDeaths = 0
                    var nbrCared = 0

                    if (dataHistory.size > 0){
                        nbrCases = dataHistory.last().cases
                        nbrDeaths = dataHistory.last().deads
                        nbrCared = dataHistory.last().recovered
                    }

                    nbrDeathsView.text = nbrDeaths.toString()
                    nbrInfectedView.text = nbrCases.toString()
                    nbrCaredView.text = nbrCared.toString()
                }
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        if (!detached){
            RequestHandler.getInstance(mContext)
                .addToRequestQueue(jsonRequestData)
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            val permission = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissions(permission, PERMISSION_CODE)
        }else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    getNearestDangerZone(latLng)
                }
        }




    }


    fun getNearestDangerZone(latLng: LatLng) {

        // this is just a test because there is no data
        val urlData = "${resources.getString(R.string.host)}/api/v0/zoneRisque/nearest?lat=${latLng.latitude}&lang=${latLng.longitude}&limit=1"

        // Request a string response from the provided URL.
        val jsonRequestData =JsonObjectRequest(Request.Method.GET, urlData, null,
            Response.Listener { response ->
                val items = response.getJSONObject("items")
                if (items.getInt("count") > 0){
                    val zone = items.getJSONArray("rows").getJSONObject(0).getJSONObject("zone")
                    val zoneRisqeCity = zone.getString("city")
                    val zoneRisqueLatLng = LatLng(zone.getDouble("latitude"), zone.getDouble("longitude"))
                    val distance = floor(latLng.distanceTo(zoneRisqueLatLng)/ 1000)
                    closestDistanceView.text = distance.toString() + "  كم "
                    closestZoneTextView.text = zoneRisqeCity
                }


            },Response.ErrorListener { Log.d("Error", "Request error") })
        RequestHandler.getInstance(mContext)
            .addToRequestQueue(jsonRequestData)

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
