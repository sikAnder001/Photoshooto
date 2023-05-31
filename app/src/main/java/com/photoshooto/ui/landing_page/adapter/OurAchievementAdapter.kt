package com.photoshooto.ui.landing_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.AdapterOurAchievementsBinding
import com.photoshooto.domain.model.OurAchievement

class OurAchievementAdapter constructor(
    private val context: Context, private val values: List<OurAchievement>
) : RecyclerView.Adapter<OurAchievementAdapter.ViewHolder>() {

    private val TAG = OurAchievementAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = AdapterOurAchievementsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = values[position]
        holder.bind(task, holder.itemView, position)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(private val itemBinding: AdapterOurAchievementsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(ourAchievement: OurAchievement, itemView: View, position: Int) {

            /*          Glide.with(context).load(ourAchievement.icon).diskCacheStrategy(DiskCacheStrategy.ALL)
                          .placeholder(R.drawable.ic_address_location_pin).into(itemBinding.icon)*/

            if (ourAchievement.title == "Albums") {
                itemBinding.icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.album_testimonial_icon
                    )
                )
            } else if (ourAchievement.title == "Photographers") {
                itemBinding.icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.camera_testmonial_icon
                    )
                )
            } else if (ourAchievement.title == "Photos Delivered") {
                itemBinding.icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.photos_testimonial_icon
                    )
                )
            }

            itemBinding.title.text = ourAchievement.title ?: ""
            itemBinding.hitsCount.text = ourAchievement.hits_number ?: ""
        }
    }
}