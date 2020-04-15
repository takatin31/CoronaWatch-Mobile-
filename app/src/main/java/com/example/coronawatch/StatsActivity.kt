package com.example.coronawatch


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_stats.*

class StatsActivity : AppCompatActivity() {

    var countryId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        countryId = intent.getIntExtra("Id", 0)
        var countryName = intent.getStringExtra("countryName")

        var nbrCases = 0
        var nbrDeaths = 0
        var nbrCared = 0

        countryNameView.text = countryName

        nbrCasesView.text = nbrCases.toString()
        nbrDeathsView.text = nbrDeaths.toString()
        nbrCaredView.text = nbrCared.toString()

    }
}
