package com.abdoul.weather_locator.service

import com.abdoul.weather_locator.model.NetworkResult
import com.abdoul.weather_locator.other.AppUtility
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherService @Inject constructor(private val api: WeatherApi) {

    suspend fun fetchCurrentWeather(latLng: LatLng): Flow<NetworkResult.WeatherResult> {
        return flow {
            emit(
                NetworkResult.WeatherResult(
                    weather =
                    api.getCurrentWeather(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        metric = AppUtility.WEATHER_UNIT,
                        key = AppUtility.WEATHER_API_KEY
                    )
                )
            )
        }.catch {
            emit(NetworkResult.WeatherResult(error = RuntimeException("Something went wrong")))
        }
    }

    suspend fun fetchForecast(latLng: LatLng): Flow<NetworkResult.ForecastResult> {
        return flow {
            emit(
                NetworkResult.ForecastResult(
                    forecast = api.getForecastWeather(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        metric = AppUtility.WEATHER_UNIT,
                        key = AppUtility.WEATHER_API_KEY
                    )
                )
            )
        }.catch {
            emit(NetworkResult.ForecastResult(error = RuntimeException("Something went wrong")))
        }
    }
}