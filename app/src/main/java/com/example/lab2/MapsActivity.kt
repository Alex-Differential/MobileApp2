package com.example.lab2
/*
import android.graphics.Color
import android.os.AsyncTask
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class MapsActivity : AppCompatActivity() {

    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            Log.d("GoogleMap", "before isMyLocationEnabled")

            val location1 = LatLng(47.9200981,33.3493149)
            googleMap.addMarker(MarkerOptions().position(location1).title("Моє місцезнаходження"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1,5f))

            Log.d("GoogleMap", "before location2")
            val location2 = LatLng(48.467684,35.0408268)
            googleMap.addMarker(MarkerOptions().position(location2).title("Дніпро"))


            Log.d("GoogleMap", "before URL")
            val URL = getDirectionURL(location1,location2)
            Log.d("GoogleMap", "URL : $URL")
            GetDirection(URL).execute()

        })
    }

    fun getDirectionURL(origin:LatLng,dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving"
    }

    private inner class GetDirection(val url : String) : AsyncTask<Void,Void,List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            var response = client.newCall(request).execute()
            val data = response.body!!.string()
            Log.d("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){

                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            googleMap.addPolyline(lineoption)
        }
    }

    public fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }
}*/

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val places: MutableList<LatLng> = ArrayList()
    private var mapsApiKey: String? = null
    private var width = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        places.add(LatLng(55.754724, 37.621380))
        places.add(LatLng(55.760133, 37.618697))
        places.add(LatLng(55.764753, 37.591313))
        places.add(LatLng(55.728466, 37.604155))
        mapsApiKey = this.resources.getString(R.string.google_maps_key)
        width = resources.displayMetrics.widthPixels
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val markers = arrayOfNulls<MarkerOptions>(places.size)
        for (i in places.indices) {
            markers[i] = MarkerOptions()
                .position(com.google.android.gms.maps.model.LatLng(places[i].lat, places[i].lng))
            googleMap.addMarker(markers[i])
        }
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(mapsApiKey)
            .build()
        var result: DirectionsResult? = null
        try {
            result = DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.WALKING)
                .origin(places[0])
                .destination(places[places.size - 1])
                .waypoints(places[1], places[2]).await()
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val path = result?.routes?.get(0)?.overviewPolyline?.decodePath()
        val line = PolylineOptions()
        val latLngBuilder = LatLngBounds.Builder()
        if (path != null) {
            for (i in path.indices) {
                line.add(com.google.android.gms.maps.model.LatLng(path[i].lat, path[i].lng))
                latLngBuilder.include(
                    com.google.android.gms.maps.model.LatLng(
                        path[i].lat,
                        path[i].lng
                    )
                )
            }
        }
        line.width(16f).color(R.color.colorRed)
        googleMap.addPolyline(line)
        /*val latLngBounds = latLngBuilder.build()
        val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, width, 25)
        googleMap.moveCamera(track)*/
    }
}
