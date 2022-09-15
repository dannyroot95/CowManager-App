package com.cow.manager.UI

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.cow.manager.Models.Cows
import com.cow.manager.Models.Reference
import com.cow.manager.Providers.AuthenticationProvider
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
import com.cow.manager.databinding.DialogMenuBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var menuBinding : DialogMenuBinding
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        menuBinding = DialogMenuBinding.inflate(layoutInflater)

        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(menuBinding.root)

        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnMenu.setOnClickListener {
            dialog.show()
        }

        menuBinding.closeDialog.setOnClickListener {
            dialog.dismiss()
        }

        menuBinding.btnLogout.setOnClickListener {
            AuthenticationProvider().logout(this)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = TinyDB(this).getObject("location",Reference::class.java)
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val point = LatLng(location.lat, location.lng)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,location.zoom))
        ReferenceProvider().getLocationReference(this)
        CowsProvider().getAllCows(this)
    }

    fun getCows(list : ArrayList<Cows>){

        if (list.size > 0){
            mMap.clear()
            for (i in list){
                var gender = i.gender
                val pos = LatLng(i.latitude,i.longitude)
                val cowID = (i.id).split("cow")
                if(gender == "male"){
                    gender = "Macho"
                    mMap.addMarker(MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.male_cow))
                        .position(pos).title("Vaca N°"+cowID[1]))
                }else{
                    gender = "Hembra"
                    mMap.addMarker(MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.female_cow))
                        .position(pos).title("Vaca N°"+cowID[1]))
                }
            }
            binding.lnLoader.visibility = View.GONE
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