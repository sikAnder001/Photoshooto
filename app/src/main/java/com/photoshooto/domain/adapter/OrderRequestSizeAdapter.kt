package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AdapterOrderRequestSizeBinding
import com.photoshooto.domain.model.SizesItem


class OrderRequestSizeAdapter(var sizes: List<SizesItem>) :
    RecyclerView.Adapter<OrderRequestSizeAdapter.ViewPagerHolder>() {

    companion object {
        var selectedItemPosition = -1
        var context: Context? = null
    }


    class ViewPagerHolder(var binding: AdapterOrderRequestSizeBinding) :
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
            AdapterOrderRequestSizeBinding.inflate(
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
