package com.abdoul.weather_locator.model.currentWeather

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)