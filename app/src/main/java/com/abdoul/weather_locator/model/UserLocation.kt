package com.abdoul.weather_locator.model

import androidx.room.Entity
import androidx.room.PrimaryKey

const val LOCATION_TABLE = "location_table"

@Entity(tableName = LOCATION_TABLE)
data class UserLocation(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String
)