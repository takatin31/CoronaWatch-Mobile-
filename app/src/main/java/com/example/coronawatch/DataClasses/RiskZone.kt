package com.example.coronawatch.DataClasses

import com.mapbox.mapboxsdk.geometry.LatLng

class RiskZone (val zoneRiskId : Int, val diametre : Float, val cause : String, val degre : Int,
                zoneId : Int,
                latLng: LatLng
) : Zone(zoneId, latLng) {
}