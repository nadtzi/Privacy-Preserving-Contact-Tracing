package com.example.ehatraz

import android.Manifest
import android.content.pm.PackageManager

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.ehatraz.gps.GpsTracker
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.android.synthetic.main.fragment_heat_map.*
import org.json.JSONArray

/**
 * A simple [Fragment] subclass.
 * Use the [HeatMapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HeatMapFragment : Fragment(R.layout.fragment_heat_map), OnMapReadyCallback {
    private lateinit var gpsTracker: GpsTracker
    var currentLocation: Location? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = this.childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val data = generateHeatMapData()
        val heatMapProvider = HeatmapTileProvider.Builder()
            .weightedData(data) // load our weighted data
            .radius(20) // optional, in pixels, can be anything between 20 and 50
            .maxIntensity(1000.0) // set the maximum intensity
            .build()

        googleMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))

        currentLocation = fetchLocation()
        Log.d("stH", getLocation().toString())
        val indiaLatLng = LatLng(getLocation()!!.latitude, getLocation()!!.longitude)


        //val indiaLatLng = LatLng(currentLocation!!.longitude, currentLocation!!.latitude)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(indiaLatLng, 20f))
    }

    private fun generateHeatMapData(): ArrayList<WeightedLatLng> {
        val data = ArrayList<WeightedLatLng>()

        val jsonData = getJsonDataFromAsset("district_data.json")
        jsonData?.let {
            for (i in 0 until it.length()) {
                val entry = it.getJSONObject(i)
                val lat = entry.getDouble("lat")
                val lon = entry.getDouble("lon")
                val density = entry.getDouble("density")

                if (density != 0.0) {
                    val weightedLatLng = WeightedLatLng(LatLng(lat, lon), density)
                    data.add(weightedLatLng)
                }
            }
        }

        return data
    }

    private fun getJsonDataFromAsset(fileName: String): JSONArray? {
        try {
            val jsonString = context?.assets?.open(fileName)?.bufferedReader().use { it?.readText() }
            return JSONArray(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun fetchLocation(): Location?{
        if (ActivityCompat.checkSelfPermission(
                        context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return null
        }
        val mLocationRequest: LocationRequest = LocationRequest.create()
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null)
                    return
            }
        }
        LocationServices.getFusedLocationProviderClient(activity!!).requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        LocationServices.getFusedLocationProviderClient(activity!!).lastLocation.addOnSuccessListener(OnSuccessListener<Location?> { location ->
            curr(location)
        })

        return currentLocation
    }

    private fun curr(location: Location?) {
        Log.d("sth", location.toString())
        currentLocation = location
        Log.d("sth", currentLocation.toString())
    }
    fun getLocation():GpsTracker? {
        gpsTracker = GpsTracker(requireContext())
        //Log.d("stH", getLocation().toString())
        if (gpsTracker.canGetLocation()) {
            return gpsTracker

        } else {
            return null
        }
    }
}