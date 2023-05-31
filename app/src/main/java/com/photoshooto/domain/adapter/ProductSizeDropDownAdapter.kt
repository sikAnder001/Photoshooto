package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.photoshooto.databinding.AdapterSpinnerProductSizeBinding
import com.photoshooto.databinding.ViewSpinnerProductSizeBinding

class ProductSizeDropDownAdapter(var contextt: Context, var sizeList: ArrayList<String>) :
    ArrayAdapter<String>(contextt, 0, sizeList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rawViewForSpinner(parent, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rawView(parent, position)
    }

    private fun rawView(parent: ViewGroup, position: Int): View {
        val binding =
            AdapterSpinnerProductSizeBinding.inflate(LayoutInflater.from(contextt), parent, false)
        binding.tvSize.text = sizeList[position]
        return binding.root
    }

    private fun rawViewForSpinner(parent: ViewGroup, position: Int): View {
        val binding =
            ViewSpinnerProductSizeBinding.inflate(LayoutInflater.from(contextt), parent, false)
        binding.tvSize.text = sizeList[position]
        return binding.root
    }
}
