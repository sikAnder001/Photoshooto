package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.PaymentRowBinding
import com.photoshooto.domain.model.CouponModel

class CouponListAdapter(
    val onItemClick: (data: CouponModel) -> Unit
) : ListAdapter<CouponModel, CouponListAdapter.RecyclerHolder>(object :
    ItemCallback<CouponModel>() {
    override fun areItemsTheSame(
        oldItem: CouponModel, newItem: CouponModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CouponModel, newItem: CouponModel
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            PaymentRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class RecyclerHolder(val binding: PaymentRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CouponModel) {

        }
    }
}

