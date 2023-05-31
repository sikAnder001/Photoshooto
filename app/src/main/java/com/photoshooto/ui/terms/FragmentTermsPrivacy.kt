package com.photoshooto.ui.terms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.photoshooto.databinding.FragmentTermsprivacyBinding


class FragmentTermsPrivacy : Fragment() {

    private lateinit var binding: FragmentTermsprivacyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsprivacyBinding.inflate(inflater, container, false)

        val data = requireArguments().get("data").toString()
        binding.tvTermsOrPrivacy.fromAsset(data)
            .defaultPage(0)
            .spacing(10)
            .load()

        return binding.root
    }
}