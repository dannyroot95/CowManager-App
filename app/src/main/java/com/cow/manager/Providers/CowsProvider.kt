package com.cow.manager.Providers

import android.widget.Toast
import com.cow.manager.Models.Cows
import com.cow.manager.UI.MapsActivity
import com.google.common.collect.Maps
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng

class CowsProvider {

    var db : DatabaseReference = Firebase.database.reference

    fun getAllCows(activity : MapsActivity){

        db.child("cows").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val cows : ArrayList<Cows> = ArrayList()
                    for (postSnapshot in snapshot.children){
                        val key = postSnapshot.key
                        db.child("cows").child(key!!).child("location").addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshotC: DataSnapshot) {
                                if (snapshotC.exists()){
                                    val cow = snapshotC.getValue(Cows::class.java)!!
                                    cows.add(cow)
                                    activity.getCows(cows)
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

}