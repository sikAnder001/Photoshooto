package com.photoshooto.ui.userhomepage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photoshooto.R
import com.photoshooto.databinding.AdapterPhotographyServicesItemsBinding
import com.photoshooto.domain.model.CityData

class OtherCitiesAdapter(
    val data: List<CityData>?,
    var context: Context,
    val onOtherCitySelectedListener: OnOtherCitySelectedListener
) :
    RecyclerView.Adapter<OtherCitiesAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            AdapterPhotographyServicesItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.tvTitle.text = data?.get(position)?.name
        val icon = data?.get(position)?.icon
        Glide.with(context)
            .load(icon)
            .placeholder(R.drawable.ic_popular_city)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.profileImage)
        holder.binding.lvPhotographyType.setOnClickListener {
            onOtherCitySelectedListener.onOtherCitySelected(data?.get(position)!!)
        }
    }

    override fun getItemCount(): Int {
        return data?.size!!
    }


    class Holder(
        val binding: AdapterPhotographyServicesItemsBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }

    interface OnOtherCitySelectedListener {
        fun onOtherCitySelected(city: CityData)
    }

}