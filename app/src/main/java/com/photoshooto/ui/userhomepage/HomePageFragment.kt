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
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import com.photoshooto.R
import com.photoshooto.databinding.FragmentHomePageBinding
import com.photoshooto.domain.adapter.ImagePagerAdapter
import com.photoshooto.domain.model.UserDashboardModel
import com.photoshooto.domain.usecase.landing_screen.LandingScreenViewModel
import com.photoshooto.ui.userhomepage.adapters.PhotographerServiceAdapter
import com.photoshooto.ui.userhomepage.adapters.TopCitiesListAdapter
import com.photoshooto.ui.userhomepage.adapters.TopPhotographyNearByListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.abs

class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private val viewModel: LandingScreenViewModel by viewModel()
    var currentPage = 0
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
        binding = FragmentHomePageBinding.inflate(inflater, container, false)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserLandingScreenData()

        initObservers()

        clickListeners()
    }

    private fun initObservers() {
        viewModel.userDashboardResponse.observe(requireActivity(), Observer {
            if (it.success!! && it.data != null) {
                binding.apply {

                    val topCities: List<UserDashboardModel.Data.TopCity?> = it.data.topCities!!
                    val tier1Cities: MutableList<UserDashboardModel.Data.TopCity?> = ArrayList()
                    val nearBy = UserDashboardModel.Data.TopCity("", "", "", "Near By")
                    tier1Cities.add(nearBy)
                    if (topCities.isNotEmpty()) {
                        for (city in topCities) {
                            val tier_type = city?.tier_type
                            if (tier_type == "1") {
                                tier1Cities.add(city)
                            }
                        }
                    }
                    val allCities = UserDashboardModel.Data.TopCity("", "", "", "All Cities")
                    tier1Cities.add(allCities)

                    rvTopCities.layoutManager =
                        LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
                    rvTopCities.adapter = TopCitiesListAdapter(topCities = tier1Cities,
                        object : TopCitiesListAdapter.OnClickListeners {
                            override fun onAllCityClick() {
                                findNavController()
                                    .navigate(R.id.action_homePageFragment_to_searchPhotographersByCityFragment)
                            }

                            override fun onNearByClick() {
                                if (!currentCityName.isNullOrEmpty()) {
                                    val bundle = Bundle()
                                    bundle.putString("filterType", "location")
                                    bundle.putString("filter", currentCityName)
                                    findNavController().navigate(
                                        R.id.action_homePageFragment_to_filterPhotographsFragment,
                                        bundle
                                    )
                                } else {
                                    getLocation()
                                }
                            }

                            override fun onCityClick(city: String) {
                                val bundle = Bundle()
                                bundle.putString("filterType", "location")
                                bundle.putString("filter", city)
                                findNavController()
                                    .navigate(
                                        R.id.action_homePageFragment_to_filterPhotographsFragment,
                                        bundle
                                    )
                            }
                        })

                    val bannerList: List<UserDashboardModel.Data.Banner?> = it.data.banners!!
                    val userBanner: MutableList<UserDashboardModel.Data.Banner> = ArrayList()
                    if (bannerList.isNotEmpty()) {
                        for (banner in bannerList) {
                            val userType = banner?.user_type
                            if (userType == "user") {
                                userBanner.add(banner)
                            }
                        }
                    }
                    binding.carouselViewpager.adapter = OffersListAdapter(userBanner,
                        object : OffersListAdapter.onClickListener {
                            override fun onItemClick(image: UserDashboardModel.Data.Banner) {
                                findNavController().navigate(R.id.action_homePageFragment_to_topPhotographersFragment)
                            }
                        })

                    binding.carouselViewpager.offscreenPageLimit = userBanner.size
                    binding.carouselViewpager.getChildAt(0).overScrollMode =
                        RecyclerView.OVER_SCROLL_NEVER
                    val compositePageTransformer = CompositePageTransformer()
                    compositePageTransformer.addTransformer(MarginPageTransformer(40))
                    compositePageTransformer.addTransformer { page, position ->
                        val r = 1 - abs(position)
                        page.scaleY = 0.85f + r * 0.15f
                    }
                    binding.carouselViewpager.setPageTransformer(compositePageTransformer)
                    TabLayoutMediator(
                        binding.indicator,
                        binding.carouselViewpager
                    ) { tab, position ->
                        // Some implementation
                    }.attach()
                    setAutoSlider(binding.carouselViewpager, userBanner.size)

                    val photographerServices: List<UserDashboardModel.Data.PhotographerService?> =
                        it.data.topPhotographyServices!!
                    rvTopServices.layoutManager =
                        LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
                    rvTopServices.adapter = PhotographerServiceAdapter(photographerServices)

                    val topPhotographers: List<UserDashboardModel.Data.TopPhotographer?> =
                        it.data.topPhotographer!!
                    rvTopIndia.layoutManager =
                        LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
                    rvTopIndia.adapter = TopPhotographyNearByListAdapter(topPhotographers)

                    binding.premiumPhotographerViewpager.adapter = ImagePagerAdapter(
                        arrayListOf(
                            R.drawable.premium_photographer1,
                            R.drawable.premium_photographer2,
                            R.drawable.premium_photographer3
                        ),
                        object : ImagePagerAdapter.OnItemClickListener {
                            override fun onImageClick() {
                                Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    binding.premiumPhotographerViewpager.offscreenPageLimit = 3
                    binding.premiumPhotographerViewpager.getChildAt(0).overScrollMode =
                        RecyclerView.OVER_SCROLL_NEVER
                    val compositePageTransformer1 = CompositePageTransformer()
                    compositePageTransformer1.addTransformer(MarginPageTransformer(40))
                    compositePageTransformer1.addTransformer { page, position ->
                        val r = 1 - abs(position)
                        page.scaleY = 0.85f + r * 0.15f
                    }
                    binding.premiumPhotographerViewpager.setPageTransformer(
                        compositePageTransformer1
                    )
                }
            }
        })

        viewModel.showProgressbar.observe(requireActivity()) {
            binding.progressBar.visibility = if (it!!) View.VISIBLE else View.GONE
        }
    }

    private fun setAutoSlider(viewPager2: ViewPager2, noOfPage: Int) {
        val handler = Handler()
        val update = Runnable {
            if (currentPage == noOfPage) {
                currentPage = 0
            }
            viewPager2.setCurrentItem(currentPage++, true)
        }

        val timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 1000, 3000)

    }

    private fun clickListeners() {
        binding.tvTopCitiesViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_searchPhotographersByCityFragment)
        }

        binding.tvTopPhotographersViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_topPhotographersFragment)
        }

        binding.tvPhotographerTopService.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_topPhotographerServiceFragment)
        }
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
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val completeAddress = list?.get(0)

                        if (completeAddress != null) {
                            currentCityName = completeAddress.locality
                            currentStateName = completeAddress.adminArea
                        }
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
}