package com.cow.manager.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cow.manager.Adapters.ReportsAdapter
import com.cow.manager.Models.Incidents
import com.cow.manager.Models.Users
import com.cow.manager.Providers.CowsProvider
import com.cow.manager.R
import com.cow.manager.Utils.TinyDB
import com.cow.manager.databinding.ActivityReportsBinding

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = TinyDB(this)
        val user = db.getObject("user",Users::class.java).name

        CowsProvider().getIncidents(this,user)
    }

    fun successDataFromServer(list: ArrayList<Incidents>){
        if (list.size > 0){
            binding.progressCircular.visibility = View.GONE
            binding.lnTitle.visibility = View.VISIBLE
            binding.recyclerRegisters.visibility = View.VISIBLE
            binding.recyclerRegisters.layoutManager = LinearLayoutManager(this)
            binding.recyclerRegisters.setHasFixedSize(true)
            val adapter = ReportsAdapter(this, list)
            binding.recyclerRegisters.adapter = adapter
        }else{
            binding.progressCircular.visibility = View.GONE
            Toast.makeText(this,"No hay datos!",Toast.LENGTH_LONG).show()
        }
    }

}