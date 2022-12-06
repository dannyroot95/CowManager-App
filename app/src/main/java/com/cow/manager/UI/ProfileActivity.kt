package com.cow.manager.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cow.manager.Models.Users
import com.cow.manager.R
import com.cow.manager.Utils.TinyDB
import com.cow.manager.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = TinyDB(this).getObject("user", Users::class.java)

        binding.edtFullname.setText(db.name)
        binding.edtDni.setText(db.dni)
        binding.edtEmail.setText(db.email)
        binding.edtPhone.setText(db.phone)
        binding.edtType.setText("Operador")

    }
}