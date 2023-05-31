package com.photoshooto.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.photoshooto.databinding.FragmentQrEventsBinding

class QrEventsFragment : Fragment() {

    private lateinit var qrEventBinding: FragmentQrEventsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        qrEventBinding = FragmentQrEventsBinding.inflate(inflater, container, false)


        qrEventBinding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return qrEventBinding.root
    }


}