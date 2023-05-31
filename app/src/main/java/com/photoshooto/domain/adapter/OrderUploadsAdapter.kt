package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterOrderUploadBinding


class OrderUploadsAdapter(var sizes: List<String>) :
    RecyclerView.Adapter<OrderUploadsAdapter.ViewPagerHolder>() {

    companion object {
        var selectedItemPosition = -1
        var context: Context? = null
    }


    class ViewPagerHolder(var binding: AdapterOrderUploadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            Glide.with(context!!).load(data)
                .into(binding.ivUploadImg)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterOrderUploadBinding.inflate(
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
