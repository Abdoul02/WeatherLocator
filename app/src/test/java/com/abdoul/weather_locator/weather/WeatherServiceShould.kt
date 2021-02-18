package com.abdoul.weather_locator.weather

import com.abdoul.weather_locator.model.NetworkResult
import com.abdoul.weather_locator.model.currentWeather.CurrentWeatherModel
import com.abdoul.weather_locator.model.forecast.ForecastModel
import com.abdoul.weather_locator.other.AppUtility
import com.abdoul.weather_locator.service.WeatherApi
import com.abdoul.weather_locator.service.WeatherService
import com.abdoul.weather_locator.utils.BaseUnitTest
import com.mapbox.mapboxsdk.geometry.LatLng
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test


@ExperimentalCoroutinesApi
class WeatherServiceShould : BaseUnitTest() {

    private lateinit var service: WeatherService
    private var api: WeatherApi = mock()
    private val latLng: LatLng = mock()
    private val currentWeatherModel: CurrentWeatherModel = mock()
    private val forecastModel: ForecastModel = mock()

    @Test
    fun getCurrentWeatherFromAPI() = runBlockingTest {
        service = WeatherService(api)

        service.fetchCurrentWeather(latLng).first()

        verify(api, times(1)).getCurrentWeather(
            0.0,
            0.0,
            AppUtility.WEATHER_UNIT,
            AppUtility.WEATHER_API_KEY
        )
    }

    @Test
    fun getForecastFromAPI() = runBlockingTest {
        service = WeatherService(api)

        service.fetchForecast(latLng).first()

        verify(api, times(1)).getForecastWeather(
            0.0,
            0.0,
            AppUtility.WEATHER_UNIT,
            AppUtility.WEATHER_API_KEY
        )
    }

    @Test
    fun convertValuesToFlowResultAndEmitsThem() = runBlockingTest {
        whenever(
            api.getCurrentWeather(
                0.0, 0.0, AppUtility.WEATHER_UNIT,
                AppUtility.WEATHER_API_KEY
            )
        ).thenReturn(currentWeatherModel)

        whenever(
            api.getForecastWeather(
                0.0, 0.0, AppUtility.WEATHER_UNIT,
                AppUtility.WEATHER_API_KEY
            )
        ).thenReturn(forecastModel)

        service = WeatherService(api)

        assertEquals(
            NetworkResult.WeatherResult(currentWeatherModel),
            service.fetchCurrentWeather(latLng).first()
        )

        assertEquals(
            NetworkResult.ForecastResult(forecastModel),
            service.fetchForecast(latLng).first()
        )
    }

    @Test
    fun emitErrorResultWhenNetworkFails() = runBlockingTest {
        whenever(
            api.getForecastWeather(
                0.0, 0.0, AppUtility.WEATHER_UNIT,
                AppUtility.WEATHER_API_KEY
            )
        ).thenThrow(RuntimeException("Oh oh network error"))

        whenever(
            api.getCurrentWeather(
                0.0, 0.0, AppUtility.WEATHER_UNIT,
                AppUtility.WEATHER_API_KEY
            )
        ).thenThrow(RuntimeException("Oh oh network error"))

        service = WeatherService(api)

        TestCase.assertEquals(
            "Something went wrong",
            service.fetchForecast(latLng).first().error?.message
        )
        TestCase.assertEquals(
            "Something went wrong",
            service.fetchCurrentWeather(latLng).first().error?.message
        )
    }
}