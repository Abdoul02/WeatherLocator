package com.abdoul.weather_locator.model

import com.abdoul.weather_locator.model.currentWeather.CurrentWeatherModel
import com.abdoul.weather_locator.model.forecast.ForecastModel

object NetworkResult {
    data class WeatherResult(val weather: CurrentWeatherModel? = null, val error: Throwable? = null)
    data class ForecastResult(val forecast: ForecastModel? = null, val error: Throwable? = null)
}