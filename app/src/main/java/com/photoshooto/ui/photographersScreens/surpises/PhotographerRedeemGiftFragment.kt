package com.photoshooto.ui.photographersScreens.surpises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.BottomsheetGiftBinding
import com.photoshooto.databinding.FragmentPhotographerRedeemGiftBinding


class PhotographerRedeemGiftFragment : Fragment() {
    lateinit var binding: FragmentPhotographerRedeemGiftBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhotographerRedeemGiftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val bottomsheetGift =
            this.let { it1 -> BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog) }
        layoutInflater.inflate(R.layout.bottomsheet_gift, null)

        val giftBottomsheetBinding = BottomsheetGiftBinding.inflate(layoutInflater)

        giftBottomsheetBinding.btnAvailNow.setOnClickListener(View.OnClickListener {
            bottomsheetGift.dismiss()
            findNavController().navigate(R.id.action_photographerRedeemGiftFragment_to_photographerUnlockFeaturesFragment)
        })

        bottomsheetGift.setCancelable(true)
        bottomsheetGift.setContentView(giftBottomsheetBinding.root)
        bottomsheetGift.show()
    }

}