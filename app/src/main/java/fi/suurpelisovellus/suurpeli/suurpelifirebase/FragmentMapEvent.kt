package fi.suurpelisovellus.suurpeli.suurpelifirebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderClient
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.layout_map_event.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread



class FragmentMapEvent: Fragment(),
        OnMapReadyCallback,
        View.OnClickListener {


    private lateinit var mView: View
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var btnRefresh: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marker: Marker
    private var markerOptions: MarkerOptions = MarkerOptions()
    private lateinit var myLocation: Marker
    private lateinit var lippu: Lippu
    private lateinit var lippuKey: String
    private lateinit var newLippu: Lippu
    private lateinit var newLippuKey: String
    private lateinit var removedLippu: Lippu
    private lateinit var removedLippuKey: String
    private lateinit var locationCallback: LocationCallback
    private lateinit var listLocationList: MutableList<Location>
    private lateinit var currentLocation: Location
    private var requestingLocationUpdates: Boolean = true

    private var listMarkersList = mutableListOf<Marker>()
    private var MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1
    private var mDatabaseReference = FirebaseDatabase.getInstance().getReference("liput")
    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapShot: DataSnapshot, previousChildName: String?) {
            lippu = dataSnapShot.getValue(Lippu::class.java)!!

            lippuKey = dataSnapShot.key!!

            marker = mMap.addMarker(MarkerOptions()
                    .title(lippuKey)
                    .position(lippu.getMarkerLocation()))

            marker.isVisible = lippu.active
            marker.tag = lippuKey
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            listMarkersList.add(marker)
            manageObjectives()

        }

        override fun onChildChanged(dataSnapShot: DataSnapshot, previousChildName: String?) {
            if (dataSnapShot.exists()) {
                newLippu = dataSnapShot.getValue(Lippu::class.java)!!
                newLippuKey = dataSnapShot.key!!

                for (listedMarker: Marker in listMarkersList) {
                    if (listedMarker.tag == newLippuKey) {
                        listedMarker.title = newLippuKey
                        listedMarker.position = newLippu.getMarkerLocation()
                        listedMarker.isVisible = newLippu.active
                        listedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    }
                }
                manageObjectives()
                //testi.text = "input: " + listMarkersList.size
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            //testi.text = "Database Error"
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            removedLippu = dataSnapshot.getValue(Lippu::class.java)!!
            removedLippuKey = dataSnapshot.key!!
            //testi.text = "Key is: " + removedLippuKey
            var indexRemoveItem = 0
            for ((index, listedMarker: Marker) in listMarkersList.withIndex()) {
                if (listedMarker.tag == removedLippuKey) {
                    listedMarker.remove()
                    indexRemoveItem = index
                }
            }
            listMarkersList.removeAt(indexRemoveItem)
            manageObjectives()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.layout_map_event, container, false)
        btnRefresh = mView.findViewById(R.id.button)
        btnRefresh.setOnClickListener(this)
        mapFragment = childFragmentManager
                .findFragmentById(R.id.map_event) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)

        if (mapFragment == null) {
            var fm: FragmentManager? = fragmentManager
            var ft: FragmentTransaction? = fm?.beginTransaction()
            mapFragment = SupportMapFragment.newInstance()
            ft?.replace(R.id.map_event, mapFragment)
                    ?.commit()
        }
        mapFragment.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null) {
                    listLocationList = locationResult!!.locations
                    if (listLocationList.size != 0) {
                        currentLocation = listLocationList[listLocationList.size - 1]

                        var latitudeLongitude = LatLng(currentLocation.latitude, currentLocation.longitude)

                        if (markerOptions.position != null) {
                            myLocation.remove()
                        }

                        myLocation = mMap.addMarker(MarkerOptions()
                                .position(latitudeLongitude)
                                .title("My Location"))

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, 14f))
                        markerOptions.position(latitudeLongitude)
                    }
                }
            }
        }
        return mView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(this.context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        } else {

        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        mDatabaseReference.addChildEventListener(childEventListener)
    }

    /*override fun onResume() {
        super.onResume()

    }*/

    /*override fun onPause() {
        super.onPause()
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }*/

    /**
     * A Button to Place new markers to the map
     *
     * This method Checks for any mismatches between the listCoordinates, and listCheckList variables.
     * In case of a mismatch, the item that does not exist in listCheckList is added to the list in question
     * and a new marker is added to listMarkersList using the new coordinates. After the listCheckList, and
     * listMarkersList have been updated, listCoordinates is cleared.
     *
     * Markers are named in an ascending order in the sequence they are created.
     */

    override fun onClick(btnRefresh: View) {

    }

    fun manageObjectives(){
        if(listMarkersList.size > 0) {
            var intListIndexCounter = 0

            for (listedMarker: Marker in listMarkersList) {
                listedMarker.snippet = "Objective Captured"
                if (!listedMarker.isVisible && intListIndexCounter >=1) {
                    listMarkersList[intListIndexCounter - 1]
                            .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    listMarkersList[intListIndexCounter - 1].snippet = "Current Objective"
                    listMarkersList[intListIndexCounter - 1].showInfoWindow()
                }else if(intListIndexCounter >= 1){
                    listMarkersList[intListIndexCounter - 1]
                            .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                }
                intListIndexCounter++
            }

        }
    }
}