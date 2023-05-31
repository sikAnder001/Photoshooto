package com.photoshooto.ui.userhomepage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.photoshooto.R
import com.photoshooto.databinding.FragmentSearchPhotographersByCityBinding
import com.photoshooto.domain.model.CityData
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SearchPhotographersByPopularCityFragment : Fragment(),
    PopularCitiesAdapter.OnPopularCitySelectedListener,
    OtherCitiesAdapter.OnOtherCitySelectedListener {

    private lateinit var binding: FragmentSearchPhotographersByCityBinding
    private val viewModel: GetUserProfileViewModel by viewModel()
    private var otherList: List<CityData> = ArrayList()
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var currentCityName: String? = null
    private var currentStateName: String? = null

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchPhotographersByCityBinding.inflate(inflater, container, false)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            ) as List<Address>
                        val completeAddress = list[0]

                        currentCityName = completeAddress.locality
                        currentStateName = completeAddress.adminArea
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarWithBack
        val backBtn = toolbar.backBtn

        val tvHeaderTxt = toolbar.tvTitle
        tvHeaderTxt.text = "Search"

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getCityList()
        initObserver()
    }

    private fun initObserver() {

        viewModel.getCityResponse.observe(requireActivity()) { response ->
            if (response.success!!) {

                val popularCities = response.data?.filter { cityData ->
                    cityData.name == "Bengaluru"
                            || cityData.name == "Hyderabad"
                            || cityData.name == "Mumbai"
                            || cityData.name == "Delhi"
                            || cityData.name == "Chennai"
                            || cityData.name == "Pune"
                            || cityData.name == "Kolkata"
                            || cityData.name == "Ahmedabad"
                }

                otherList = response.data?.filter { cityData ->
                    cityData.name != "Bengaluru"
                            && cityData.name != "Hyderabad"
                            && cityData.name != "Mumbai"
                            && cityData.name != "Delhi"
                            && cityData.name != "Chennai"
                            && cityData.name != "Pune"
                            && cityData.name != "Kolkata"
                            && cityData.name != "Ahmedabad"
                }!!

                binding.recyclerViewPopularCities.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                binding.recyclerViewPopularCities.adapter =
                    PopularCitiesAdapter(popularCities, requireActivity(), this)
                binding.recyclerViewOtherCities.adapter =
                    OtherCitiesAdapter(otherList, requireActivity(), this)

                binding.edtSearch.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        filter(s.toString())
                    }

                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })
            }
        }

        binding.tvCurrentLocation.setOnClickListener {
            if (!currentCityName.isNullOrEmpty()) {
                val bundle = Bundle()
                bundle.putString("filterType", "location")
                bundle.putString("filter", currentCityName)
                findNavController().navigate(
                    R.id.action_searchPhotographersByCityFragment_to_filterPhotographsFragment,
                    bundle
                )
            } else {
                getLocation()
            }
        }

        viewModel.showProgressbar.observe(requireActivity()) {
            binding.progressBar.visibility = if (it!!) View.VISIBLE else View.GONE
        }
    }

    fun filter(text: String?) {
        val temp: MutableList<CityData> = ArrayList()
        if (otherList.isNotEmpty()) {
            if (!text.isNullOrEmpty()) {
                for (city in otherList) {
                    if (city.name?.lowercase(Locale.getDefault())!!
                            .contains(text.lowercase(Locale.getDefault()))
                    ) {
                        temp.add(city)
                    }
                }
            } else {
                temp.addAll(otherList)
            }
        }

        //update recyclerview
        binding.recyclerViewOtherCities.adapter =
            OtherCitiesAdapter(temp, requireActivity(), this)
    }

    override fun onCitySelected(city: CityData) {
        navigateToFilterPhotograph(city)
    }

    override fun onOtherCitySelected(city: CityData) {
        navigateToFilterPhotograph(city)
    }

    private fun navigateToFilterPhotograph(city: CityData) {
        val bundle = Bundle()
        bundle.putString("filterType", "location")
        bundle.putString("filter", city.name)
        findNavController().navigate(
            R.id.action_searchPhotographersByCityFragment_to_filterPhotographsFragment,
            bundle
        )
    }
}