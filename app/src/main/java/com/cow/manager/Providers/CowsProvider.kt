package com.cow.manager.Providers

import android.os.Build
import androidx.annotation.RequiresApi
import com.cow.manager.Models.Cows
import com.cow.manager.UI.MapsActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CowsProvider {

    var db : DatabaseReference = Firebase.database.reference

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllCows(activity : MapsActivity){
        db.child("cows").addValueEventListener(object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var ctx = 0
                    val cows : ArrayList<Cows> = ArrayList()
                    for(postSnapshot in snapshot.children){
                        ctx++
                        val key = postSnapshot.key
                        db.child("cows").child(key!!).child("location").addValueEventListener(object : ValueEventListener{
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onDataChange(snapshotC: DataSnapshot) {
                                if (snapshotC.exists()){
                                    val cow = snapshotC.getValue(Cows::class.java)!!
                                    cow.id = key
                                    cows.add(cow)
                                    if (cows.size != 0 && cows.size == ctx){
                                        activity.getCows(cows)
                                    }
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