package com.abdoul.weather_locator.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abdoul.weather_locator.model.LOCATION_TABLE
import com.abdoul.weather_locator.model.UserLocation

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(userLocation: UserLocation)

    @Delete
    suspend fun deleteLocation(userLocation: UserLocation)

    @Query("SELECT * FROM $LOCATION_TABLE ORDER BY name")
    fun getLocations(): LiveData<List<UserLocation>>
}