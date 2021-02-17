package com.abdoul.weather_locator.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.abdoul.weather_locator.data.LocationDao
import com.abdoul.weather_locator.data.WeatherDataDao
import com.abdoul.weather_locator.data.WeatherDatabase
import com.abdoul.weather_locator.other.AppUtility.Companion.DATABASE_NAME
import com.abdoul.weather_locator.service.WeatherApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val client = OkHttpClient()

@Module
@InstallIn(SingletonComponent::class)
class WeatherModule {

    @Provides
    fun weatherApi(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

    @Provides
    fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    fun provideLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        return mLocationRequest
    }

    @Singleton
    @Provides
    fun provideAppDb(app: Application): WeatherDatabase {
        return Room.databaseBuilder(app, WeatherDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideWeatherDataDao(db: WeatherDatabase): WeatherDataDao = db.weatherDataDao()

    @Singleton
    @Provides
    fun provideLocationDao(db: WeatherDatabase): LocationDao = db.locationDao()

}