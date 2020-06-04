package com.example.coronawatch.Testing

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdelingResource {
    private const val RESOURCE = "GLOBAL"

    @JvmField val countingIdlingResorce = CountingIdlingResource(RESOURCE)

    fun increment(){
        countingIdlingResorce.increment()
    }

    fun decrement(){
        if (!countingIdlingResorce.isIdleNow){
            countingIdlingResorce.decrement()
        }
    }
}