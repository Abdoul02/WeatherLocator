package com.abdoul.weather_locator.weather

import com.abdoul.weather_locator.data.WeatherDataDao
import com.abdoul.weather_locator.model.NetworkResult
import com.abdoul.weather_locator.model.WeatherData
import com.abdoul.weather_locator.model.currentWeather.CurrentWeatherModel
import com.abdoul.weather_locator.model.forecast.ForecastModel
import com.abdoul.weather_locator.other.AppUtility
import com.abdoul.weather_locator.other.LocationProvider
import com.abdoul.weather_locator.repository.WeatherRepository
import com.abdoul.weather_locator.service.WeatherService
import com.abdoul.weather_locator.utils.BaseUnitTest
import com.mapbox.mapboxsdk.geometry.LatLng
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryShould : BaseUnitTest() {

    private val service: WeatherService = mock()
    private val appUtility: AppUtility = mock()
    private val locationProvider: LocationProvider = mock()
    private val weatherDataDao: WeatherDataDao = mock()
    private val latLng: LatLng = mock()
    private val currentWeatherModel: CurrentWeatherModel = mock()
    private val forecastModel: ForecastModel = mock()
    private val exception = RuntimeException("Something went wrong")

    private val state = MutableStateFlow<LocationProvider.LocationData>(
        LocationProvider.LocationData.LocationRetrieved(latLng)
    )

    @Before
    fun setUp() {
        whenever(locationProvider.locationState).thenReturn(state)
    }

    @Test
    fun getWeatherDataFromDb() = runBlockingTest {
        val repository = mockWeatherData()

        assertEquals(
            WeatherData(currentWeatherModel, forecastModel),
            repository.getWeatherData().first()
        )
    }

    private suspend fun mockWeatherData(success: Boolean = true): WeatherRepository {
        whenever(service.fetchCurrentWeather(latLng)).thenReturn(
            flow {
                if (success)
                    emit(NetworkResult.WeatherResult(currentWeatherModel))
                else
                    emit(NetworkResult.WeatherResult(error = exception))
            }
        )

        whenever(service.fetchForecast(latLng)).thenReturn(
            flow {
                if (success)
                    emit(NetworkResult.ForecastResult(forecastModel))
                else
                    emit(NetworkResult.ForecastResult(error = exception))
            }
        )

        whenever(weatherDataDao.getWeatherData()).thenReturn(
            flow {
                emit(WeatherData(currentWeatherModel, forecastModel))
            }
        )

        return WeatherRepository(service, weatherDataDao, locationProvider, appUtility)
    }
}