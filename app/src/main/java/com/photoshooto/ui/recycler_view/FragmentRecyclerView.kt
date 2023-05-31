package com.photoshooto.ui.recycler_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.databinding.FragmentRecyclerViewBinding

class FragmentRecyclerView : Fragment() {

    private lateinit var binding: FragmentRecyclerViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerViewBinding.inflate(inflater, container, false)


        val toolbar = binding.toolbarRecyclerView

        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        val titleTxt = toolbar.tvTitle
        titleTxt.text = activity?.getString(R.string.label_recycler_view)

        return binding.root
    }

}