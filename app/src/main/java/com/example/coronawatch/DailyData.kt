package com.example.coronawatch

import java.time.LocalDate
import java.util.*

class DailyData (date : LocalDate, cases : Int, deads : Int, recovered : Int) : Comparable<DailyData> {
    var date = date
    var cases = cases
    var deads = deads
    var recovered = recovered


    override fun compareTo(other: DailyData): Int {
        return this.date.compareTo(other.date)
    }

    fun getOptionData(option : String) : Int {
        if (option == "cases"){
            return cases
        }else if (option == "deaths"){
            return deads
        }else{
            return recovered
        }
    }


}