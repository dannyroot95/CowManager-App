package com.cow.manager.Providers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.cow.manager.Models.Cows
import com.cow.manager.Models.Incidents
import com.cow.manager.UI.MapsActivity
import com.cow.manager.UI.ReportsActivity
import com.cow.manager.Utils.TinyDB
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CowsProvider {

    var db : DatabaseReference = Firebase.database.reference
    var fs : FirebaseFirestore = FirebaseFirestore.getInstance()

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

    fun reportCow(id : String,
                  gender : String,
                  lat : Double,
                  lgn : Double,
                  description : String,
                  sign : String,
                  user : String){

        val data = hashMapOf(
            "cow" to id,
            "date" to System.currentTimeMillis(),
            "description" to description,
            "gender" to gender,
            "lat" to lat,
            "lng" to lgn,
            "signs" to sign,
            "user" to user
        )
        fs.collection("incidents").add(data)
    }

    fun getIncidents(context : ReportsActivity,user: String){

        fs.collection("incidents").whereEqualTo("user",user).get().addOnSuccessListener {snapshot ->
            if(snapshot != null){
                val list: ArrayList<Incidents> = ArrayList()
                val db = TinyDB(context)
                for (i in snapshot.documents){
                    val rg = i.toObject(Incidents::class.java)!!
                    list.add(rg)
                }
                list.reverse()
                db.putListObjectIncidents("incidents",list)
                when (context) {
                    else -> {
                        context.successDataFromServer(list)
                    }
                }
            }
        }


    }

}