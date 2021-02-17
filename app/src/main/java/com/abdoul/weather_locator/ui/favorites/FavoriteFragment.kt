package com.abdoul.weather_locator.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdoul.weather_locator.databinding.FragmentFavoriteBinding
import com.abdoul.weather_locator.model.UserLocation
import com.abdoul.weather_locator.other.AppUtility
import com.abdoul.weather_locator.other.LocationListAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationListAdapter: LocationListAdapter
    private var deleteFromDB = false

    @Inject
    lateinit var appUtility: AppUtility

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root = binding.root

        locationListAdapter = LocationListAdapter(requireContext())
        binding.locationRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.locationRecycleView.setHasFixedSize(true)
        binding.locationRecycleView.adapter = locationListAdapter

        favoriteViewModel.locations.observe(viewLifecycleOwner, Observer {
            it?.let { locations ->
                populateView(locations)
            }
        })
        return root
    }

    private fun populateView(locations: List<UserLocation>) {
        locationListAdapter.setData(locations)
        if (locations.isNotEmpty()) {
            binding.tvNoLocations.isVisible = false
            binding.locationRecycleView.isVisible = true
            binding.mapFab.isVisible = true
        }

        locationListAdapter.setOnItemClickListener(object :
            LocationListAdapter.OnItemClickListener {
            override fun onDeleteIconClick(position: Int) {
                val location = locationListAdapter.getLocation(position)
                locationListAdapter.removeLocation(position)
                val snackBar = Snackbar.make(
                    binding.mapFab,
                    "Location removed from favorite",
                    Snackbar.LENGTH_LONG
                )
                snackBar.setAction("UNDO") {
                    deleteFromDB = false
                    locationListAdapter.restoreLocation(location, position)
                }
                snackBar.show()

                snackBar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            favoriteViewModel.deleteLocation(location)
                        }
                        if (event == DISMISS_EVENT_ACTION) {
                            Toast.makeText(
                                requireContext(),
                                "Location restored",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onShown(sb: Snackbar?) {
                        super.onShown(sb)
                        deleteFromDB = true
                    }
                })
            }

        })

        binding.mapFab.setOnClickListener {
            it.findNavController().navigate(FavoriteFragmentDirections.favoriteToMap())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}