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
import com.photoshooto.databinding.FragmentManageBinding
import com.photoshooto.domain.adapter.AdminAdapter
import com.photoshooto.domain.model.UserElement
import com.photoshooto.domain.usecase.manage_admin.ManageAdminViewModel
import com.photoshooto.ui.admin_screen.AdminDashboardActivity
import com.photoshooto.util.hide
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.onToast
import com.photoshooto.util.show
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageFragment : Fragment() {

    private lateinit var binding: FragmentManageBinding
    private val viewModel: ManageAdminViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            adminResponse.observe(requireActivity(), Observer { result ->
                result.success?.let {
                    if (it) {
                        result.data?.list?.let {
                            binding.apply {
                                var activeAdmins =
                                    it.filter { x -> x.status == "active" } as ArrayList
                                if (activeAdmins.size > 3) {
                                    // activeAdmins = activeAdmins.subList(0, 2) as ArrayList
                                    btnAdminViewAll.show()
                                } else btnAdminViewAll.hide()

                                var inactiveAdmins =
                                    it.filter { x -> x.status != "active" } as ArrayList
                                if (inactiveAdmins.size > 3) {
                                    // inactiveAdmins = inactiveAdmins.subList(0, 2) as ArrayList
                                    btnInactiveAdminViewAll.show()
                                } else btnInactiveAdminViewAll.hide()

                                recyclerViewAdmins.layoutManager =
                                    GridLayoutManager(requireContext(), 3)
                                recyclerViewInactiveAdmins.layoutManager =
                                    GridLayoutManager(requireContext(), 3)
                                recyclerViewAdmins.adapter = AdminAdapter(activeAdmins)
                                recyclerViewInactiveAdmins.adapter =
                                    AdminAdapter(inactiveAdmins, true)
                                (recyclerViewAdmins.adapter as AdminAdapter).onItemClickListener =
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
                                (recyclerViewInactiveAdmins.adapter as AdminAdapter).onItemClickListener =
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
                                (recyclerViewAdmins.adapter as AdminAdapter).notifyDataSetChanged()
                            }
                            getSuperAdmin()
                        }
                    } else {
                        binding.btnAdminViewAll.hide()
                        binding.btnInactiveAdminViewAll.hide()
                        result.message?.let {
                            onToast(it, requireContext())
                        }
                    }
                }
            }
            )
            superAdminResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            result.data?.list?.let {
                                binding.apply {
                                    var superAdminList = it as ArrayList
                                    if (superAdminList.size > 3) {
                                        btnSuperAdminViewAll.show()
                                        superAdminList = superAdminList.subList(0, 2) as ArrayList
                                    } else btnSuperAdminViewAll.hide()
                                    recyclerViewSuperAdmins.layoutManager =
                                        GridLayoutManager(requireContext(), 3)
                                    recyclerViewSuperAdmins.adapter = AdminAdapter(superAdminList)
                                    (recyclerViewSuperAdmins.adapter as AdminAdapter).onItemClickListener =
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
            messageData.observe(requireActivity()) { result ->
//                onToast(result, requireContext())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAdmin()
        initObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()

        // initView()

        if (viewModel.adminResponse.value == null) {
            if (requireActivity().isInternetAvailable()) {
                viewModel.getAdmin()
            } else {
                onToast(getString(R.string.internet_check), requireActivity())
            }
        } else initObserver()
    }

    private fun clickListener() {
        binding.apply {
            ivSideMenu.setOnClickListener {
                (activity as AdminDashboardActivity).drawerOpen()
            }
            ivAddNewAdmin.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    findNavController().navigate(ManageFragmentDirections.actionCreateAdmin())
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            btnSuperAdminViewAll.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    findNavController().navigate(
                        ManageFragmentDirections.actionViewAllAdmins(
                            "super_admin"
                        )
                    )
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            btnAdminViewAll.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    findNavController().navigate(
                        ManageFragmentDirections.actionViewAllAdmins(
                            "admin"
                        )
                    )
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            btnInactiveAdminViewAll.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    findNavController().navigate(
                        ManageFragmentDirections.actionViewAllAdmins(
                            "inactive_admin"
                        )
                    )
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
        }
    }
}
