package com.cow.manager.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cow.manager.Models.Cows
import com.cow.manager.Models.Reference
import com.cow.manager.Providers.CowsProvider
import com.cow.manager.Providers.ReferenceProvider
import com.cow.manager.R
import com.cow.manager.Utils.TinyDB

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.cow.manager.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = TinyDB(this).getObject("location",Reference::class.java)
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val point = LatLng(location.lat, location.lng)
        //mMap.addMarker(MarkerOptions().position(point).title("Ubicación de referencia"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,location.zoom))
        ReferenceProvider().getLocationReference(this)
        CowsProvider().getAllCows(this)
    }

    fun getCows(list : ArrayList<Cows>){

        if (list.size > 0){
            mMap.clear()
            for (i in list){
                var gender = i.gender
                gender = if (gender == "male"){
                    "Macho"
                }else{
                    "Hembra"
                }
                val pos = LatLng(i.latitude,i.longitude)
                mMap.addMarker(MarkerOptions()
                    .position(pos).title(gender))
            }
        }else{
            Toast.makeText(this,"Sin data",Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(this,list[0].gender,Toast.LENGTH_SHORT).show()
    }

    fun getReference(locationBefore : Reference){
        val locationAfter = TinyDB(this).getObject("location",Reference::class.java)
        if (locationAfter.lat != locationBefore.lat && locationAfter.lng != locationBefore.lng){
            val db = TinyDB(this)
            val point = LatLng(locationBefore.lat, locationBefore.lng)
            db.putObject("location",locationBefore)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,locationBefore.zoom))
        }
    }

    override fun onBackPressed() {
    }

}