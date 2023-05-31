package com.photoshooto.ui.coupon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photoshooto.databinding.AdapterOurAchievementsBinding
import com.photoshooto.domain.model.OurAchievement

class CouponAdapter constructor(
    private val context: Context, private val values: List<OurAchievement>
) : RecyclerView.Adapter<CouponAdapter.ViewHolder>() {

    private val TAG = CouponAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = AdapterOurAchievementsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = values[position]
        holder.bind(task, holder.itemView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(private val itemBinding: AdapterOurAchievementsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(ourAchievement: OurAchievement, itemView: View) {

            Glide.with(context).load(ourAchievement.icon).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemBinding.icon)

            itemBinding.title.text = ourAchievement.title ?: ""
        }
    }
}