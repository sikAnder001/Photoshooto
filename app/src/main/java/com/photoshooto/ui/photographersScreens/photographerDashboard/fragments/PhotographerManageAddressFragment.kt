package com.photoshooto.ui.photographersScreens.photographerDashboard.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.databinding.DialogRemoveStandeeCartBinding
import com.photoshooto.databinding.FragmentPhotographerManageAddressBinding
import com.photoshooto.domain.adapter.SelectAddressAdapter
import com.photoshooto.domain.model.AddressElement
import com.photoshooto.domain.usecase.manage_address.ManageAddressViewModel
import com.photoshooto.ui.manage_address.AddEditAddressFragment
import com.photoshooto.util.KEY_DATA
import com.photoshooto.util.KEY_RESULT
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotographerManageAddressFragment : Fragment() {

    companion object {
        const val REQUEST_ADDRESS_CHANGE = "request_address_change"
    }

    private lateinit var binding: FragmentPhotographerManageAddressBinding
    private val viewModel: ManageAddressViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(AddEditAddressFragment.REQUEST_ADD_EDIT_ADDRESS) { requestKey, bundle ->
            val result = bundle.getInt(KEY_RESULT, Activity.RESULT_CANCELED)
            if (result == Activity.RESULT_OK) {
                viewModel.getAddress()
            }
        }
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            getAddressResponse.observe(
                requireActivity(),
                Observer { result ->
                    if (result.success) {
                        result.data?.list?.let {
                            binding.apply {
                                recyclerViewAddress.layoutManager =
                                    LinearLayoutManager(requireContext())
                                recyclerViewAddress.adapter = SelectAddressAdapter(it as ArrayList)
                                (recyclerViewAddress.adapter as SelectAddressAdapter).onItemClickListener =
                                    object : SelectAddressAdapter.OnItemClickListener {
                                        override fun onItemClick(
                                            position: Int,
                                            data: AddressElement
                                        ) {
                                            this@PhotographerManageAddressFragment.setFragmentResult(
                                                REQUEST_ADDRESS_CHANGE, bundleOf(
                                                    KEY_DATA to Gson().toJson(data)
                                                )
                                            )
                                            findNavController().popBackStack()
                                        }

                                        override fun onEditClick(
                                            position: Int,
                                            data: AddressElement
                                        ) {
                                            if (requireContext().isInternetAvailable()) {
                                                val jsonData = Gson().toJson(data)
                                                val bundle = Bundle()
                                                bundle.putString("addressData", jsonData)
                                                findNavController().navigate(
                                                    R.id.action_photographerManageAddressFragment_to_photographerAddAddressFragment,
                                                    bundle
                                                )
                                                /*findNavController().navigate(
                                                    SelectAddressFragmentDirections.actionMoveToAddAddress(
                                                        jsonData
                                                    )
                                                )*/
                                            } else onToast(
                                                getString(R.string.internet_check),
                                                requireContext()
                                            )
                                        }

                                        override fun onDeleteClick(
                                            position: Int,
                                            data: AddressElement
                                        ) {
                                            removeAddressConfirmation(data)
                                        }
                                    }
                            }
                        }
                    } else {
                        onToast(result.message, requireContext())
                    }
                }
            )

            deleteAddressResponse.observe(
                requireActivity()
            ) { result ->
                if (result.success!!) {
                    viewModel.getAddress()
                } else {
                    onToast(result.message!!, requireContext())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerManageAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()

        if (requireContext().isInternetAvailable()) {
            if (viewModel.getAddressResponse.value == null) {
                viewModel.getAddress()
            } else initObserver()
        } else {
            onToast(getString(R.string.internet_check), requireContext())
        }
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnAddAddress.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    val bundle = Bundle()
                    bundle.putString("addressData", "")
                    findNavController().navigate(
                        R.id.action_photographerManageAddressFragment_to_photographerAddAddressFragment,
                        bundle
                    )
                    /* findNavController().navigate(
                         SelectAddressFragmentDirections.actionMoveToAddAddress(
                             ""
                         )
                     )*/
                } else onToast(getString(R.string.internet_check), requireContext())
            }
        }
    }

    private fun removeAddressConfirmation(data: AddressElement) {
        val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        val dialogBinding = DialogRemoveStandeeCartBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            cardNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            cardYes.setOnClickListener {
                bottomSheetDialog.dismiss()
                viewModel.deleteAddress(data.id!!)
            }
        }
        bottomSheetDialog.show()
    }
}