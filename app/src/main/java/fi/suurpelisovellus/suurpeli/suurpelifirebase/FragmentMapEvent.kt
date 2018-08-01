package fi.suurpelisovellus.suurpeli.suurpelifirebase

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.layout_map_event.*


class FragmentMapEvent: Fragment(),
        OnMapReadyCallback,
        View.OnClickListener {
    

    private lateinit var eventMap: GroundOverlay
    private lateinit var eventMapGrid: GroundOverlay
    private lateinit var eventMapInfo: ImageView
    private lateinit var mView: View
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var btnEventMapGrid: ImageButton
    private lateinit var btnEventMapInfo: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marker: Marker
    private lateinit var myLocation: Marker
    private lateinit var locationCallback: LocationCallback
    private lateinit var listLocationList: MutableList<Location>
    private lateinit var currentLocation: Location
    private var markerOptions: MarkerOptions = MarkerOptions()
    private var eventMapGridOnOff = true
    private var eventMapInfoOnOff = true
    private var eventSouthWestCoordinate = LatLng(61.813022, 25.16621)
    private var eventNorthEastCoordinate = LatLng(61.817262, 25.176301)
    private var eventLatLngBounds: LatLngBounds = LatLngBounds(
            eventSouthWestCoordinate,
            eventNorthEastCoordinate)
    private var initialCameraPosition = LatLng(
            ((eventSouthWestCoordinate.latitude + eventNorthEastCoordinate.latitude) / 2f),
            ((eventSouthWestCoordinate.longitude + eventNorthEastCoordinate.longitude) / 2f))

    private var listMarkersList = mutableListOf<Marker>()
    private var MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:Int = 1
    private val mDatabaseReference = FirebaseDatabase.getInstance().getReference("liput")
    // The childEventListener for Firebase
    private val childEventListener = object : ChildEventListener{
        /** The following override methods are used to update information on markers that are displayed
         * to the client after respective marker(s) have received updates manage objectives function is
         * called to manage the markers that are displayed.
         */
        override fun onChildAdded(dataSnapShot: DataSnapshot, previousChildName: String?) {
            val lippu: Lippu? = dataSnapShot.getValue(Lippu::class.java)
            if(lippu != null) {
                val lippuKey = dataSnapShot.key!!

                marker = mMap.addMarker(MarkerOptions()
                        .title(lippuKey)
                        .position(lippu.getMarkerLocation()))

                marker.isVisible = lippu.active
                marker.tag = lippuKey
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                listMarkersList.add(marker)
                manageObjectives()
            }
        }

        override fun onChildChanged(dataSnapShot: DataSnapshot, previousChildName: String?) {
            if (dataSnapShot.exists()) {
                val newLippu: Lippu? = dataSnapShot.getValue(Lippu::class.java)
                if(newLippu != null) {
                    val newLippuKey = dataSnapShot.key!!

                    for (listedMarker: Marker in listMarkersList) {
                        if (listedMarker.tag == newLippuKey) {
                            listedMarker.title = newLippuKey
                            listedMarker.position = newLippu.getMarkerLocation()
                            listedMarker.isVisible = newLippu.active
                            listedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        }
                    }
                    manageObjectives()
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val removedLippu: Lippu? = dataSnapshot.getValue(Lippu::class.java)
            if(removedLippu!= null) {
                val removedLippuKey = dataSnapshot.key!!
                var indexRemoveItem = 0

                for ((index, listedMarker: Marker) in listMarkersList.withIndex()) {
                    if (listedMarker.tag == removedLippuKey) {
                        listedMarker.remove()
                        indexRemoveItem = index
                        listedMarker.tag
                    }
                }

                listMarkersList.removeAt(indexRemoveItem)
                manageObjectives()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.layout_map_event, container, false)
        // Set the buttons with their respective Views by id
        btnEventMapGrid = mView.findViewById(R.id.buttonEventMapGrid)
        btnEventMapGrid.setOnClickListener(this)
        btnEventMapInfo = mView.findViewById(R.id.buttonMapInfo)
        btnEventMapInfo.setOnClickListener(this)
        mapFragment = childFragmentManager
                .findFragmentById(R.id.map_event) as SupportMapFragment
        // set fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)

        mapFragment.getMapAsync(this)

        // Determine the locationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null) {
                    listLocationList = locationResult.locations
                    // Store user's location updates to listLocationList
                    if (listLocationList.size != 0){
                        // Update current location to currentLocation variable
                        // by retrieving the most recent addition to the list.
                        currentLocation = listLocationList[listLocationList.size -  1]

                        var latitudeLongitude = LatLng(currentLocation.latitude, currentLocation.longitude)
                        // Removes the existing myLocation marker from the map before adding a new one
                        if(markerOptions.position != null){
                            myLocation.remove()
                        }
                        // Add myLocation marker to the map
                        myLocation = mMap.addMarker(MarkerOptions()
                                .position(latitudeLongitude)
                                .title("My Location"))

                        markerOptions.position(latitudeLongitude)
                        // Execute the below code only if the user is within the bounds of the event.
                        // Centers camera on the user.
                        if(eventNorthEastCoordinate.latitude > myLocation.position.latitude
                                && eventSouthWestCoordinate.latitude < myLocation.position.latitude
                                && eventSouthWestCoordinate.longitude < myLocation.position.longitude
                                && eventNorthEastCoordinate.longitude > myLocation.position.longitude) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation.position))
                        }
                    }
                }
            }
        }
        return mView
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // set the eventMap overlay from assets folder
        eventMap = mMap.addGroundOverlay(GroundOverlayOptions().apply {
            image(BitmapDescriptorFactory.fromAsset("map_nogrit_smallest.png"))
            positionFromBounds(eventLatLngBounds)
        })

        // set the eventMapGrid overlay from assets folder
        eventMapGrid = mMap.addGroundOverlay(GroundOverlayOptions().apply {
            image(BitmapDescriptorFactory.fromAsset("map_grit_smallest.png"))
            positionFromBounds(eventLatLngBounds)
            visible(false)
        })



        // set the imageView by id
        eventMapInfo = mView.findViewById(R.id.mapInfo)


        // Set the frequency and accuracy of location requests
        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        // check, and request access fine location permission from the user
        if (ContextCompat.checkSelfPermission(this.context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }else {

        }
        // set locationCallback, and DatabaseReference
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        mDatabaseReference.addChildEventListener(childEventListener)
        // Set the initial camera settings to display eventGroundOverlay, and disable user map scroll
        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.setMapType(GoogleMap.MAP_TYPE_NONE)
        mMap.setLatLngBoundsForCameraTarget(eventLatLngBounds)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((initialCameraPosition), 18f))
        mMap.setMinZoomPreference(16f)
    }

    override fun onResume() {
        super.onResume()

    }

    /**
     * This function is used to determine what actions will be taken in accordance to the button pressed.
     *
     * Each ON and OFF if-clause is regualted by a respective boolean switch
     *
     * The function contains if-clauses for displaying a grid over the eventMap,
     * and for mapInfo-imageview.
     */

    override fun onClick(btnEventMap: View) {
        // ON/OFF button (boolean switch) for map grid decides, which overlay (eventMap or eventMapGrid) is displayed.
        if (btnEventMap == btnEventMapGrid) {
            if (eventMapGridOnOff) {
                eventMapGrid.isVisible = true
                eventMap.isVisible = false
                eventMapGridOnOff = false
            } else {
                eventMap.isVisible = true
                eventMapGrid.isVisible = false
                eventMapGridOnOff = true
            }
        }
        // ON/OFF button (boolean switch) for mapInfo, which is either gone or visible.
        if(btnEventMap == btnEventMapInfo){
            if(eventMapInfoOnOff){
                mapInfo.visibility = VISIBLE
                btnEventMapInfo.text = "Info: ON"
                eventMapInfoOnOff = false
            } else {
                mapInfo.visibility = GONE
                btnEventMapInfo.text = "Info: OFF"
                eventMapInfoOnOff = true
            }
        }
    }

    /**
     * This function is used to control the visibility and coloring of the objectives
     *
     * ManageObjectives loops through the listMarkerList looking for first non-visible marker, coloring
     * every marker on the list green.
     * Once the first non-visible counter is found the function colors the previous marker in the list
     * as yellow (current objective)
     */
    fun manageObjectives(){
        if(listMarkersList.size > 0) {
            var intListIndexCounter = 0

            for (listedMarker: Marker in listMarkersList) {
                listedMarker.snippet = "Objective Captured"

                if (listedMarker.isVisible == false && intListIndexCounter >= 1) {
                    listMarkersList[intListIndexCounter - 1]
                            .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    listMarkersList[intListIndexCounter - 1].snippet = "Current Objective"
                    listMarkersList[intListIndexCounter - 1].showInfoWindow()
                }else if (intListIndexCounter >= 1) {
                    listMarkersList[intListIndexCounter - 1]
                            .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                }
                intListIndexCounter++
            }
        }
    }

    /*fun createLocationRequest(){
        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }*/
}