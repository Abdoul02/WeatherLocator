package com.abdoul.weather_locator.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdoul.weather_locator.model.UserLocation
import com.abdoul.weather_locator.model.WeatherData
import com.abdoul.weather_locator.repository.LocationRepository
import com.abdoul.weather_locator.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<ViewAction>(ViewAction.Empty)
    val weatherState: StateFlow<ViewAction> = _weatherState

    val weatherError = repository.weatherError

    fun getWeatherData() {
        _weatherState.value = ViewAction.Loading
        repository.getWeatherInfo()
        viewModelScope.launch {
            repository.getWeatherData()
                .collect { weatherData ->
                    if (weatherData != null) {
                        _weatherState.value = ViewAction.WeatherInfo(weatherData)
                    }
                }
        }
    }

    fun insertLocation(userLocation: UserLocation) {
        locationRepository.insertLocation(userLocation)
    }

    sealed class ViewAction {
        object Loading : ViewAction()
        data class WeatherInfo(val weatherData: WeatherData) : ViewAction()
        object Empty : ViewAction()
    }
}