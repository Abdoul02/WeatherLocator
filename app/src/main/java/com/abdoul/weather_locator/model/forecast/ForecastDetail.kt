package com.abdoul.weather_locator.model.forecast

data class ForecastDetail(
    val clouds: ForecastClouds,
    val dt: Int,
    val dt_txt: String,
    val main: ForecastMain,
    val rain: Rain?,
    val sys: ForecastSys,
    val weather: List<ForecastWeather>,
    val wind: ForecastWind
)