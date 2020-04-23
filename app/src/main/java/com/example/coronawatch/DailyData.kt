package com.example.coronawatch

import java.time.LocalDate
import java.util.*

class DailyData (date : LocalDate, cases : Int, deaths : Int, recovered : Int) : Comparable<DailyData> {
    var date = date
    var cases = cases
    var deads = deaths
    var recovered = recovered


    override fun compareTo(other: DailyData): Int {
        return this.date.compareTo(other.date)
    }

    fun getOptionData(option : String) : Int {
        return when (option) {
            "cases" -> {
                cases
            }
            "deaths" -> {
                deads
            }
            else -> {
                recovered
            }
        }
    }


}