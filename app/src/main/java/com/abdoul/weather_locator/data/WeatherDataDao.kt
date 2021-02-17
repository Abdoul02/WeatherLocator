package com.abdoul.weather_locator.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdoul.weather_locator.model.WEATHER_DATA_TABLE
import com.abdoul.weather_locator.model.WeatherData
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherData: WeatherData)

    @Query("DELETE FROM $WEATHER_DATA_TABLE")
    suspend fun clearTable()

    @Query("SELECT * FROM $WEATHER_DATA_TABLE")
    fun getWeatherData(): Flow<WeatherData>
}