package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterNewRequestBinding
import com.photoshooto.domain.model.UserElement
import com.photoshooto.util.*

class NewRequestAdapter(var dataList: ArrayList<UserElement>) :
    RecyclerView.Adapter<NewRequestAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    fun addMoreItems(items: ArrayList<UserElement>) {
        val oldSize = dataList.size
        dataList.addAll(items)
        notifyItemRangeInserted(oldSize, items.size)
    }

    inner class ViewPagerHolder(var binding: AdapterNewRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserElement) {
            binding.apply {
                data.profile_details?.let {
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

                    if (data.status == ACCEPT) {
                        binding.cardAccept.visibility = View.GONE
                        binding.cardDecline.visibility = View.GONE
                        binding.cardDeclined.visibility = View.GONE
                        binding.cardAccepted.visibility = View.VISIBLE
                    } else if (data.status == DECLINE || data.status == REJECT || data.status == BLOCK) {
                        binding.cardAccept.visibility = View.GONE
                        binding.cardDecline.visibility = View.GONE
                        binding.cardDeclined.visibility = View.VISIBLE
                        binding.cardAccepted.visibility = View.GONE
                    } else {
                        binding.cardAccept.visibility = View.VISIBLE
                        binding.cardDecline.visibility = View.VISIBLE
                        binding.cardDeclined.visibility = View.GONE
                        binding.cardAccepted.visibility = View.GONE
                    }


                    tvDate1.text = data.created_at.convertDate("dd/MM/yyyy hh:mma") ?: "-"
                    tvDate2.text = data.onboarded_at.convertDate("dd/MM/yyyy hh:mma") ?: "-"
                    tvRequestDueTime.text = data.created_at.calculateDueTime() ?: "-"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterNewRequestBinding.inflate(
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
        holder.binding.cardAccept.setOnClickListener {
            onItemClickListener?.onAcceptOrRejectClick(ACCEPT, dataList[position])
        }
        holder.binding.cardDecline.setOnClickListener {
            onItemClickListener?.onAcceptOrRejectClick(REJECT, dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onDetailsClick(position: Int, data: UserElement)

        fun onAcceptOrRejectClick(acceptOrReject: String, data: UserElement)
    }
}
