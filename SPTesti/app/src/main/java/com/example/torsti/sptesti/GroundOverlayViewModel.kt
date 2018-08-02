package com.example.torsti.sptesti

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.GroundOverlay
import android.arch.lifecycle.LiveData
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions


class GroundOverlayViewModel: ViewModel() {
    private var groundOverlayOptions: MutableLiveData<List<GroundOverlayOptions>>? = null
    fun getGroundOverlayOptions(): LiveData<List<GroundOverlayOptions>> {
        if(groundOverlayOptions == null){
            groundOverlayOptions = MutableLiveData<List<GroundOverlayOptions>>()
            loadGroundOverlayOptions()
        }
        return groundOverlayOptions as MutableLiveData<List<GroundOverlayOptions>>
    }
    private fun loadGroundOverlayOptions() {
        var groundOverlayOptionsList= mutableListOf<GroundOverlayOptions>()

        groundOverlayOptionsList.add(GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromAsset("map_nogrit_smallestest.png")))

        groundOverlayOptionsList.add(GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromAsset("map_gritonly.png")))

    }
}
