package com.photoshooto.ui.manage_address

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.text.trimmedLength
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.databinding.DialogSelectStateBinding
import com.photoshooto.databinding.FragmentAddEditAddressBinding
import com.photoshooto.domain.adapter.StateListAdapter
import com.photoshooto.domain.model.AddressElement
import com.photoshooto.domain.model.Location
import com.photoshooto.domain.model.StatesData
import com.photoshooto.domain.usecase.manage_address.ManageAddressViewModel
import com.photoshooto.ui.job.utility.GetCurrentLocation
import com.photoshooto.ui.job.utility.openPermissionSettings
import com.photoshooto.ui.job.utility.toastError
import com.photoshooto.ui.photographer.FragmentPhotographerAddress
import com.photoshooto.util.*
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddEditAddressFragment : Fragment() {
    private lateinit var fusedLocationProvider: FusedLocationProviderClient


    companion object {
        const val REQUEST_ADD_EDIT_ADDRESS = "request_add_edit_address"
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    private lateinit var binding: FragmentAddEditAddressBinding
    private val viewModel: ManageAddressViewModel by viewModel()
    private var addressElement: AddressElement? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun initObserver() {
        viewModel.apply {
            addAddressResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            this@AddEditAddressFragment.setFragmentResult(
                                REQUEST_ADD_EDIT_ADDRESS,
                                bundleOf(KEY_RESULT to Activity.RESULT_OK)
                            )
                            findNavController().popBackStack()
                        } else {
                            result.message?.let { msg -> onToast(msg, requireContext()) }
                        }
                    }
                }
            )
            getStatesResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (!it) {
                            result.message?.let { msg -> onToast(msg, requireContext()) }
                        }
                    }
                }
            )

            deleteAddressResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {

                        } else {
                            result.message?.let { msg -> onToast(msg, requireContext()) }
                        }
                    }
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditAddressBinding.inflate(inflater, container, false)
        val dataJson = requireArguments().getString("addressData")
        if (!dataJson.isNullOrEmpty()) {
            addressElement = Gson().fromJson(dataJson, AddressElement::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        initView()

        if (requireContext().isInternetAvailable()) {
            viewModel.getStates()
        } else {
            onToast(getString(R.string.internet_check), requireContext())
        }
    }

    private fun initView() {
        addressElement?.let {
            binding.apply {
                tvTitle.text = getString(R.string.edit_address)
                edtOfficeHouseNo.setText(it.flat_no)
                edtLandmark.setText(it.landmark)
                edtCity.setText(it.city)
                edtSocietyName.setText(it.address)
                edtPincode.setText(it.pincode)
                edtState.setText(it.state)
                if (it.type == "Office") {
                    rbOffice.isChecked = true
                } else rbHome.isChecked = true
                if (it.is_default) {
                    checkboxMakeDefault.isChecked = true
                }
                btnAdd.text = getString(R.string.update)
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }






            location.setOnClickListener {

                getLocation()

            }




            edtState.setOnClickListener {
                viewModel.getStatesResponse.value.apply {
                    if (this == null) {
                        onToast(getString(R.string.label_some_thing_went_wrong), requireContext())
                    } else if ((this.data ?: arrayListOf()).isEmpty()) {
                        onToast(getString(R.string.label_some_thing_went_wrong), requireContext())
                    } else {
                        selectState()
                    }
                }
            }
            btnAdd.setOnClickListener {
                if (!requireContext().isInternetAvailable()) {
                    onToast(getString(R.string.internet_check), requireContext())
                    return@setOnClickListener
                }
                if (validate()) {
                    binding.apply {
                        if (addressElement == null) {
                            viewModel.addAddress(
                                edtSocietyName.text.toString(),
                                edtCity.text.toString(),
                                edtOfficeHouseNo.text.toString(),
                                checkboxMakeDefault.isChecked, edtLandmark.text.toString(), 0, 0,
                                edtPincode.text.toString(),
                                edtState.text.toString(),
                                if (rbOffice.isChecked) "Office" else "Home"
                            )
                        } else {
                            viewModel.updateAddress(
                                addressElement?.id ?: "",
                                edtSocietyName.text.toString(),
                                edtCity.text.toString(),
                                edtOfficeHouseNo.text.toString(),
                                checkboxMakeDefault.isChecked, edtLandmark.text.toString(), 0, 0,
                                edtPincode.text.toString(),
                                edtState.text.toString(),
                                if (rbOffice.isChecked) "Office" else "Home"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setUserData(location: Location) {


    }

    private fun selectState() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogSelectStateBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter =
                StateListAdapter(viewModel.getStatesResponse.value?.data as ArrayList)
            (recyclerView.adapter as StateListAdapter).onItemClickListener =
                object : StateListAdapter.OnItemClickListener {
                    override fun onItemClick(value: StatesData) {
                        bottomSheetDialog.dismiss()
                        binding.edtState.setText(value.name)
                    }
                }
        }
        bottomSheetDialog.show()
    }

    private fun validate(): Boolean {
        var status = true
        binding.apply {
            if (radioGroup.checkedRadioButtonId == -1) {
                onToast(getString(R.string.label_select_address_type), requireContext())
                status = false
            } else if (edtOfficeHouseNo.text.toString().isEmpty()) {
                edtOfficeHouseNo.error = getString(R.string.is_required)
                status = false
            } else if (edtCity.text.toString().isEmpty()) {
                edtCity.error = getString(R.string.is_required)
                status = false
            } else if (edtSocietyName.text.toString().isEmpty()) {
                edtSocietyName.error = getString(R.string.is_required)
                status = false
            } else if (edtPincode.text.toString().isEmpty()) {
                edtPincode.error = getString(R.string.is_required)
                status = false
            } else if (edtPincode.text.toString().length < 6) {
                edtPincode.error = getString(R.string.is_pincode)
                status = false
            } else if (!isValidPinCode(edtPincode.text.toString())) {
                edtPincode.error = getString(R.string.is_not_start_with_zero)
                status = false
            } else if (edtState.text.toString().isEmpty()) {
                edtState.error = getString(R.string.is_required)
                status = false
            }
        }
        return status
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

            val snackbar: Snackbar = Snackbar
                .make(
                    binding.root,
                    getString(R.string.permission_always_denied),
                    Snackbar.LENGTH_INDEFINITE
                )
                .setAction("Settings") {
                    activity?.openPermissionSettings()
                }
            snackbar.show()
            return
        }

        val isAllPermissionGranted = permissions.all {
            it.isGranted
        }

        if (isAllPermissionGranted) {
            GetCurrentLocation.getLastLocation(requireContext()) { _, _, address ->
                try {
                    binding.edtOfficeHouseNo.setText("")
                    var googleAddress = ""
                    if (address.isNotEmpty()) {
                        googleAddress = (address[0].locality)
                    }
                    binding.edtOfficeHouseNo.setText(googleAddress)
                    binding.edtOfficeHouseNo.setSelection(
                        binding.edtOfficeHouseNo.text.toString().trimmedLength()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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
            FragmentPhotographerAddress.MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == FragmentPhotographerAddress.MY_PERMISSIONS_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    /*  private fun setUserData(location: Location) {


      }
  */


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProvider.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: android.location.Location? = task.result


                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val completeAddress = list?.get(0)

                        if (completeAddress != null) {

                            val address: String =
                                completeAddress.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


                            val society: String =
                                checkStringReturnValue(completeAddress.subLocality)
                            val city: String = checkStringReturnValue(completeAddress.locality)
                            val state: String =
                                checkStringReturnValue(completeAddress.adminArea)
                            val postalCode: String =
                                checkStringReturnValue(completeAddress.postalCode)
                            val featureName: String =
                                completeAddress.featureName



                            binding.edtCity.setText(city)
                            binding.edtState.setText(state)


                            // binding.editState.text = state
                            //  binding.edtLandmark.setText(featureName)
                            binding.edtOfficeHouseNo.setText(featureName)
                            binding.edtPincode.setText(postalCode)
                            binding.edtSocietyName.setText(society)

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
