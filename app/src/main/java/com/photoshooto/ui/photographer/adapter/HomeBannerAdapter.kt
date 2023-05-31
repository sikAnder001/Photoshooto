package com.photoshooto.ui.photographer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterPicOfDayLandingScreenBinding
import com.photoshooto.domain.model.Banner

class HomeBannerAdapter(val list: List<Banner>?, private val itemListener: OnItemClickListener) :
    RecyclerView.Adapter<HomeBannerAdapter.Holder>() {

    inner class Holder(var binding: AdapterPicOfDayLandingScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Banner?) {
            Glide.with(binding.root.context).load(data?.background_image!!)
                .into(binding.picOfDayImage)

            binding.banner.setOnClickListener {
                itemListener.onBannerClick(data)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerAdapter.Holder {
        return Holder(
            AdapterPicOfDayLandingScreenBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeBannerAdapter.Holder, position: Int) {
        holder.bind(list?.get(position))
    }

    override fun getItemCount(): Int = list?.size!!

    interface OnItemClickListener {
        fun onBannerClick(data: Banner)
    }
}