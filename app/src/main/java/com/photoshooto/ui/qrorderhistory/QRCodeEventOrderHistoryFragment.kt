package com.photoshooto.ui.qrorderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.photoshooto.R
import com.photoshooto.databinding.FragmentQrCodeEventOrderHistoryBinding
import com.photoshooto.domain.adapter.QREventOrderHistoryAdapter
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class QRCodeEventOrderHistoryFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentQrCodeEventOrderHistoryBinding
    private val viewModel: QREventHistoryViewModel by viewModel()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentQrCodeEventOrderHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()

        viewModel.getQREventOrderHistory(0, 0)
        initView()
    }

    private fun clickListener() {
        binding.apply {
            liViewAll.setOnClickListener {
                findNavController().navigate(QRCodeEventOrderHistoryFragmentDirections.actionFragmentQRcodeToFragmentEventTab())
            }
        }
    }

    private fun initView() {
        with(viewModel) {
            eventOrderHistoyDetails.observe(
                requireActivity(),
                Observer { eventOrderHistoyDetails ->
                    eventOrderHistoyDetails.success?.let { it ->
                        if (it) {
                            eventOrderHistoyDetails.data?.list?.let { eventorderList ->
                                eventorderList[0].let { eventorderhistory ->
                                    binding.apply {

                                        liEmptyWallet.isVisible = false
                                        recyclerQrEventList.isVisible = true
                                        recyclerQrEventList.layoutManager =
                                            LinearLayoutManager(requireContext())
                                        recyclerQrEventList.adapter = QREventOrderHistoryAdapter(
                                            requireActivity(),
                                            eventorderList
                                        )
                                    }
                                }
                            }
                        } else {
                            eventOrderHistoyDetails.message?.let { msg ->
                                onToast(
                                    msg,
                                    requireContext()
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            (R.id.li_view_all) -> {
                // navController.navigate(R.id.navigation_routing)
                findNavController().navigate(QRCodeEventOrderHistoryFragmentDirections.actionFragmentQRcodeToFragmentEventTab())
            }
        }

    }

}