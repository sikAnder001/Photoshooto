package com.photoshooto.ui.photographersScreens.surpises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.databinding.FragmentPhotographerUnlockFeaturesBinding
import com.photoshooto.ui.photographersScreens.photographerDashboard.GiftItemsAdapter


class PhotographerUnlockFeaturesFragment : Fragment(), GiftItemsAdapter.OnItemClickListener {

    lateinit var binding: FragmentPhotographerUnlockFeaturesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerUnlockFeaturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.rvGiftItems.adapter = GiftItemsAdapter(requireActivity(), this)

    }

    override fun onItemClick(position: Int) {
        findNavController().navigate(R.id.action_photographerUnlockFeaturesFragment_to_photographerSurpriseDetailsFragment)
    }


}