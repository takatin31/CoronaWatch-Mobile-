package com.example.coronawatch.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.coronawatch.Fragments.ArticlesFragment
import com.example.coronawatch.Fragments.HomeFragment
import com.example.coronawatch.Fragments.MapFragment
import com.example.coronawatch.R
import com.example.coronawatch.Fragments.SignalFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var currentIndex : Int = 0
    private val PERMISSION_CODE: Int = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        var titleTextView = fragmentTitleView
        titleTextView.text = resources.getStringArray(R.array.menu)[0]


        var homeFr: Fragment = HomeFragment()
        if (homeFr != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFragmentView, homeFr)
            transaction.commit()
        }
        currentIndex = 0

        buttonEffect(homeBtnView)
        buttonEffect(mapBtnView)
        buttonEffect(signalBtnView)
        buttonEffect(contentBtnView)

        var homeBtn = homeBtnView
        var signalBtn = signalBtnView
        var mapBtn = mapBtnView
        var contentBtn = contentBtnView

        homeBtn.setOnClickListener {

            var homeFr: Fragment =
                HomeFragment()
            if (homeFr != null) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mainFragmentView, homeFr)
                transaction.commit()
            }
            changeIndex(0)

        }

        signalBtn.setOnClickListener {
            //if system os if Marshmellow or Above, we need to request runtime permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED   ) {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            }else {
                var signalFr: Fragment? = null
                signalFr = SignalFragment()
                if (signalFr != null) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.mainFragmentView, signalFr)
                    transaction.commit()
                }
            }
            changeIndex(3)
        }

        mapBtn.setOnClickListener {

            if (currentIndex != 1){
                var mapFr: Fragment =
                    MapFragment()
                if (mapFr != null) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.mainFragmentView, mapFr)
                    transaction.commit()
                }
                changeIndex(1)
            }
        }

        btnAddContentView.setOnClickListener {
            val addVideoIntent = Intent(this, ShareVideoActivity::class.java)
            startActivity(addVideoIntent)
        }

        contentBtn.setOnClickListener {
            if (currentIndex != 4){
                var contentFr: Fragment =
                    ArticlesFragment()
                if (contentFr != null) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.mainFragmentView, contentFr)
                    transaction.commit()
                }
                changeIndex(4)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Go to the signal page
                    var signalFr: Fragment? = null
                    signalFr = SignalFragment()
                    if (signalFr != null) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.mainFragmentView, signalFr)
                        transaction.commit()
                    }
                } else {
                    Toast.makeText(this, "Vous ne disposez pas des permissions necessaires", Toast.LENGTH_SHORT)
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    //effecturer un effet sur les bottons
    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.greyLighter
                    ))
                    //v.background.setColorFilter(resources.getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.white
                    ))
                    v.invalidate()
                }
            }
            false
        }
    }


    //changer l'index
    fun changeIndex(newIndex: Int){
        currentIndex = newIndex
        var titleTextView = fragmentTitleView
        titleTextView.text = resources.getStringArray(R.array.menu)[newIndex]
    }




}
