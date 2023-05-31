package com.photoshooto.ui.userhomepage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterQrViewpagerImageSliderBinding
import com.photoshooto.domain.model.UserDashboardModel

class OffersListAdapter(
    var images: List<UserDashboardModel.Data.Banner?>,
    private val listener: onClickListener
) :
    RecyclerView.Adapter<OffersListAdapter.ViewPagerHolder>() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterQrViewpagerImageSliderBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    inner class ViewPagerHolder(private var binding: AdapterQrViewpagerImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: UserDashboardModel.Data.Banner) {
            val bannerImage = image.background_image!!
            Glide.with(context).load(bannerImage).into(binding.picOfDayImage)

            binding.rootView.setOnClickListener {
                listener.onItemClick(image)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(images[position]!!)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    interface onClickListener {
        fun onItemClick(image: UserDashboardModel.Data.Banner)
    }
}