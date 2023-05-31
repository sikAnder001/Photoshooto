package com.photoshooto.ui.userhomepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.photoshooto.databinding.FragmentUserEnquiriesBinding
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.userhomepage.adapters.UserMyEnquiriesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserEnquiriesFragment : Fragment() {

    private lateinit var binding: FragmentUserEnquiriesBinding

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserEnquiriesBinding.inflate(inflater, container, false)
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

        profileViewModel.getEnquiryAPI()

        profileViewModel.getEnquiryResponse.observe(requireActivity(), Observer {
            if (it.success != null) {
                binding.recyclerView.adapter = UserMyEnquiriesAdapter(it.data?.list!!)
            }
        })

        profileViewModel.showProgressbar.observe(requireActivity(), Observer {
            binding.progressBarCommon.visibility = if (it!!) View.VISIBLE else View.GONE
        })


    }

}