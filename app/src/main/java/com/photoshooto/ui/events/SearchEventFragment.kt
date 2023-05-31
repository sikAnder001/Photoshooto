package com.photoshooto.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.FragmentSearchEventBinding


class SearchEventFragment : Fragment() {
    private var newBinding: FragmentSearchEventBinding? = null
    private val binding get() = newBinding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        newBinding = FragmentSearchEventBinding.inflate(inflater, container, false)

        initview()


        return binding.root
    }

    private fun initview() {

        newBinding?.ivFilter?.setOnClickListener {
            showbottomSheetfilter()

        }


    }

    private fun showbottomSheetfilter() {

        var dailog = BottomSheetDialog(requireContext(), R.style.AppTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_filter_events, null)
        val btnClearE: Button = dailog.findViewById(R.id.tvClear)!!
        dailog.apply {
            setContentView(view)
            setCancelable(false)
            btnClearE.setOnClickListener {
                dismiss()
            }

            view.apply {


            }


        }.show()
    }


}