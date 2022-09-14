package com.cow.manager.Providers

import com.cow.manager.Models.Reference
import com.cow.manager.UI.MapsActivity
import com.cow.manager.Utils.TinyDB
import com.google.firebase.firestore.FirebaseFirestore

class ReferenceProvider {

    var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getLocationReference(activity:MapsActivity){

        db.collection("reference").document("point").get().addOnSuccessListener {
            if (it.exists()){
                val location = it.toObject(Reference::class.java)!!
                activity.getReference(location)
            }
        }

    }

}