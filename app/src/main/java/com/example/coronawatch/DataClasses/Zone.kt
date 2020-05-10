package com.example.coronawatch.DataClasses

import com.mapbox.mapboxsdk.geometry.LatLng
import java.io.Serializable

abstract class Zone (val id : Int, val latLng : LatLng) : Serializable{
}