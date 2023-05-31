package com.photoshooto.ui.qrorderhistory

//import com.photoshooto.domain.adapter.EventOrderHistoryTabAdapter
import EventOrderHistoryTabAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.databinding.FragmentQreventOrderHistoryBinding


class QREventOrderHistoryFragment : Fragment() {

    private lateinit var binding: FragmentQreventOrderHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentQreventOrderHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        val eventOrderHistoryTabAdapter =
            EventOrderHistoryTabAdapter(childFragmentManager, binding.orderTabLayout)
        binding.viewpagerEvent.adapter = eventOrderHistoryTabAdapter
        binding.orderTabLayout.setupWithViewPager(binding.viewpagerEvent)

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }


    private fun clickListener() {
        binding.apply {

        }
    }

}