package com.example.coronawatch.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.coronawatch.Fragments.*
import com.example.coronawatch.R
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.home_layout.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var currentIndex : Int = 0
    private val PERMISSION_CODE: Int = 1000
    private var spinnerCpt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initDrawer()


        `userImageٍView`.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.END)
        }

        spinnerContent.visibility = View.GONE

        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_content,
            R.layout.spinner_item_layout
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item_layout)
            spinnerContent.adapter = adapter
        }

        spinnerContent.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (spinnerCpt == 0){
                    spinnerCpt++
                }else {
                    when (position) {
                        0 -> {
                            var contentFr: Fragment =
                                ArticlesFragment()
                            if (contentFr != null) {
                                val transaction = supportFragmentManager.beginTransaction()
                                transaction.replace(R.id.mainFragmentView, contentFr)
                                transaction.commit()
                            }
                        }
                        1 -> {
                            var videoFr: Fragment =
                                VideoFragment()
                            if (videoFr != null) {
                                val transaction = supportFragmentManager.beginTransaction()
                                transaction.replace(R.id.mainFragmentView, videoFr)
                                transaction.commit()
                            }
                        }

                    }
                }
            }
        }



        var titleTextView = fragmentTitleView
        titleTextView.text = resources.getStringArray(R.array.menu)[0]


        var homeFr: Fragment = HomeFragment()
        if (homeFr != null) {
            spinnerContent.visibility = View.GONE
            fragmentTitleView.visibility = View.VISIBLE
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
                spinnerContent.visibility = View.GONE
                fragmentTitleView.visibility = View.VISIBLE
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
                    spinnerContent.visibility = View.GONE
                    fragmentTitleView.visibility = View.VISIBLE
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
                    spinnerContent.visibility = View.GONE
                    fragmentTitleView.visibility = View.VISIBLE
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
                spinnerContent.visibility = View.VISIBLE
                fragmentTitleView.visibility = View.GONE
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

    private fun initDrawer(){
        val toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.nav_open, R.string.nav_close)
        drawer_layout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigation_view.setNavigationItemSelectedListener(this)

        initUserData(navigation_view.getHeaderView(0).userNameView, navigation_view.getHeaderView(0).userPicView)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.END)
        when(item.itemId){
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_health_data -> {

            }

            R.id.nav_how_protect -> {

            }

            R.id.nav_logout -> {

            }
        }


        return true
    }

    private fun initUserData(
        userNameView: TextView,
        userPicView: CircleImageView
    ) {
        val pref = getSharedPreferences(resources.getString(R.string.shared_pref),0)

        val userName = pref.getString("userNom", "")+" "+ pref.getString("userPrenom", "")
        val userPic = pref.getString("userPic", "")

        if (userPic != ""){
            Picasso.get().load(userPic).into(`userImageٍView`)
            Picasso.get().load(userPic).into(userPicView)
        }

        userNameView.text = userName

    }


}
