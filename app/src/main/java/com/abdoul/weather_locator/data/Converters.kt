package com.abdoul.weather_locator.data

import androidx.room.TypeConverter
import com.abdoul.weather_locator.model.currentWeather.*
import com.abdoul.weather_locator.model.currentWeather.Coord
import com.abdoul.weather_locator.model.forecast.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromClouds(clouds: Clouds?): String {
        return gson.toJson(clouds, object : TypeToken<Clouds>() {}.type)
    }

    @TypeConverter
    fun toClouds(data: String): Clouds {
        return gson.fromJson(data, object : TypeToken<Clouds>() {}.type)
    }

    @TypeConverter
    fun fromCoord(coord: Coord?): String {
        return gson.toJson(coord, object : TypeToken<Coord>() {}.type)
    }

    @TypeConverter
    fun toCoord(data: String): Coord {
        return gson.fromJson(data, object : TypeToken<Coord>() {}.type)
    }

    @TypeConverter
    fun fromMain(main: Main?): String {
        return gson.toJson(main, object : TypeToken<Main>() {}.type)
    }

    @TypeConverter
    fun toMain(data: String): Main {
        return gson.fromJson(data, object : TypeToken<Main>() {}.type)
    }

    @TypeConverter
    fun fromSys(sys: Sys?): String {
        return gson.toJson(sys, object : TypeToken<Sys>() {}.type)
    }

    @TypeConverter
    fun toSys(data: String): Sys {
        return gson.fromJson(data, object : TypeToken<Sys>() {}.type)
    }

    @TypeConverter
    fun fromWind(wind: Wind?): String {
        return gson.toJson(wind, object : TypeToken<Wind>() {}.type)
    }

    @TypeConverter
    fun toWind(data: String): Wind {
        return gson.fromJson(data, object : TypeToken<Wind>() {}.type)
    }

    @TypeConverter
    fun fromWeather(data: List<Weather>?): String {
        return gson.toJson(data, object : TypeToken<List<Weather>>() {}.type)
    }

    @TypeConverter
    fun toWeather(data: String): List<Weather> {
        return if (data.isEmpty()) Collections.emptyList() else gson.fromJson(
            data,
            object : TypeToken<List<Weather>>() {}.type
        )
    }

    @TypeConverter
    fun fromForecastDetail(data: List<ForecastDetail>?): String {
        return gson.toJson(data, object : TypeToken<List<ForecastDetail>>() {}.type)
    }

    @TypeConverter
    fun toForecastDetail(data: String): List<ForecastDetail> {
        return if (data.isEmpty()) Collections.emptyList() else gson.fromJson(
            data,
            object : TypeToken<List<ForecastDetail>>() {}.type
        )
    }

    @TypeConverter
    fun fromForecastClouds(forecastClouds: ForecastClouds?): String {
        return gson.toJson(forecastClouds, object : TypeToken<ForecastClouds>() {}.type)
    }

    @TypeConverter
    fun toForecastClouds(data: String): ForecastClouds {
        return gson.fromJson(data, object : TypeToken<ForecastClouds>() {}.type)
    }

    @TypeConverter
    fun fromForecastMain(main: ForecastMain?): String {
        return gson.toJson(main, object : TypeToken<ForecastMain>() {}.type)
    }

    @TypeConverter
    fun toForecastMain(data: String): ForecastMain {
        return gson.fromJson(data, object : TypeToken<ForecastMain>() {}.type)
    }

    @TypeConverter
    fun fromRain(rain: Rain?): String {
        return gson.toJson(rain, object : TypeToken<Rain>() {}.type)
    }

    @TypeConverter
    fun toRain(data: String): Rain {
        return gson.fromJson(data, object : TypeToken<Rain>() {}.type)
    }

    @TypeConverter
    fun fromForecastSys(sys: ForecastSys?): String {
        return gson.toJson(sys, object : TypeToken<ForecastSys>() {}.type)
    }

    @TypeConverter
    fun toForecastSys(data: String): ForecastSys {
        return gson.fromJson(data, object : TypeToken<ForecastSys>() {}.type)
    }

    @TypeConverter
    fun fromForecastWind(wind: ForecastWind?): String {
        return gson.toJson(wind, object : TypeToken<ForecastWind>() {}.type)
    }

    @TypeConverter
    fun toForecastWind(data: String): ForecastWind {
        return gson.fromJson(data, object : TypeToken<ForecastWind>() {}.type)
    }

    @TypeConverter
    fun fromForecastWeather(data: List<ForecastWeather>?): String {
        return gson.toJson(data, object : TypeToken<List<ForecastWeather>>() {}.type)
    }

    @TypeConverter
    fun toForecastWeather(data: String): List<ForecastWeather> {
        return if (data.isEmpty()) Collections.emptyList() else gson.fromJson(
            data,
            object : TypeToken<List<ForecastWeather>>() {}.type
        )
    }

    @TypeConverter
    fun toCurrentWeatherModel(data: String): CurrentWeatherModel {
        return gson.fromJson(data, object : TypeToken<CurrentWeatherModel>() {}.type)
    }

    @TypeConverter
    fun fromCurrentWeatherModel(currentWeatherModel: CurrentWeatherModel?): String {
        return gson.toJson(currentWeatherModel, object : TypeToken<CurrentWeatherModel>() {}.type)
    }

    @TypeConverter
    fun toForecastModel(data: String): ForecastModel {
        return gson.fromJson(data, object : TypeToken<ForecastModel>() {}.type)
    }

    @TypeConverter
    fun fromForecastModel(forecastModel: ForecastModel?): String {
        return gson.toJson(forecastModel, object : TypeToken<ForecastModel>() {}.type)
    }
}