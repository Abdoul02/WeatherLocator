package com.abdoul.weather_locator.repository

import androidx.lifecycle.LiveData
import com.abdoul.weather_locator.data.LocationDao
import com.abdoul.weather_locator.model.UserLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationRepository @Inject constructor(private val locationDao: LocationDao) {

    fun insertLocation(userLocation: UserLocation) {
        GlobalScope.launch(Dispatchers.IO) {
            locationDao.insertLocation(userLocation)
        }
    }

    fun deleteLocation(userLocation: UserLocation) {
        GlobalScope.launch(Dispatchers.IO) {
            locationDao.deleteLocation(userLocation)
        }
    }

    fun getLocations(): LiveData<List<UserLocation>> = locationDao.getLocations()
}