package com.abdoul.weather_locator.ui.map

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.abdoul.weather_locator.R
import com.abdoul.weather_locator.databinding.FragmentMapBinding
import com.abdoul.weather_locator.other.AppUtility
import com.abdoul.weather_locator.other.AppUtility.Companion.MAPBOX_TOKEN
import com.abdoul.weather_locator.other.LocationProvider
import com.abdoul.weather_locator.ui.favorites.FavoriteViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment() {

    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: MapboxMap
    private var mapInitialised = false

    @Inject
    lateinit var locationProvider: LocationProvider

    @Inject
    lateinit var appUtility: AppUtility


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root = binding.root

        Mapbox.getInstance(requireContext(), MAPBOX_TOKEN)

        binding.mapView.onCreate(savedInstanceState)
        if (appUtility.hasLocationPermission(requireContext())) {
            if (appUtility.isLocationEnabled()) {
                initializeMap()
            } else {
                appUtility.showMessage(getString(R.string.turn_location_on))
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            appUtility.showMessage(getString(R.string.permission_required))
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (appUtility.hasLocationPermission(requireContext()) && appUtility.isLocationEnabled() && !mapInitialised) {
            initializeMap()
        }
    }

    private fun initializeMap() {
        binding.mapView.getMapAsync {
            it?.let { mapBoxMap ->
                map = mapBoxMap
                map.setStyle(Style.DARK)
                populateMap()
                getCurrentLocation()
            }
        }
        mapInitialised = true
    }

    private fun getCurrentLocation() {
        lifecycleScope.launchWhenStarted {
            locationProvider.locationState.collect {
                when (it) {
                    is LocationProvider.LocationData.LocationRetrieved -> animateToCurrentLocation(
                        it.latLng
                    )
                    LocationProvider.LocationData.Empty -> loading(true)
                }
            }
        }
    }

    private fun loading(show: Boolean) {
        binding.mapProgressbar.isVisible = show
    }

    private fun animateToCurrentLocation(currentLocation: LatLng) {
        loading(false)
        addIconToMap(currentLocation, getString(R.string.current_location), true)
        val position = CameraPosition.Builder()
            .target(currentLocation)
            .zoom(13.0)
            .bearing(180.0)
            .tilt(30.0)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000)
    }

    private fun populateMap() {
        favoriteViewModel.locations.observe(viewLifecycleOwner, Observer {
            it?.let { locations ->
                for (location in locations) {
                    addIconToMap(LatLng(location.latitude, location.longitude), location.name)
                }
            }
        })
    }

    private fun addIconToMap(point: LatLng, content: String, currentLocation: Boolean = false) {
        val iconFactory = IconFactory.getInstance(requireContext())
        val icon =
            if (currentLocation) iconFactory.fromBitmap(appUtility.getBitmapFromDrawableId(R.drawable.ic_current_location)) else
                iconFactory.fromBitmap(appUtility.getBitmapFromDrawableId(R.drawable.ic_locations))
        val marker = MarkerOptions()
        marker.icon = icon
        marker.position = point
        marker.title = content
        map.addMarker(marker)
    }
}