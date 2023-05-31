package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.photoshooto.databinding.AdapterOrderSizeBinding

import com.photoshooto.domain.model.SizesItem


class OrderSizeAdapter(var sizes: List<SizesItem>) :
    RecyclerView.Adapter<OrderSizeAdapter.ViewPagerHolder>() {

    companion object {
        var selectedItemPosition = -1
        var context: Context? = null
    }


    class ViewPagerHolder(var binding: AdapterOrderSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SizesItem) {
            if (data.is_avilable) {
                binding.tvSize.isEnabled = true
                binding.tvSize.text = "${data.size}(${data.quantity}), "

            } //else binding.tvSize.isEnabled = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterOrderSizeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(sizes[position])


    }

    override fun getItemCount(): Int {
        return sizes.size
    }
}
