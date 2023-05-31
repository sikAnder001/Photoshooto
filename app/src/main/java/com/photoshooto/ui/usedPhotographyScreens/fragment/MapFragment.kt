package com.photoshooto.ui.usedPhotographyScreens.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentMapBinding
import com.photoshooto.ui.job.utility.GetCurrentLocation
import com.photoshooto.ui.job.utility.openPermissionSettings
import com.photoshooto.ui.job.utility.toastError
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import java.io.IOException


class MapFragment : BaseFragment(), OnMapReadyCallback {

    lateinit var binding: FragmentMapBinding

    private var mMap: GoogleMap? = null
    private var onCameraIdleListener: GoogleMap.OnCameraIdleListener? = null
    var currentLocation: Location? = null
    var address: String? = null
    var
            newAddress: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized) {
            return binding.root
        }

        binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.imgPostBack.setOnClickListener {
            findNavController().popBackStack()

        }

        binding.ivRecenter.setOnClickListener {
            getLocationMap()
        }

        binding.llUsePosition.setOnClickListener {
            //PostProductStep01Fragment.selectLocation = newAddress.toString()
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "address",
                newAddress.toString()
            )
            findNavController().popBackStack()

        }

        getLocationMap()

        return binding.root
    }

    private fun getLocationMap() {

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )

        MayI.withActivity(requireActivity()).withPermissions(*permissions)
            .onRationale(this::permissionRationaleMultiLocation)
            .onResult(this::permissionResultMultiLocation)
            .onErrorListener(this::inCaseOfErrorLocation).check()
    }

    private fun permissionRationaleMultiLocation(
        permissions: Array<PermissionBean>, token: PermissionToken
    ) {
        token.continuePermissionRequest()
    }

    private fun inCaseOfErrorLocation(e: Exception) {
        requireContext().toastError("Error for permission : " + e.message)
    }

    private fun permissionResultMultiLocation(
        permissions: Array<PermissionBean>
    ) {
        val isAllPermanentlyDenied = permissions.all {
            it.isPermanentlyDenied
        }
        if (isAllPermanentlyDenied) {

            val snackbar: Snackbar = Snackbar.make(
                binding.root,
                getString(R.string.permission_always_denied),
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Settings") {
                activity?.openPermissionSettings()
            }
            snackbar.show()
            return
        }
        val isAllPermissionGranted = permissions.all {
            it.isGranted
        }

        if (isAllPermissionGranted) {
            showProgress()
            GetCurrentLocation.getLastLocation(requireContext()) { userLatitude, userLongitude, address ->
                hideProgress()
                val defaultPoint = Location("default")
                defaultPoint.latitude = userLatitude
                defaultPoint.longitude = userLongitude

                currentLocation = defaultPoint
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this)
                } else {
                    Log.w("TAG", "Cant start map")
                }
                configureCameraIdle()

            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap ?: return
        mMap!!.clear()
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap?.setOnCameraIdleListener(onCameraIdleListener)
        mMap?.setMyLocationEnabled(true)

        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)

        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
    }

    private fun configureCameraIdle() {
        onCameraIdleListener = GoogleMap.OnCameraIdleListener {
            val latLng = mMap!!.cameraPosition.target
            val geocoder = Geocoder(requireContext())
            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.size > 0) {
                    val locality = addressList[0].getAddressLine(0)
                    val country = addressList[0].countryName
                    val PostalCode = addressList[0].postalCode
                    val AdminArea = addressList[0].adminArea
                    val getSubAdminArea = addressList[0].subAdminArea
                    val cityname = addressList[0].locality
                    val feature = addressList[0].featureName
                    //newAddress = locality
                    val defaultPoint = Location("default")
                    defaultPoint.latitude = latLng.latitude
                    defaultPoint.longitude = latLng.longitude

                    currentLocation = defaultPoint

                    newAddress = feature + " " + cityname





                    binding.tvfeature.text = feature
                    binding.tvcity.text = cityname
                    Log.e("cityname", "" + cityname)
                    Log.e("feature", "" + feature)

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}