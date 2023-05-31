package com.photoshooto.ui.photographer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.text.trimmedLength
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.bottomsheet.ImagePreviewBottomFragment
import com.photoshooto.bottomsheet.SetOfValuesBottomDialogFragment
import com.photoshooto.databinding.DialogSelectStateBinding
import com.photoshooto.databinding.FragmentPhotographerAddressBinding
import com.photoshooto.domain.adapter.StateListAdapter
import com.photoshooto.domain.model.Location
import com.photoshooto.domain.model.StatesData
import com.photoshooto.domain.model.UpdateProfileModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.job.utility.GetCurrentLocation
import com.photoshooto.ui.job.utility.openPermissionSettings
import com.photoshooto.ui.job.utility.toastError
import com.photoshooto.util.*
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class FragmentPhotographerAddress : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentPhotographerAddressBinding
    var latitude = 0.0
    var longitude = 0.0

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    //    private val viewModel: ManageAddressViewModel by viewModel()
    lateinit var customProgress: CustomProgress // used in child activities
    var regex = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$"
    //   private val navArgs: fragmentPhotographerAddress by navArgs()

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized) {
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("address")
                ?.observe(viewLifecycleOwner) {
                    Log.e("TAG", it.toString())
                    binding.editLandmark.setText(it.toString())

                }
            return binding.root
        }

        binding = FragmentPhotographerAddressBinding.inflate(inflater, container, false)


        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())


        // getLocation()


        return binding.root
    }

    /* override fun onResume() {
         super.onResume()
         getLocation()

     }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getCityList()

        profileViewModel.getStates()

        val toolbar = binding.toolbarEditAddressDetails

        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.imageMapCl.setOnClickListener {
            /* Toast.makeText(requireContext(), "hello", Toast.LENGTH_SHORT).show()*/
            //    getLocation()

            findNavController().navigate(
                FragmentPhotographerAddressDirections.actionPost1ToMap()
            )
            /* view?.findNavController()
                 ?.navigate(
                     R.id.action_fragmentPhotographerAddress_to_map
                 )
 */

            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )

            MayI.withActivity(requireActivity()).withPermissions(*permissions)
                .onRationale(this::permissionRationaleMultiLocation)
                .onResult(this::permissionResultMultiLocation)
                .onErrorListener(this::inCaseOfErrorLocation).check()
        }


        val titleTxt = toolbar.tvTitle
        titleTxt.text = activity?.getString(R.string.address_details)

        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
        customProgress = CustomProgress().getInstance()!!

        binding.btnUpdateAddress.setOnClickListener(this)
        binding.useLocationMap.setOnClickListener(this)
        binding.imgValidatedPincode.setOnClickListener(this)
        binding.editState.setOnClickListener(this)

        with(profileViewModel) {
            getUserData.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            setUserData(response.data?.data?.location!!)
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }

            updateProfileData.observe(requireActivity()) { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        onToast(response.data?.message!!, requireActivity())
                        lifecycleScope.launchWhenResumed {
                            findNavController().popBackStack()
                        }
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
            }
            getCityResponse.observe(requireActivity()) { response ->
                if (response.success!!) {
                    val adapter = ArrayAdapter(
                        requireActivity(),
                        android.R.layout.simple_list_item_1, response.data!!
                    )
                    binding.editCity.setAdapter(adapter)
                }
            }

            getStatesResponse.observe(requireActivity()) { response ->
                if (response.success!!) {

                }
            }

            showProgressbar.observe(requireActivity(), Observer { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            })
        }

    }

    private fun selectState() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        val dialogBinding = DialogSelectStateBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            recyclerView.adapter =
                StateListAdapter(profileViewModel.getStatesResponse.value?.data as ArrayList)
            (recyclerView.adapter as StateListAdapter).onItemClickListener =
                object : StateListAdapter.OnItemClickListener {
                    override fun onItemClick(value: StatesData) {
                        bottomSheetDialog.dismiss()
                        binding.editState.text = value.name
                    }
                }
        }
        bottomSheetDialog.show()
    }

    private fun validatePincode() {
        val editPincode = binding.editPincode.text.toString()
        if (editPincode.isEmpty()) {
            binding.editPincode.requestFocus()
            binding.errorPincode.visibility = View.VISIBLE
            return
        }
        val p: Pattern = Pattern.compile(regex)
        val m: Matcher = p.matcher(editPincode)
        if (!m.matches()) {
            binding.editPincode.requestFocus()
            binding.errorPincode.visibility = View.VISIBLE
            return
        }
        binding.errorPincode.visibility = View.GONE
    }

    private fun updateProfileData() {

        val editOfficeHouse = getStringFromEditText(binding.editOfficeHouse)
        if (editOfficeHouse.isEmpty()) {
            binding.editOfficeHouse.requestFocus()
            binding.errorofficeHouse.visibility = View.VISIBLE
            return
        }
        binding.errorofficeHouse.visibility = View.GONE

        val editSociety = getStringFromEditText(binding.editSociety)
        if (editSociety.isEmpty()) {
            binding.editSociety.requestFocus()
            binding.errorsociety.visibility = View.VISIBLE
            return
        }
        binding.errorsociety.visibility = View.GONE

        val editLandmark = getStringFromEditText(binding.editLandmark)
        if (editLandmark.isEmpty()) {
            binding.editLandmark.requestFocus()
            binding.errorlandmark.visibility = View.VISIBLE
            return
        }
        binding.errorlandmark.visibility = View.GONE

        val editPincode = getStringFromEditText(binding.editPincode)
        if (editPincode.isEmpty()) {
            binding.editPincode.requestFocus()
            binding.errorPincode.visibility = View.VISIBLE
            return
        }
        val p: Pattern = Pattern.compile(regex)
        val m: Matcher = p.matcher(editPincode)
        if (!m.matches()) {
            binding.editPincode.requestFocus()
            binding.errorPincode.visibility = View.VISIBLE
            return
        }
        binding.errorPincode.visibility = View.GONE

        val editCity = getStringFromEditText(binding.editCity)
        if (editCity.isEmpty()) {
            binding.editCity.requestFocus()
            binding.errorcity.visibility = View.VISIBLE
            return
        }

        val list = profileViewModel.getCityResponse.value!!.data!!
        var isValidCity = false
        for (i in list.indices) {
            if (editCity.lowercase() == list[i].name?.lowercase()) {
                isValidCity = true
                break
            }
        }

        if (isValidCity == null) {
            binding.errorcity.visibility = View.VISIBLE
            return
        }


        /*if (!isValidCity) {
            binding.errorcity.visibility = View.VISIBLE
            return
        }*/

        binding.errorcity.visibility = View.GONE

        val editState = binding.editState.text.toString()
        if (editState.isEmpty()) {
            binding.editState.requestFocus()
            binding.errorstate.visibility = View.VISIBLE
            return
        }
        binding.errorstate.visibility = View.GONE

        val updateProfileModel = UpdateProfileModel(
            flat_no = editOfficeHouse,
            address = editSociety,
            landmark = editLandmark,
            pincode = editPincode,
            city = editCity,
            state = editState,
        )
        profileViewModel.updateProfileUseCase(
            updateProfileModel, SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    private fun setUserData(location: Location) {
        binding.editOfficeHouse.setText(location.flat_no)
        binding.editCity.setText(location.city)
        binding.editState.text = location.state
        binding.editLandmark.setText(location.landmark)
        binding.editPincode.setText(location.pincode)
        binding.editSociety.setText(location.address)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnUpdateAddress -> {
                updateProfileData()
            }
            R.id.use_location_map -> {
                getLocation()
            }


            R.id.imgValidatedPincode -> {
                validatePincode()

            }
            R.id.edit_state -> {
                selectState()
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
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    @Deprecated("Deprecated in Java")
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

                            binding.editCity.setText(city)
                            binding.editState.text = state
                            binding.editLandmark.setText(featureName)
                            binding.editPincode.setText(postalCode)
                            binding.editSociety.setText(society)

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

    private fun addDocuments(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SetOfValuesBottomDialogFragment.newInstance(title, args,
            object : SetOfValuesBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    imagePath: String
                ) {

                }
            }
        )
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }

    private fun showPreview(title: String, imagePreview: String) {
        val imagePreviewBottomFragment = ImagePreviewBottomFragment.newInstance(title, imagePreview)
        imagePreviewBottomFragment.show(parentFragmentManager, "ImagePreviewBottomFragment")
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
            //  showProgress()
            GetCurrentLocation.getLastLocation(requireContext()) { userLatitude, userLongitude, address ->
                // hideProgress()
                latitude = userLatitude
                longitude = userLongitude
                try {
                    binding.editLandmark.setText("")
                    var googleAddress = ""
                    if (address.isNotEmpty()) {
                        googleAddress =
                                // (address[0].featureName) + ", " + (address[0].locality) + ", " + (address[0].adminArea)
                            (address[0].featureName) /*+ ", " + (address[0].locality) + ", " + (address[0].adminArea)*/
                    }
                    binding.editLandmark.setText(googleAddress)
                    binding.editLandmark.setSelection(
                        binding.editLandmark.text.toString().trimmedLength()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}