package com.abdoul.weather_locator.other

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.qualifiers.ApplicationContext
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AppUtility @Inject constructor(@ApplicationContext private val context: Context) {

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun showMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Checks if the user accepted the necessary location permissions or not
     */
    fun hasLocationPermission(mContext: Context) = EasyPermissions.hasPermissions(
        mContext,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun showSnackBarMessage(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBitmapFromDrawableId(drawableId: Int): Bitmap {
        val vectorDrawable: Drawable = context.resources.getDrawable(drawableId, null)
        val wrapDrawable = DrawableCompat.wrap(vectorDrawable)
        var h = vectorDrawable.intrinsicHeight
        var w = vectorDrawable.intrinsicWidth
        h = if (h > 0) h else 96
        w = if (w > 0) w else 96
        wrapDrawable.setBounds(0, 0, w, h)
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        wrapDrawable.draw(canvas)
        return bm
    }

    fun getDate(milliSeconds: Long, isFromApi: Boolean = false): String {
        val formatter = SimpleDateFormat("EEE, d MMM yyyy hh:mm:aaa", Locale.getDefault())
        val calender = Calendar.getInstance()
        calender.timeInMillis = if (isFromApi) milliSeconds * 1000 else milliSeconds
        return formatter.format(calender.time)
    }

    fun checkPlayServices(): Boolean {
        val gApi = GoogleApiAvailability.getInstance()
        val resultCode = gApi.isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            return false
        }
        return true
    }

    companion object {
        const val WEATHER_API_KEY = "553c6868e55911a25016bd12138e0974"
        const val MAPBOX_TOKEN = "sk.eyJ1IjoiYWJkb3VsayIsImEiOiJja2JnZGhncjUxNGMzMnZxZWxrbWVoZTR6In0.JSBhdBT3IhFIKoJ3eZdKcA"
        const val WEATHER_UNIT = "metric"
        const val REQUEST_CODE_LOCATION_PERMISSION = 0
        const val DATABASE_NAME = "weather_db"
    }
}