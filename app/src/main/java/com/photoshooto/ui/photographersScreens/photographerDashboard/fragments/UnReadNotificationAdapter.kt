package com.photoshooto.ui.photographersScreens.photographerDashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.UnreadNotificationItemsBinding

class UnReadNotificationAdapter(val context: Context) :
    RecyclerView.Adapter<UnReadNotificationAdapter.holder>() {

    var isSelected: Boolean = false
    var isClickForRate: Boolean = false
    var isRate: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val binding =
            UnreadNotificationItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UnReadNotificationAdapter.holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        holder.binding.tvTitle.setOnClickListener(View.OnClickListener {
            isSelected = !isSelected
            if (isSelected) {
                holder.binding.lvNotificationDetails.visibility = View.VISIBLE
            } else {
                holder.binding.lvNotificationDetails.visibility = View.GONE

            }
        })

        holder.binding.tvRate.setOnClickListener(View.OnClickListener {
            isClickForRate = !isClickForRate
            if (isClickForRate) {
                holder.binding.lvRatingView.visibility = View.VISIBLE
            } else {
                holder.binding.lvRatingView.visibility = View.GONE

            }
        })

        holder.binding.tvRateTitle.setOnClickListener(View.OnClickListener {
            isRate = !isRate
            if (isRate) {
                holder.binding.lvNotificationRatingDetails.visibility = View.VISIBLE
            } else {
                holder.binding.lvNotificationRatingDetails.visibility = View.GONE
            }
        })
    }

    override fun getItemCount(): Int {
        return 1
    }

    class holder(
        val binding: UnreadNotificationItemsBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }
}