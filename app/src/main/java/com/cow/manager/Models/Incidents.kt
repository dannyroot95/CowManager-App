package com.cow.manager.Models

data class Incidents(val cow : String = "",
                     val gender : String = "",
                     val lat : Double = 0.0,
                     val lng : Double = 0.0,
                     val date : Long = 0L,
                     val description : String = "",
                     val signs : String = "",
                     val user : String = "")
