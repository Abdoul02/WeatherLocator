package com.abdoul.weather_locator.weather

import androidx.lifecycle.MutableLiveData
import com.abdoul.weather_locator.model.UserLocation
import com.abdoul.weather_locator.model.WeatherData
import com.abdoul.weather_locator.model.currentWeather.CurrentWeatherModel
import com.abdoul.weather_locator.model.forecast.ForecastModel
import com.abdoul.weather_locator.repository.LocationRepository
import com.abdoul.weather_locator.repository.WeatherRepository
import com.abdoul.weather_locator.ui.home.HomeViewModel
import com.abdoul.weather_locator.utils.BaseUnitTest
import com.abdoul.weather_locator.utils.getValueForTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelShould : BaseUnitTest() {

    private val repository: WeatherRepository = mock()
    private val locationRepository: LocationRepository = mock()
    private val currentWeatherModel: CurrentWeatherModel = mock()
    private val forecastModel: ForecastModel = mock()
    private val exception = RuntimeException("Something went wrong")
    private var _error = MutableLiveData<Throwable>()
    private val userLocation: UserLocation = mock()

    private val weatherDataViewAction =
        HomeViewModel.ViewAction.WeatherInfo(WeatherData(currentWeatherModel, forecastModel))

    @Test
    fun getWeatherDataFromRepository() = runBlockingTest {
        val viewModel = mockWeatherData()

        viewModel.getWeatherData()

        verify(repository, times(1)).getWeatherInfo()
        verify(repository, times(1)).getWeatherData()
    }

    @Test
    fun emitWeatherDataFromRepositoryViaViewAction() = runBlockingTest {
        val viewModel = mockWeatherData()

        viewModel.getWeatherData()

        assertEquals(
            weatherDataViewAction,
            viewModel.weatherState.first()
        )
    }

    @Test
    fun propagatesErrorWhenReceived() = runBlockingTest {
        val homeViewModel = mockWeatherData(false)

        assertEquals(exception, homeViewModel.weatherError.getValueForTest())
    }

    @Test
    fun insertUserLocationViaLocationRepository() = runBlockingTest {
        val viewModel = mockWeatherData()

        viewModel.insertLocation(userLocation)

        verify(locationRepository, times(1)).insertLocation(userLocation)
    }

    private suspend fun mockWeatherData(success: Boolean = true): HomeViewModel {
        whenever(repository.getWeatherData()).thenReturn(
            flow {
                emit(WeatherData(currentWeatherModel, forecastModel))
            }
        )

        if (!success) {
            _error.postValue(exception)
            whenever(repository.weatherError).thenReturn(
                _error
            )
        }

        return HomeViewModel(repository, locationRepository)
    }
}