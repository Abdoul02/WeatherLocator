package com.abdoul.weather_locator.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abdoul.weather_locator.data.WeatherDataDao
import com.abdoul.weather_locator.model.NetworkResult
import com.abdoul.weather_locator.model.WeatherData
import com.abdoul.weather_locator.other.AppUtility
import com.abdoul.weather_locator.other.LocationProvider
import com.abdoul.weather_locator.service.WeatherService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.zip
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WeatherRepository @Inject constructor(
    private val service: WeatherService,
    private val weatherDataDao: WeatherDataDao,
    private val locationProvider: LocationProvider,
    private val appUtility: AppUtility
) : CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val _weatherError = MutableLiveData<Throwable>()
    val weatherError: LiveData<Throwable> = _weatherError

    init {
        locationProvider.getCurrentLocation()
    }

    fun getWeatherInfo() {
        if (appUtility.isOnline()) {
            clearTable()
        }

        launch {
            withContext(Dispatchers.IO) {
                locationProvider.locationState.collect {
                    when (it) {
                        is LocationProvider.LocationData.LocationRetrieved -> {
                            service.fetchCurrentWeather(it.latLng)
                                .zip(service.fetchForecast(it.latLng)) { currentWeather, forecast ->
                                    return@zip listOf(currentWeather, forecast)
                                }.collect { weatherInfo ->
                                    val weather = weatherInfo.first() as NetworkResult.WeatherResult
                                    val forecast =
                                        weatherInfo.last() as NetworkResult.ForecastResult
                                    loadWeather(weather, forecast)
                                }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun loadWeather(
        weatherResult: NetworkResult.WeatherResult,
        forecastResult: NetworkResult.ForecastResult
    ) {
        if (weatherResult.weather != null && forecastResult.forecast != null) {
            insertWeatherData(WeatherData(weatherResult.weather, forecastResult.forecast))
        } else {
            _weatherError.postValue(RuntimeException("Something went wrong"))
        }
    }

    fun getWeatherData(): Flow<WeatherData> {
        return weatherDataDao.getWeatherData()
    }

    private fun insertWeatherData(weatherData: WeatherData) {
        launch {
            withContext(Dispatchers.IO) {
                weatherDataDao.insert(weatherData)
            }
        }
    }

    private fun clearTable() {
        launch {
            withContext(Dispatchers.IO) {
                weatherDataDao.clearTable()
            }
        }
    }
}