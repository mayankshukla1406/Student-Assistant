package com.example.studentasisstant.Activity

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Build.ID
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.studentasisstant.Helper.GeofenceHelper
import com.example.studentasisstant.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var markingOptions: MarkerOptions
    private lateinit var locationManager: LocationManager
    private lateinit var geofenceHelper : GeofenceHelper
    private val locationcode = 2000
    private val locationcode1 = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper   = GeofenceHelper(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        markingOptions = MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Good City")
        mMap.addMarker(markingOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 12F)
        mMap.animateCamera(cameraUpdate)
        getmyLocation()
        mMap.setOnMapLongClickListener(this)
    }

    private fun getmyLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), locationcode)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), locationcode)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationcode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if ((ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(
                                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ) {
                    return
                }
                mMap.isMyLocationEnabled = true
            }
        }
        if (requestCode == locationcode1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                                this, ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)
                )
                {
                    return
            }
                Toast.makeText(this@MapsActivity,"You Can Add Geofences",Toast.LENGTH_LONG).show()
        }
    }
}

    override fun onMapLongClick(p0: LatLng) {
        if(Build.VERSION.SDK_INT>=29) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(p0)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), locationcode1)
                    Toast.makeText(this@MapsActivity, "For Triggering Geofences We Need your Background Location Permision", Toast.LENGTH_LONG).show()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), locationcode1)
                }
            }
        }
        else
        {
            handleMapLongClick(p0)
        }
    }

    private fun handleMapLongClick(p0: LatLng) {
        mMap.clear()
        addMarker(p0)
        addCircle(p0)
        addgeofence(p0)
    }
    private fun addMarker(latLng: LatLng)
    {
        markingOptions = MarkerOptions().position(latLng)
        mMap.addMarker(markingOptions)
    }
    private fun addCircle(latLng: LatLng)
    {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(100.00)
        circleOptions.strokeColor(Color.argb(255,255,0,0))
        circleOptions.fillColor(Color.argb(64,255,0,0))
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }
    private fun addgeofence(p0: LatLng) {
       val geofence = geofenceHelper.getGeofence(ID,p0,100.00, Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
        val geofenceRequest = geofence?.let { geofenceHelper.getGeofencingRequest(it) }
        val pendingIntent   = geofenceHelper.pendingIntent
        if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            return
        }
        geofencingClient.addGeofences(geofenceRequest!!,pendingIntent).run {
            addOnSuccessListener{
                Log.d("Success","Grofence Added")
            }
            addOnFailureListener{
                Log.d("Failure","Geofence Not Added")
            }
        }
    }
}