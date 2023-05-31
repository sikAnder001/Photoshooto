package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.PaymentRowBinding
import com.photoshooto.domain.model.PaymentListModel
import com.photoshooto.ui.job.utility.setSafeOnClickListener

class PaymentListAdapter(
    val onItemClick: (data: PaymentListModel) -> Unit
) : ListAdapter<PaymentListModel, PaymentListAdapter.RecyclerHolder>(object :
    ItemCallback<PaymentListModel>() {
    override fun areItemsTheSame(
        oldItem: PaymentListModel, newItem: PaymentListModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: PaymentListModel, newItem: PaymentListModel
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
        fun bind(data: PaymentListModel) {

            binding.title.text = data.name
            binding.title.setCompoundDrawablesWithIntrinsicBounds(
                data.icon,
                0,
                R.drawable.ic_arrow_right_black,
                0
            )

            //loadImageUser(binding.imageViewProfile, data.userProfile.profileImage)

            binding.option.setSafeOnClickListener {
                onItemClick.invoke(data)
            }

        }
    }
}

