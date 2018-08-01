package fi.suurpelisovellus.suurpeli.suurpelifirebase
import android.Manifest
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
    private lateinit var myLocation: Marker
    private lateinit var locationCallback: LocationCallback
    private lateinit var listLocationList: MutableList<Location>
    private lateinit var currentLocation: Location
    private var requestingLocationUpdates:Boolean = true

    private var listMarkersList = mutableListOf<Marker>()
    private var MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:Int = 1
    private val mDatabaseReference = FirebaseDatabase.getInstance().getReference("liput")

    private val childEventListener = object : ChildEventListener{
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
        btnEventMapGrid = mView.findViewById(R.id.buttonEventMapGrid)
        btnEventMapGrid.setOnClickListener(this)
        btnEventMapInfo = mView.findViewById(R.id.buttonMapInfo)
        btnEventMapInfo.setOnClickListener(this)
        mapFragment = childFragmentManager
                .findFragmentById(R.id.map_event) as SupportMapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)

        mapFragment.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null) {
                    listLocationList = locationResult!!.locations
                    if (listLocationList.size != 0){
                        currentLocation = listLocationList[listLocationList.size -  1]

                        var latitudeLongitude = LatLng(currentLocation.latitude, currentLocation.longitude)

                        if(markerOptions.position != null){
                            myLocation.remove()
                        }

                        myLocation = mMap.addMarker(MarkerOptions()
                                .position(latitudeLongitude)
                                .title("My Location"))

                        markerOptions.position(latitudeLongitude)

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

        eventMap = mMap.addGroundOverlay(GroundOverlayOptions().apply {
            image(BitmapDescriptorFactory.fromAsset("map_nogrit_smallest.png"))
            positionFromBounds(eventLatLngBounds)
        })

        eventMapGrid = mMap.addGroundOverlay(GroundOverlayOptions().apply {
            image(BitmapDescriptorFactory.fromAsset("map_grit_smallest.png"))
            positionFromBounds(eventLatLngBounds)
            visible(false)
        })
        eventMapInfo = mView.findViewById(R.id.mapInfo)



        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(this.context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }else {

        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        mDatabaseReference.addChildEventListener(childEventListener)
        // Set the initial camera settings to display eventGroundOverlay
        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.setMapType(GoogleMap.MAP_TYPE_NONE)
        mMap.setLatLngBoundsForCameraTarget(eventLatLngBounds)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((initialCameraPosition), 18f))
        mMap.setMinZoomPreference(16f)
    }

    override fun onResume() {
        super.onResume()

    }

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

    override fun onClick(btnEventMap: View) {
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