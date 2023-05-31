package com.photoshooto.ui.photographersScreens.enquiries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.photoshooto.R
import com.photoshooto.databinding.PhotographerEnquiryFragmentBinding

class PhotographerEnquiryFragment : Fragment(), TabLayout.OnTabSelectedListener,
    View.OnClickListener {

    private lateinit var binding: PhotographerEnquiryFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PhotographerEnquiryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarMyEnquiry

        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        val titleTxt = toolbar.tvTitle
        titleTxt.text = "My Enquires"

        binding.all.setOnClickListener(this)
        binding.read.setOnClickListener(this)
        binding.unread.setOnClickListener(this)

        loadDefaultAllEnquiries()
        binding.rvEnquiry.isVisible = true
        binding.rvEnquiry.adapter = PhotographerEnquiryAdapter(requireActivity(), "all")
        binding.rvEnquiry.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        binding.rvEnquiry.adapter = PhotographerEnquiryAdapter(requireActivity(), "all")
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.all -> {
                loadDefaultAllEnquiries()
            }

            R.id.read -> {
                binding.all.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
                binding.read.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.white_bg_corners)
                binding.unread.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
                updateAdapter("read")
            }

            R.id.unread -> {
                binding.all.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
                binding.read.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
                binding.unread.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.white_bg_corners)
                updateAdapter("unread")
            }
        }
    }

    private fun loadDefaultAllEnquiries() {
        binding.all.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.white_bg_corners)
        binding.read.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
        binding.unread.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
        updateAdapter("all")
    }

    private fun updateAdapter(s: String) {
        binding.rvEnquiry.adapter = PhotographerEnquiryAdapter(requireActivity(), s)
    }
}