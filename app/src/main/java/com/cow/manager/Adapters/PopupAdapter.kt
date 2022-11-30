package com.cow.manager.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.cow.manager.Models.Cows
import com.cow.manager.R
import com.cow.manager.databinding.PopupBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker



class PopupAdapter(var context : Context, var cow : Cows) : GoogleMap.InfoWindowAdapter{

    private val binding : PopupBinding = PopupBinding
        .inflate(LayoutInflater.from(context),null,false)


    private fun setInfoWindowText(marker: Marker) {
        val title = marker.title
        val content = marker.snippet
        binding.idCow.text = "Vaca N°"+cow.id
        binding.idCow.text = title
        binding.gender.text = "Género : ${cow.gender}"
        binding.gender.text = content
        if (content == "Hembra"){
            binding.imgGender.setImageResource(R.drawable.female_cow)
        }else{
            binding.imgGender.setImageResource(R.drawable.male_cow)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun getInfoContents(marker: Marker): View {
        setInfoWindowText(marker)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun getInfoWindow(marker: Marker): View {
        setInfoWindowText(marker)
        return binding.root
    }
}
