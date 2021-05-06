package com.example.lab2

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.directions.route.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map.*
import java.io.IOException


class MapActivity : FragmentActivity(), OnMapReadyCallback,
    OnConnectionFailedListener, RoutingListener {
    //google map object
    private var mMap: GoogleMap? = null
    private lateinit var mSearchText: EditText
    var addressPlace: Address? =null

    //current and destination location objects
    var myLocation: Location? = null
    var destinationLocation: Location? = null
    protected var start: LatLng? = null
    protected var end: LatLng? = null
    var locationPermission = false

    //polyline object
    private var polylines: MutableList<Polyline>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.lab2.R.layout.activity_map)
        mSearchText = input_search

        //mSearchText = supportFragmentManager.findFragmentById(com.example.lab2.R.id.input_search) as EditText
        init()
        //request location permission.
        requestPermision()

        //init google map fragment to show map.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.example.lab2.R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    private fun init() {
        btn_map_search.setOnClickListener {
            geoLocate()
        }

        //Log.d(TAG, "init: initializing")
        mSearchText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if ( true
            ) {
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                //execute our method for searching
                geoLocate()
            }
            false
        }
    }

    private fun geoLocate() {
        //Log.d(TAG, "geoLocate: geolocating")
        val searchString = mSearchText.text.toString()
        val geocoder = Geocoder(this@MapActivity)
        var list: List<Address> = ArrayList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)
        } catch (e: IOException) {
            //Log.e(TAG, "geoLocate: IOException: " + e.getMessage())
        }
        if (list.size > 0) {
            val address: Address = list[0]
            //Log.d(TAG, "geoLocate: found a location: " + address.toString())
            addressPlace = address
            //Toast.makeText(this, address.longitude.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private fun requestPermision() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MapActivity.Companion.LOCATION_REQUEST_CODE
            )
        } else {
            locationPermission = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MapActivity.Companion.LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    //if permission granted.
                    locationPermission = true
                    getMyLocation()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.setOnMyLocationChangeListener(object : OnMyLocationChangeListener {
            override fun onMyLocationChange(location: Location) {
                myLocation = location
                val ltlng = LatLng(location.getLatitude(), location.getLongitude())
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    ltlng, 16f
                )
                mMap!!.animateCamera(cameraUpdate)
            }
        })

        mMap!!.setOnMapClickListener { latLng ->
            end = latLng
            mMap!!.clear()
            start = myLocation?.let { LatLng(it.getLatitude(), myLocation!!.getLongitude()) }
            //Toast.makeText(this@MapActivity, end.toString(), Toast.LENGTH_LONG).show()
            input_search.setText("")
            Findroutes(start, addressPlace?.let { LatLng(it.latitude, it.longitude) })
        }
/*
        btn_map_search.setOnClickListener{
            mMap!!.clear()
            start = myLocation?.let { LatLng(it.getLatitude(), myLocation!!.getLongitude()) }
            //Toast.makeText(this@MapActivity, end.toString(), Toast.LENGTH_LONG).show()
            Findroutes(start, addressPlace?.let { LatLng(it.latitude, it.longitude) })
        }*/
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (locationPermission) {
            getMyLocation()
        }
        //init()
    }

    fun Findroutes(Start: LatLng?, End: LatLng?) {
        if (Start == null || End == null) {
            Toast.makeText(this@MapActivity, "Unable to get location", Toast.LENGTH_LONG).show()
        } else {
            val routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(Start, End)
                .key("AIzaSyDqmPv6krClozUtknIwk5fBtuFtXfruqQA") //also define your api key here.
                .build()
            routing.execute()
        }
    }

    //Routing call back functions.
    override fun onRoutingFailure(e: RouteException) {
        val parentLayout: View = findViewById(R.id.content)
        val snackbar: Snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG)
        snackbar.show()
        //        Findroutes(start,end);
    }

    override fun onRoutingStart() {
        Toast.makeText(this@MapActivity, "Finding Route...", Toast.LENGTH_LONG).show()
    }

    //If Route finding success..
    override fun onRoutingSuccess(route: ArrayList<Route>, shortestRouteIndex: Int) {
        val center = CameraUpdateFactory.newLatLng(start)
        val zoom = CameraUpdateFactory.zoomTo(16f)
        if (polylines != null) {
            polylines!!.clear()
        }
        val polyOptions = PolylineOptions()
        var polylineStartLatLng: LatLng? = null
        var polylineEndLatLng: LatLng? = null
        polylines = ArrayList()
        //add route(s) to the map using polyline
        for (i in 0 until route.size) {
            if (i == shortestRouteIndex) {
                polyOptions.color(resources.getColor(R.color.black))
                polyOptions.width(7f)
                polyOptions.addAll(route[shortestRouteIndex].getPoints())
                val polyline = mMap!!.addPolyline(polyOptions)
                polylineStartLatLng = polyline.points[0]
                val k = polyline.points.size
                polylineEndLatLng = polyline.points[k - 1]
                (polylines as ArrayList<Polyline>).add(polyline)
            } else {
            }
        }

        //Add Marker on route starting position
        val startMarker = MarkerOptions()
        startMarker.position(polylineStartLatLng)
        startMarker.title("My Location")
        mMap!!.addMarker(startMarker)

        //Add Marker on route ending position
        val endMarker = MarkerOptions()
        endMarker.position(polylineEndLatLng)
        endMarker.title("Destination")
        mMap!!.addMarker(endMarker)
    }

    override fun onRoutingCancelled() {
        Findroutes(start, end)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Findroutes(start, end)
    }

    companion object {
        //to get location permissions.
        private const val LOCATION_REQUEST_CODE = 23
    }
}