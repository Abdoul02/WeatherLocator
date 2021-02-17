package com.abdoul.weather_locator.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abdoul.weather_locator.model.UserLocation
import com.abdoul.weather_locator.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val locationRepository: LocationRepository) :
    ViewModel() {

    val locations: LiveData<List<UserLocation>> = locationRepository.getLocations()

    fun deleteLocation(userLocation: UserLocation) {
        locationRepository.deleteLocation(userLocation)
    }
}