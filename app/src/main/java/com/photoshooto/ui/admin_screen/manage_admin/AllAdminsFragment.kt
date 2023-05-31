package com.photoshooto.ui.admin_screen.manage_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.photoshooto.R
import com.photoshooto.databinding.FragmentAllAdminsBinding
import com.photoshooto.domain.adapter.AdminAdapter
import com.photoshooto.domain.model.UserElement
import com.photoshooto.domain.usecase.manage_admin.ManageAdminViewModel
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllAdminsFragment : Fragment() {

    private lateinit var binding: FragmentAllAdminsBinding
    private val viewModel: ManageAdminViewModel by viewModel()
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = requireArguments().getString("type")
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            when (type) {
                "super_admin" -> {
                    superAdminResponse.observe(
                        requireActivity(),
                        Observer { result ->
                            result.success?.let {
                                if (it) {
                                    result.data?.list?.let {
                                        binding.apply {
                                            recyclerView.layoutManager =
                                                GridLayoutManager(requireContext(), 3)
                                            recyclerView.adapter = AdminAdapter(it as ArrayList)
                                            (recyclerView.adapter as AdminAdapter).onItemClickListener =
                                                object : AdminAdapter.OnItemClickListener {
                                                    override fun onDetailsClick(
                                                        position: Int,
                                                        data: UserElement
                                                    ) {
                                                        if (requireContext().isInternetAvailable()) {
                                                            findNavController().navigate(
                                                                ManageFragmentDirections.actionAdminDetails(
                                                                    data.id ?: ""
                                                                )
                                                            )
                                                        } else onToast(
                                                            getString(R.string.internet_check),
                                                            requireContext()
                                                        )
                                                    }
                                                }
                                        }
                                    }
                                } else {
                                    result.message?.let {
                                        onToast(it, requireContext())
                                    }
                                }
                            }
                        }
                    )
                }
                else -> {
                    adminResponse.observe(
                        requireActivity(),
                        Observer { result ->
                            result.success?.let {
                                if (it) {
                                    result.data?.list?.let {
                                        binding.apply {
                                            val activeAdmins =
                                                it.filter { x -> x.status == "active" } as ArrayList
                                            val inactiveAdmins =
                                                it.filter { x -> x.status != "active" } as ArrayList
                                            recyclerView.layoutManager =
                                                GridLayoutManager(requireContext(), 3)
                                            if (type == "admin") {
                                                recyclerView.adapter = AdminAdapter(activeAdmins)
                                            } else recyclerView.adapter =
                                                AdminAdapter(inactiveAdmins, true)
                                            (recyclerView.adapter as AdminAdapter).onItemClickListener =
                                                object : AdminAdapter.OnItemClickListener {
                                                    override fun onDetailsClick(
                                                        position: Int,
                                                        data: UserElement
                                                    ) {
                                                        if (requireContext().isInternetAvailable()) {
                                                            findNavController().navigate(
                                                                AllAdminsFragmentDirections.actionAdminDetails(
                                                                    data.id ?: ""
                                                                )
                                                            )
                                                        } else onToast(
                                                            getString(R.string.internet_check),
                                                            requireContext()
                                                        )
                                                    }
                                                }
                                        }
                                        //getSuperAdmin()
                                    }
                                } else {
                                    result.message?.let {
                                        onToast(it, requireContext())
                                    }
                                }
                            }
                        }
                    )
                }
            }

            messageData.observe(
                requireActivity(),
                Observer { result ->
                    onToast(result, requireContext())
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllAdminsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()

        when (type) {
            "super_admin" -> {
                if (viewModel.superAdminResponse.value == null) {
                    if (requireContext().isInternetAvailable()) {
                        viewModel.getSuperAdmin()
                    } else {
                        onToast(getString(R.string.internet_check), requireContext())
                    }
                } else initObserver()
            }
            else -> {
                if (viewModel.adminResponse.value == null) {
                    if (requireContext().isInternetAvailable()) {
                        viewModel.getAdmin()
                    } else {
                        onToast(getString(R.string.internet_check), requireContext())
                    }
                } else initObserver()
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}
