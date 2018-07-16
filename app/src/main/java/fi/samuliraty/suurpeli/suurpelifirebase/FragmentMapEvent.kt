package fi.samuliraty.suurpeli.suurpelifirebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderClient
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
//import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_map_event.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread



class FragmentMapEvent: Fragment(),
        OnMapReadyCallback,
        View.OnClickListener {


    private lateinit var stringObjectives: String
    private lateinit var mView: View
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    //private lateinit var webApp: ServerConnection
    private lateinit var btnRefresh: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var listLocationsList = mutableListOf<LatLng>()
    private var listMarkersList = mutableListOf<Marker>()
    private var listObjectivesList = mutableListOf<String>()
    private var listDataBreakDown = mutableListOf<String>()
    private var listSplitDataList = mutableListOf<String>()
    private var listCheckList = mutableListOf<LatLng>()
    private var intObjectiveCount = 1
    private var MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:Int = 1
    private val database = FirebaseDatabase.getInstance()
    private val timerValue = database.getReference("lippu1")
    private val timerValue2 = database.getReference("lippu2")
    private val valueListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d("LeftFragValueListener", "no go")
        }

        override fun onDataChange(data: DataSnapshot) {
            val value: String = data.value.toString()
            if(!listObjectivesList.contains(value)){
                listObjectivesList.add(value)
                listSplitDataList = value.split(",") as MutableList<String>
                listLocationsList.add(LatLng(listSplitDataList[0].toDouble(),listSplitDataList[1].toDouble()))
                listSplitDataList.clear()
            }
        }
    }
    var dataReady: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.layout_map_event, container, false)
        btnRefresh = mView.findViewById(R.id.button)
        btnRefresh.setOnClickListener(this)
        mapFragment = childFragmentManager
                .findFragmentById(R.id.map_event) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)

        timerValue.addValueEventListener(valueListener)
        timerValue2.addValueEventListener(valueListener)
        mapFragment.getMapAsync(this)
        return mView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(this.context!!,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
        }else {

        }
        fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        lateinit var latitudeLongitude: LatLng
                        if(location != null){
                            latitudeLongitude = LatLng(location.latitude, location.longitude)
                            mMap.addMarker(MarkerOptions().position(latitudeLongitude).title("It is MEEEEE!!"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latitudeLongitude))
                            mMap.setMinZoomPreference(6f)
                            mMap.setMaxZoomPreference(14f)
                        }
                    }

    }

    /**
     * Method used to obtaining a set of coordinates from a server
     *
     * This method fetches a set of coordinates in the form of a single string from the server given in the method.
     * Once the string has been retrieved, it is first split into sets of coordinates that are saved
     * in the listObjectivesList. Due to the split process, the first entry to listObjectivesList is
     * empty, and thus will be removed before proceeding further, inorder to avoid errors.
     *
     * Additional modification is then applied to remove redundant characters from the string items in listObjectivesList,
     * before they are saved in listDataBreakdown. The items in listDataBreakdown are split into variables Latitude,
     * and Longitude, then converted to double and saved as LatLng in the variable newLatLng, which is then saved
     * in listCoordinates.
     */
    /*override fun onResume() {
        super.onResume()
        /*server.settings.javaScriptEnabled = true
        webApp = ServerConnection(this.context)
        server.addJavascriptInterface(webApp, "Android")
        server.loadUrl("http://suurpeli.samuliraty.fi/testi/markerfeed.php")
        doAsync {
            while (!dataReady) {
                dataReady = webApp.serverReady
            }
            if (dataReady) {
                uiThread {
                    stringObjectives = webApp.marker
                    listObjectivesList = stringObjectives
                            .split("Lippu") as MutableList<String>
                    listObjectivesList.removeAt(0)

                    for (item in listObjectivesList) {
                        listDataBreakDown.add(item.substring(2))
                    }
                    for (item in listDataBreakDown) {
                        listSplitData = item.split(",") as MutableList<String>
                        latitude = listSplitData[0].toDouble()
                        longitude = listSplitData[1].toDouble()
                        var newLatLng = LatLng(latitude, longitude)
                        listCoordinates.add(newLatLng)
                        listSplitData.clear()
                    }
                    //testi.text = "List value is:" + listCoordinates[0] + " ; " + listCoordinates[1]
                }
            }
        }*/
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
        for(LatLng in listLocationsList){
            if(!listCheckList.contains(LatLng)){
                listCheckList.add(LatLng)
                listMarkersList.add(mMap.addMarker(MarkerOptions()
                        .position(LatLng)
                        .title("Objective " + intObjectiveCount)))
                intObjectiveCount++
            }
        }

    }
}