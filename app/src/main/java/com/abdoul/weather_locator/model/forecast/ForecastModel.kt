package com.abdoul.weather_locator.model.forecast

data class ForecastModel(
    //val city: City,  //No use at the moment
    val cnt: Int,
    val cod: String,
    val list: List<ForecastDetail>,
    val message: Int
)