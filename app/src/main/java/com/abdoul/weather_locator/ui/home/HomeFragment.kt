package com.abdoul.weather_locator.ui.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdoul.weather_locator.R
import com.abdoul.weather_locator.databinding.FragmentHomeBinding
import com.abdoul.weather_locator.model.UserLocation
import com.abdoul.weather_locator.model.WeatherData
import com.abdoul.weather_locator.model.enums.WeatherTypes
import com.abdoul.weather_locator.other.AppUtility
import com.abdoul.weather_locator.other.ForecastAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var forecastAdapter: ForecastAdapter

    @Inject
    lateinit var appUtility: AppUtility

    private var isDataFetched = false

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        if (!appUtility.hasLocationPermission(requireContext())) {
            requestPermissions()
        } else {
            if (!appUtility.isLocationEnabled()) {
                appUtility.showMessage(getString(R.string.turn_location_on))
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            } else {
                getWeather()
            }
        }

        forecastAdapter = ForecastAdapter(requireContext())
        binding.forecastRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.forecastRecycleView.setHasFixedSize(true)
        binding.forecastRecycleView.adapter = forecastAdapter

        return root
    }


    override fun onResume() {
        super.onResume()
        if (appUtility.isLocationEnabled() && appUtility.hasLocationPermission(requireContext()) && !isDataFetched) {
            getWeather()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getWeather() {
        homeViewModel.getWeatherData()

        lifecycleScope.launchWhenStarted {
            homeViewModel.weatherState.collect {
                when (it) {
                    is HomeViewModel.ViewAction.Loading -> showHideProgress()
                    is HomeViewModel.ViewAction.WeatherInfo -> displayWeatherInfo(it.weatherData)
                    else -> Unit
                }
            }
        }
        isDataFetched = true

        homeViewModel.weatherError.observe(viewLifecycleOwner, Observer {
            it?.let { error ->
                showError(error)
            }
        })
    }

    private fun showHideProgress() {
        binding.loadingCL.isVisible = true
    }

    private fun displayWeatherInfo(weatherData: WeatherData) {
        binding.loadingCL.isVisible = false
        val currentWeather = weatherData.currentWeatherModel
        val forecast = weatherData.forecastModel
        changeBackground(currentWeather.weather.first().main)
        forecastAdapter.setData(forecast.list)

        binding.homeContainer.isVisible = true
        binding.tvCurrentLocation.text = currentWeather.name
        binding.tvTemperature.text =
            getString(
                R.string.current_temp,
                currentWeather.main.temp.toInt().toString(),
                "\u2103"
            )
        binding.tvTemperatureDetail.text = currentWeather.weather[0].main
        binding.tvMinTemp.text = getString(
            R.string.current_min_temp,
            currentWeather.main.temp_min.toInt().toString(),
            "\u2103"
        )
        binding.tvCurrentTemp.text = getString(
            R.string.current_temp,
            currentWeather.main.temp.toInt().toString(),
            "\u2103"
        )
        binding.tvMaxTemp.text = getString(
            R.string.current_max_temp,
            currentWeather.main.temp_max.toInt().toString(),
            "\u2103"
        )

        if (!appUtility.isOnline()) {
            binding.tvLastUpdated.isVisible = true
            binding.tvLastUpdated.text = requireContext().getString(
                R.string.last_updated,
                appUtility.getDate(currentWeather.dt.toLong(), true)
            )
        }

        binding.favoriteFab.setOnClickListener {
            val userLocation = UserLocation(
                currentWeather.id,
                currentWeather.coord.lat,
                currentWeather.coord.lon,
                currentWeather.name
            )

            addLocation(userLocation)
        }
    }

    private fun addLocation(userLocation: UserLocation) {
        homeViewModel.insertLocation(userLocation)
        appUtility.showSnackBarMessage(binding.homeParent, getString(R.string.location_saved))
    }

    private fun changeBackground(weatherType: String) {
        when (weatherType) {
            WeatherTypes.CLEAR.types -> {
                binding.homeContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.cloudy
                    )
                )
            }
            WeatherTypes.CLOUD.types -> {
                binding.homeContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.cloudy
                    )
                )
            }
            WeatherTypes.RAIN.types -> {
                binding.homeContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.rainy
                    )
                )
            }
            WeatherTypes.SUN.types -> {
                binding.homeContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.sunny
                    )
                )
            }
            else -> {
                binding.homeContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.sunny
                    )
                )
            }
        }
    }

    private fun showError(error: Throwable) {
        if (!appUtility.isOnline()) {
            appUtility.showSnackBarMessage(binding.homeParent, getString(R.string.network_error))
        } else {
            appUtility.showSnackBarMessage(binding.homeParent, error.message.toString())
        }
    }

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.permission_required),
            AppUtility.REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setThemeResId(R.style.AlertDialogTheme).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }
}