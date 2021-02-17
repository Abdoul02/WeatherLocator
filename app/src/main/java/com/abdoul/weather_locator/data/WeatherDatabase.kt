package com.abdoul.weather_locator.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abdoul.weather_locator.model.UserLocation
import com.abdoul.weather_locator.model.WeatherData

@Database(
    entities = [WeatherData::class, UserLocation::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDataDao(): WeatherDataDao
    abstract fun locationDao(): LocationDao
}