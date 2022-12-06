package com.cow.manager.Utils

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cow.manager.Models.Areas
import com.cow.manager.Models.Cows
import com.cow.manager.R
import com.cow.manager.UI.MapsActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import android.app.NotificationManager
import android.content.ContentResolver
import android.net.Uri
import android.media.RingtoneManager

import android.media.Ringtone





class MonitoringService : Service() {

    val dr : DatabaseReference = Firebase.database.reference
    val df : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificacionChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        initMoniroting()
        return START_STICKY
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification() {
       val notification = Notification
            .Builder(this,StringsValues.CHANNEL_ID)
            .setContentText(StringsValues.ACTIVE)
            .setSmallIcon(R.drawable.icon_cow)
            .build()
        startForeground(StringsValues.NOTIFICATION_ID,notification)
    }

    private fun initMoniroting(){

        dr.child("cows").addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var ctx = 0
                    val cows : ArrayList<Cows> = ArrayList()
                    for(postSnapshot in snapshot.children){
                        ctx++
                        val key = postSnapshot.key
                        dr.child("cows").child(key!!).child("location").addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            @SuppressLint("WrongConstant")
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onDataChange(snapshotC: DataSnapshot) {
                                if (snapshotC.exists()){
                                    val cow = snapshotC.getValue(Cows::class.java)!!
                                    val positionCow : LatLng = LatLng(cow.latitude,cow.longitude)
                                    cow.id = key
                                    val cowID = key.split("cow")
                                    val gender = cow.gender
                                    df.collection("area").get()
                                        .addOnCompleteListener { documents ->
                                            if (documents.isSuccessful){
                                                var isFar = 0
                                                for(document in documents.result) {
                                                    val areas = document.toObject(Areas::class.java)
                                                    val vertices: ArrayList<LatLng> = ArrayList()

                                                    for ((countShape) in areas.shapes.withIndex()){
                                                        val valueShape = LatLng(areas
                                                            .shapes[countShape].lat,areas
                                                            .shapes[countShape].lng)
                                                        vertices.add(valueShape)
                                                    }
                                                    if(isPointInPolygon(positionCow,vertices)){
                                                        isFar++
                                                    }
                                                }
                                                if(isFar > 0){
                                                    val notificationId = cowID[1].toInt()
                                                    val notificationManager = getSystemService(
                                                        NOTIFICATION_SERVICE) as NotificationManager
                                                    notificationManager.cancel(notificationId)
                                                }else{
                                                    var largeIcon = 0
                                                    val notificationId = cowID[1].toInt()
                                                    val notificationIntent = Intent(applicationContext,MapsActivity::class.java)
                                                    val pendingIntent = PendingIntent.getActivity(applicationContext,0,notificationIntent,0)

                                                    largeIcon = if(gender == "male"){
                                                        R.drawable.male_cow
                                                    }else{
                                                        R.drawable.female_cow
                                                    }
                                                    val alarmSound = Uri.parse(
                                                        ContentResolver.SCHEME_ANDROID_RESOURCE
                                                                + "://" + application.packageName + "/raw/cow_sound")
                                                    val r = RingtoneManager.getRingtone(applicationContext,
                                                        alarmSound)
                                                    r.play()
                                                    val notification = Notification
                                                        .Builder(applicationContext,StringsValues.CHANNEL_ID)
                                                        .setContentTitle(StringsValues.ALERT)
                                                        .setContentText("La vaca N°"+cowID[1]+" está fuera del perímetro")
                                                        .setSmallIcon(R.drawable.icon_cow)
                                                        .setLargeIcon(BitmapFactory.decodeResource(resources,largeIcon))
                                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                                        .setContentIntent(pendingIntent)
                                                        .build()
                                                    with(NotificationManagerCompat.from(applicationContext)) {
                                                        notify(notificationId, notification)
                                                    }
                                                }
                                            }

                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w(ContentValues.TAG, "Error getting documents: ", exception)
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

    private fun isPointInPolygon(tap: LatLng, vertices: ArrayList<LatLng>): Boolean {
        var intersectCount = 0
        for (j in 0 until vertices.size - 1) {
            if (rayCastIntersect(tap, vertices[j], vertices[j + 1])) {
                intersectCount++
            }
        }
        return intersectCount % 2 == 1 // odd = inside, even = outside;
    }

    private fun rayCastIntersect(tap: LatLng, vertA: LatLng, vertB: LatLng): Boolean {
        val aY = vertA.latitude
        val bY = vertB.latitude
        val aX = vertA.longitude
        val bX = vertB.longitude
        val pY = tap.latitude
        val pX = tap.longitude
        if (aY > pY && bY > pY || aY < pY && bY < pY
            || aX < pX && bX < pX
        ) {
            return false // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }
        val m = (aY - bY) / (aX - bX) // Rise over run
        val bee = -aX * m + aY // y = mx + b
        val x = (pY - bee) / m // algebra is neat!
        return x > pX
    }

    private fun createNotificacionChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                StringsValues.CHANNEL_ID,
                StringsValues.SERVICE_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

}