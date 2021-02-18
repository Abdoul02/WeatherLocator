package com.abdoul.weather_locator.other

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * this class will be used to get location for devices with
 * no Google play services
 * */
class GPSTracker @Inject constructor(@ApplicationContext private val context: Context) : LocationListener {

    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    private var canGetLocation = false
    private var mLocation: Location? = null
    private var locationManager: LocationManager? = null
    private var latitude = 0.0
    private var longitude = 0.0

    companion object {

        val TAG = GPSTracker.javaClass.simpleName
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000 * 60 * 5.toLong()
    }

    init {
        getLocation()
    }


    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            locationManager?.let {
                isGPSEnabled = it.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled = it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                when {
                    !isGPSEnabled && !isNetworkEnabled -> {
                        Log.d(TAG, "No provider enabled")
                    }
                    else -> {
                        this.canGetLocation = true
                        try {
                            if (mLocation != null) {
                                latitude = mLocation!!.latitude
                                longitude = mLocation!!.longitude
                            } else {
                                it.requestLocationUpdates(
                                    getProvider(it),
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                                )
                                mLocation =
                                    it.getLastKnownLocation(getProvider(it))
                                latitude = mLocation?.latitude ?: 0.0
                                longitude = mLocation?.longitude ?: 0.0

                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Exception: ${e.message}")
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
        }
        return mLocation
    }

    private fun getProvider(locationManager: LocationManager): String {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return when {
            isGPSEnabled -> LocationManager.GPS_PROVIDER
            isNetworkEnabled -> LocationManager.NETWORK_PROVIDER
            else -> throw Exception("No Provider enabled")
        }
    }

    fun getLongitude(): Double {
        return mLocation?.longitude ?: longitude
    }

    fun getLatitude(): Double {
        return mLocation?.latitude ?: latitude
    }

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    override fun onLocationChanged(location: Location) {
        mLocation = location
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }

}