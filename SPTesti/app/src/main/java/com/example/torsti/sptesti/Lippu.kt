package com.example.torsti.sptesti

import com.google.android.gms.maps.model.LatLng

class Lippu (var LatLng: String, var active: Boolean) {
    constructor(): this("",false)

    lateinit var listTemp: MutableList<String>
    lateinit var coordinates:LatLng

    fun getMarkerLocation(): LatLng {
        listTemp = LatLng.split(",") as MutableList<String>
        coordinates = LatLng(listTemp[0].toDouble(), listTemp[1].toDouble())
        return coordinates
    }
}