package com.abdoul.weather_locator.model.forecast

data class ForecastWeather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)