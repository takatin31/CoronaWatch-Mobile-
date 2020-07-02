package com.example.coronawatch.Fragments

import android.Manifest
import android.content.Context
import android.content.Intent
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
import com.android.volley.toolbox.Volley
import com.example.coronawatch.Controllers.ArabicController
import com.example.coronawatch.DataClasses.DailyData
import com.example.coronawatch.R
import com.example.coronawatch.Request.FileUploadRequest
import com.example.coronawatch.Request.RequestHandler
import com.example.coronawatch.Services.NotificationActions
import com.example.coronawatch.Services.NotificationReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.time.LocalDate
import kotlin.concurrent.fixedRateTimer
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
                        var nbrCases = data.getInt("totalActive")
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

        fixedRateTimer("timer",false,0,1000*60*15){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
            Log.i("dangerVerif", "we are here")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.i("dangerVerif", "we are here2")
                    verifDangerZoneInBack(latLng)
                }
        }
    }

    fun verifDangerZoneInBack(latLng: LatLng){
        if (!detached){
            Log.i("dangerVerif", "we are here3")
            Log.i("dangerVerif", latLng.toString())
            // this is just a test because there is no data
            val urlData = "${resources.getString(R.string.host)}/api/v0/zoneRisque/nearest?lat=${latLng.latitude}&lang=${latLng.longitude}&limit=1"

            // Request a string response from the provided URL.
            val jsonRequestData =JsonObjectRequest(Request.Method.GET, urlData, null,
                Response.Listener { response ->
                    val items = response.getJSONObject("items")
                    if (items.getInt("count") > 0){
                        val zone = items.getJSONArray("rows").getJSONObject(0).getJSONObject("zone")

                        val zoneRisqueLatLng = LatLng(zone.getDouble("latitude"), zone.getDouble("longitude"))

                        val distance = distance(latLng.latitude, latLng.longitude, zoneRisqueLatLng.latitude, zoneRisqueLatLng.longitude)
                        Log.i("dangerVerifLat", zoneRisqueLatLng.toString())
                        Log.i("dangerVerif", "we are here4  "+distance)

                        if (distance <= 50){
                            Log.i("dangerVerif", "we are here5")
                            addNotification()
                        }
                    }

                },Response.ErrorListener { Log.d("Error", "Request error") })
            RequestHandler.getInstance(mContext)
                .addToRequestQueue(jsonRequestData)
        }
    }

    fun addNotification(){
        var postURL: String = "${resources.getString(R.string.host)}/api/v0/notification/"
        Log.i("dangerVerif", "we are here6")
        val request = object : FileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                Log.i("success", "comment posted succefully")
                Log.i("dangerVerif", "we are here7")
                val titre = "منطقة خطر"
                val content = "حذار لقد دخلت منطقة خطر"

                val NOTIF_SERVICE = NotificationActions.newNotification.actionCode
                val i = Intent()
                i.putExtra("title", titre)
                i.putExtra("content", content)
                i.action = NOTIF_SERVICE
                i.setClass(mContext, NotificationReceiver::class.java)
                mContext.sendBroadcast(i)

            },
            Response.ErrorListener {
                Log.i("error", "error while posting comment")
                Log.i("error", it.toString())
            }
        ){

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val dataOutputStream = DataOutputStream(byteArrayOutputStream)
                val jsonUser = JSONObject()

                val pref = mContext.getSharedPreferences(resources.getString(R.string.shared_pref),0)

                val userId = pref.getInt("userId", -1)
                jsonUser.put("typeUser", "U")
                jsonUser.put("title", ArabicController.encode_str("منطقة خطر"))
                jsonUser.put("contenu", ArabicController.encode_str("تحذير لقد دخلت منطقة خطر"))
                jsonUser.put("typeContenu", "warning")
                jsonUser.put("notificationUrlContenu","null" )
                jsonUser.put("isSeen", false)
                jsonUser.put("utilisateurUtilisateurId", userId)

                dataOutputStream.writeBytes(jsonUser.toString())
                return byteArrayOutputStream.toByteArray()
            }
        }
        Volley.newRequestQueue(mContext).add(request)
    }


    fun getNearestDangerZone(latLng: LatLng) {
        if (!detached){
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
                        val distance = floor(distance(latLng.latitude, latLng.longitude, zoneRisqueLatLng.latitude, zoneRisqueLatLng.longitude))

                        if (closestDistanceView != null && closestZoneTextView != null){
                            closestDistanceView.text = distance.toString() + "  كم "
                            closestZoneTextView.text = zoneRisqeCity
                        }

                    }

                },Response.ErrorListener { Log.d("Error", "Request error") })
            RequestHandler.getInstance(mContext)
                .addToRequestQueue(jsonRequestData)
        }


    }

    private fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
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
