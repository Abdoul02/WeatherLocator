package com.abdoul.weather_locator.service

import com.abdoul.weather_locator.model.currentWeather.CurrentWeatherModel
import com.abdoul.weather_locator.model.forecast.ForecastModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double?,
        @Query("lon") longitude: Double?,
        @Query("units") metric: String,
        @Query("appid") key: String
    ): CurrentWeatherModel

    @GET("/data/2.5/forecast")
    suspend fun getForecastWeather(
        @Query("lat") latitude: Double?,
        @Query("lon") longitude: Double?,
        @Query("units") metric: String,
        @Query("appid") key: String
    ): ForecastModel
}