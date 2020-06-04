package com.example.coronawatch.Request

import android.content.Context
import com.example.coronawatch.R

open class ApiManager (context : Context){

    val context = context
    private val url = "${context.resources.getString(R.string.host)}/api/v0/"

    open fun getApiUrl() : String{
        return url
    }


}