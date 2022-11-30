package com.cow.manager.Providers

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.cow.manager.Models.Areas
import com.cow.manager.Models.Cows
import com.cow.manager.UI.MapsActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AreaProvider {

    var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllAreas(context: MapsActivity){

        val area : ArrayList<ArrayList<LatLng>> = ArrayList()
        db.collection("area").get()
            .addOnCompleteListener { documents ->
                if (documents.isSuccessful){
                    var ctx = 0
                    for(document in documents.result) {
                        val shapes : ArrayList<LatLng> = ArrayList()
                        ctx++
                        val areas = document.toObject(Areas::class.java)
                        for (i in areas.shapes){
                            shapes.add(LatLng(i.lat,i.lng))
                        }
                        area.add(shapes)
                    }
                    context.getAreas(area)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}