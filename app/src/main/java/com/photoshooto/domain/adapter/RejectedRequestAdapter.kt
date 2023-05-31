package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.AdapterRejectedRequestBinding
import com.photoshooto.domain.model.UserElement
import com.photoshooto.util.DOMAIN
import com.photoshooto.util.convertDate

class RejectedRequestAdapter(
    var dataList: ArrayList<UserElement>,
    var isForBlockedRequest: Boolean = false
) :
    RecyclerView.Adapter<RejectedRequestAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    fun addMoreItems(items: ArrayList<UserElement>) {
        val oldSize = dataList.size
        dataList.addAll(items)
        notifyItemRangeInserted(oldSize, items.size)
    }

    inner class ViewPagerHolder(var binding: AdapterRejectedRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserElement) {
            binding.apply {
                data.profile_details?.let {
                    if (isForBlockedRequest) {
                        tvStatus.text = context?.getString(R.string.label_blocked)
                    } else tvStatus.text = context?.getString(R.string.label_declined)
                    context?.let { con ->
                        it.profile_image?.let { img ->
                            Glide.with(con)
                                .load("$DOMAIN$img")
                                .into(ivUserProfile)
                        }
                    }
                    tvName.text = it.name
                    tvDesignation.text = data.type
                    data.location?.let { location ->
                        tvLocation.text = "${location.city},${location.state}"
                    }
                    tvDate1.text = data.created_at.convertDate("dd/MM/yyyy hh:mma") ?: "-"
                    tvDate2.text = data.onboarded_at.convertDate("dd/MM/yyyy hh:mma") ?: "-"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterRejectedRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.tvDetails.setOnClickListener {
            onItemClickListener?.onDetailsClick(position, dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onDetailsClick(position: Int, data: UserElement)
    }
}
