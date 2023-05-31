package com.photoshooto.ui.job.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.DialogBottomSortBinding
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetSortDialog : BottomSheetDialogFragment() {

    lateinit var binding: DialogBottomSortBinding
    private val jobHomeViewModel: JobViewModel by viewModel()
    private val navArgs: BottomSheetSortDialogArgs by navArgs()
    var sortBY = ""

    private val NEARBY = "Nearby"
    private val RECENT = "Recently Posted"
    private val RATING = "Rating"
    private val VERIFIED = "Verified"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DialogBottomSortBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancel.setSafeOnClickListener {
            findNavController().popBackStack()
        }

        binding.clear.setSafeOnClickListener {
            binding.rdGroup.clearCheck()
            sortBY = ""
        }

        when (navArgs.prefill) {
            NEARBY -> {
                binding.radioNearby.isChecked = true

            }
            RECENT -> {
                binding.radioRecent.isChecked = true
            }
            RATING -> {
                binding.radioRating.isChecked = true
            }
            VERIFIED -> {
                binding.radioVerified.isChecked = true
            }
        }

        if (navArgs.type.equals("Hire Me", true)) {
            binding.radioVerified.text = "Verified Profile"
        } else {
            binding.radioVerified.text = "Verified Job"
        }

        binding.btnApply.setSafeOnClickListener {
            requireActivity().supportFragmentManager
                .setFragmentResult("dialog_sort", Bundle().apply {
                    putString("sort_by", sortBY)
                })
            findNavController().popBackStack()
        }


        binding.rdGroup.setOnCheckedChangeListener { _, radioId ->
            when (radioId) {
                R.id.radioRecent -> {
                    sortBY = RECENT
                }
                R.id.radioNearby -> {
                    sortBY = NEARBY
                }
                R.id.radioRating -> {
                    sortBY = RATING
                }
                R.id.radioVerified -> {
                    sortBY = VERIFIED
                }
            }
        }
    }
}