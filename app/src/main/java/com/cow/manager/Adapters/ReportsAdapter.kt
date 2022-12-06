package com.cow.manager.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cow.manager.Models.Incidents
import com.cow.manager.R
import com.cow.manager.databinding.ItemsReportsBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class ReportsAdapter(private val context: Context,
                          private var list: ArrayList<Incidents>) : RecyclerView.Adapter<ReportsAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemsReportsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        val sdfDate = SimpleDateFormat("dd/MM/yyyy hh:mm")
        val netDate = Date(model.date)
        val myDate = sdfDate.format(netDate)
        val idCow = model.cow
        val sign = model.signs
        val gender = model.gender
        val description = model.description

        with(holder){
            binding.itId.text = "ID Vaca : $idCow"
            binding.itSign.text = "Signo : $sign"
            binding.itDescription.text = "Descripción : $description"
            binding.itDate.text = "Fecha : $myDate"
            if (gender == "male"){
                binding.itGender.text = "Macho"
                binding.itIvGender.setImageResource(R.drawable.male_cow)
            }else{
                binding.itGender.text = "Hembra"
                binding.itIvGender.setImageResource(R.drawable.female_cow)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class MyViewHolder(val binding: ItemsReportsBinding) : RecyclerView.ViewHolder(binding.root)
}