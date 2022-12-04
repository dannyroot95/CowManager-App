package com.cow.manager.UI

import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.cow.manager.Adapters.PopupAdapter
import com.cow.manager.Models.Cows
import com.cow.manager.Models.Reference
import com.cow.manager.Utils.MonitoringService
import com.cow.manager.Providers.*
import com.cow.manager.R
import com.cow.manager.Utils.TinyDB
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.cow.manager.databinding.ActivityMapsBinding
import com.cow.manager.databinding.DialogMenuBinding
import com.cow.manager.databinding.PopupBinding
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.MarkerOptions





class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var menuBinding : DialogMenuBinding
    private lateinit var dialog: Dialog
    private lateinit var popup : PopupBinding
    private lateinit var areasX : ArrayList<ArrayList<LatLng>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        popup = PopupBinding.inflate(layoutInflater)
        menuBinding = DialogMenuBinding.inflate(layoutInflater)
        areasX = ArrayList()

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

        menuBinding.btnMonitoring.setOnClickListener {
            startStopService()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {

        val location = TinyDB(this).getObject("location",Reference::class.java)
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val point = LatLng(location.lat, location.lng)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,location.zoom))
        ReferenceProvider().getLocationReference(this)
        CowsProvider().getAllCows(this)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getCows(list : ArrayList<Cows>){
        mMap.clear()
        if(areasX.isNotEmpty()){
            isCompleteArea(areasX)
        }else{
            AreaProvider().getAllAreas(this)
        }
        if (list.size > 0){
            var ctx = 0
            for (i in list){
                ctx++
                var gender = i.gender
                val pos = LatLng(i.latitude,i.longitude)
                val cowID = (i.id).split("cow")
              if(gender == "male"){
                    gender = "Macho"
                    mMap.addMarker(MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.male_cow))
                        .position(pos).title("Vaca N°"+cowID[1]).snippet(gender))
                    val cows = Cows(gender,0.0,0.0,cowID[1],0)
                    mMap.setInfoWindowAdapter(PopupAdapter(this,cows))
                }else{
                    gender = "Hembra"
                    mMap.addMarker(MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.female_cow))
                        .position(pos).title("Vaca N°"+cowID[1]).snippet(gender))
                    val cows = Cows(gender,0.0,0.0,cowID[1],0)
                    mMap.setInfoWindowAdapter(PopupAdapter(this,cows))
                }
            }
            binding.lnLoader.visibility = View.GONE


        }else{
            Toast.makeText(this,"Sin data",Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAreas(areas : ArrayList<ArrayList<LatLng>>){
    areasX = areas
    isCompleteArea(areasX)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isCompleteArea(areas : ArrayList<ArrayList<LatLng>>){
        if (areas.size > 0) {
            var ctx = 0
            for (i in areas){
                val polygon: Polygon = mMap.addPolygon(PolygonOptions()
                    .addAll(areas[ctx]))
                polygon.fillColor = Color.argb(0f, 139f, 16f, 0.285f)
                ctx++
            }
        }
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

    private fun startStopService(){

        if(isActiveService(MonitoringService::class.java)){
            Toast.makeText(this,"Inactivo",Toast.LENGTH_SHORT).show()
            stopService(Intent(this, MonitoringService::class.java))
        }else{
            Toast.makeText(this,"Activo",Toast.LENGTH_SHORT).show()
            startService(Intent(this, MonitoringService::class.java))
        }

    }

    private fun isActiveService(myService : Class<MonitoringService>) : Boolean{

        val manager:ActivityManager = getSystemService(Context.ACTIVITY_SERVICE
        )as ActivityManager

        for(service : ActivityManager.RunningServiceInfo in
        manager.getRunningServices(Integer.MAX_VALUE)){
            if(myService.name.equals(service.service.className)){
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
    }



}