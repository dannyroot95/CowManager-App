package com.cow.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cow.manager.Providers.AuthenticationProvider
import com.cow.manager.UI.MapsActivity
import com.cow.manager.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class Main : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            val email = binding.edtCode.text.toString()
            val password = binding.edtPassword.text.toString()

            if(email != "" && password != ""){
                AuthenticationProvider().login(email,password,this,binding)
            }else{
                Toast.makeText(this,"Complete los campos!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        val currentUser  = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startActivity(Intent(this,MapsActivity::class.java))
            finish()
        }
        super.onStart()
    }

}