package com.example.coronawatch

import com.mapbox.mapboxsdk.geometry.LatLng

//cette classe permet de stocker les informations d'un zone(datazone)
//dans une date définie
class ZoneData (id : Int, latLng : LatLng, nbrCases : Int, nbrDeaths : Int, nbrRecovered : Int){
    var id = id
    var latLng = latLng
    var nbrCases = nbrCases
    var nbrDeaths = nbrDeaths
    var nbrRecovered = nbrRecovered
}