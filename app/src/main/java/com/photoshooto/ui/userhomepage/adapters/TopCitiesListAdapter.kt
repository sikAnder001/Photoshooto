package com.photoshooto.ui.userhomepage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photoshooto.R
import com.photoshooto.databinding.CitiesItemsBinding
import com.photoshooto.domain.model.UserDashboardModel

class TopCitiesListAdapter(
    val topCities: List<UserDashboardModel.Data.TopCity?>,
    private val listeners: OnClickListeners
) :
    RecyclerView.Adapter<TopCitiesListAdapter.Holder>() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        return Holder(
            CitiesItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val cities = topCities[position]
        holder.bind(cities!!, holder.itemView)

        val icon = topCities[position]?.icon
        val name = topCities[position]?.name
        holder.binding.tvCity.text = name!!

        when (name) {
            "Near By" -> {
                Glide.with(context)
                    .load(R.drawable.ic_near_by)
                    .placeholder(R.drawable.ic_popular_city)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.cityIcon)
            }
            "All Cities" -> {
                Glide.with(context)
                    .load(R.drawable.ic_all_cities)
                    .placeholder(R.drawable.ic_popular_city)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.cityIcon)
            }
            else -> {
                Glide.with(context)
                    .load(icon)
                    .placeholder(R.drawable.ic_popular_city)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.cityIcon)
            }
        }

        holder.binding.llMain.setOnClickListener {
            if (name == "All Cities") {
                listeners.onAllCityClick()
            } else if (name == "Near By") {
                listeners.onNearByClick()

            } else {
                listeners.onCityClick(cities.name!!)
            }


        }
    }

    override fun getItemCount(): Int {
        return topCities.size
    }


    inner class Holder(var binding: CitiesItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(city: UserDashboardModel.Data.TopCity, itemView: View) {
        }
    }

    interface OnClickListeners {
        fun onCityClick(city: String)
        fun onNearByClick()
        fun onAllCityClick()
    }
}