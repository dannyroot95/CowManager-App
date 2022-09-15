package com.cow.manager.Providers

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cow.manager.Main
import com.cow.manager.Models.Reference
import com.cow.manager.Models.Token
import com.cow.manager.Models.Users
import com.cow.manager.UI.MapsActivity
import com.cow.manager.Utils.TinyDB
import com.cow.manager.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class AuthenticationProvider {

    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(email : String , password : String, activity : Main , binding : ActivityMainBinding) {
        binding.progress.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { response ->
            if (response.isSuccessful) {
                val id : String = FirebaseAuth.getInstance().currentUser!!.uid
                getUsersDetails().get().addOnSuccessListener {
                    if(it.exists()){
                        val users = it.toObject(Users::class.java)
                        if(users!!.type == "operator"){
                            db.collection("reference").document("point").get().addOnSuccessListener { response ->
                                if (response.exists()){
                                    val location = response.toObject(Reference::class.java)
                                    val db = TinyDB(activity)
                                    db.putObject("location",location)
                                    db.putObject("user",users)
                                    createToken(id)
                                    activity.startActivity(Intent(activity,MapsActivity::class.java))
                                }else{
                                    val location = Reference(-12.594044060846166, -69.1955224858147,14f)
                                    val db = TinyDB(activity)
                                    db.putObject("location",location)
                                    createToken(id)
                                    activity.startActivity(Intent(activity,MapsActivity::class.java))
                                }
                            }

                        }else{
                            auth.signOut()
                            binding.progress.visibility = View.GONE
                            Toast.makeText(activity,"Usuario no permitido!",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        binding.progress.visibility = View.GONE
                        Toast.makeText(activity,"No existe el usuario!",Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                binding.progress.visibility = View.GONE
                Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun createToken(id: String){
       FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
           if (!task.isSuccessful) {
               Log.w(TAG, "Fetching FCM registration token failed", task.exception)
               return@addOnCompleteListener
           }else{
               val token = Token(task.result)
               db.collection("token").document(id).set(token)
           }
       }
    }

    private fun getUsersDetails() : DocumentReference{
        return db.collection("users").document(getCurrentUserID())
    }

    private fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun logout(activity: MapsActivity){
        auth.signOut()
        val db = TinyDB(activity)
        db.remove("user")
        db.remove("location")
        val intent = Intent(activity, Main::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

}