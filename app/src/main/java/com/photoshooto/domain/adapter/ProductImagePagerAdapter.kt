package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterProductImageBinding

class ProductImagePagerAdapter(var images: List<String>) :
    RecyclerView.Adapter<ProductImagePagerAdapter.ViewPagerHolder>() {
    companion object {
        var context: Context? = null
    }

    class ViewPagerHolder(private var binding: AdapterProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            context?.apply {
                Glide.with(this)
                    .load(image)
                    .into(binding.ivProductImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterProductImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }
}
