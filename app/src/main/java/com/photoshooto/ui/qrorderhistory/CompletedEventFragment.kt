package com.photoshooto.ui.qrorderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.photoshooto.databinding.FragmentCompletedEventBinding
import com.photoshooto.domain.adapter.QREventOrderPagerAdapter
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompletedEventFragment : Fragment() {

    companion object {
        fun newInstance() = OngoingEventFragment()
    }

    private lateinit var binding: FragmentCompletedEventBinding
    private val viewModel: OngoingEventViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompletedEventBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        val className = CompletedEventFragment::class.java.name;
        viewModel.getEventOrderHistory(0, 0, className)
        initView()
    }

    private fun clickListener() {
        binding.apply {

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

                                        recyclerEventList.layoutManager =
                                            LinearLayoutManager(requireContext())
                                        recyclerEventList.adapter = QREventOrderPagerAdapter(
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
}