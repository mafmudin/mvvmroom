package com.example.mvvmroom.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "LocationData")
data class LocationData(
    @PrimaryKey(autoGenerate = true)
    val id : Int?,
    val name: String?,
    val address: String?,
    val city: String?,
    val zipcode :String?,
    val lat: String?,
    val lng : String?,
    val status: Int?
):Serializable
