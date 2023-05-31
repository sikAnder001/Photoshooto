package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.ListOrderSummaryBinding
import com.photoshooto.domain.model.OrderSummaryModel
import com.photoshooto.ui.job.utility.setSafeOnClickListener

class OrderSummaryAdapter(
    val onEditClick: (data: OrderSummaryModel) -> Unit
) : ListAdapter<OrderSummaryModel, OrderSummaryAdapter.RecyclerHolder>(object :
    ItemCallback<OrderSummaryModel>() {
    override fun areItemsTheSame(oldItem: OrderSummaryModel, newItem: OrderSummaryModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: OrderSummaryModel,
        newItem: OrderSummaryModel
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ListOrderSummaryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(getItem(position))
    }

    inner class RecyclerHolder(val binding: ListOrderSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrderSummaryModel) {

            binding.orderImg.setImageResource(data.orderResourceImg)
            binding.orderRowName.text = data.orderName
            binding.orderType.text = "Type: ${data.orderType}"
            binding.orderRowPrice.text = "Price: ${data.orderPrice}"
            binding.orderRowTotal.text = "₹${data.orderRowTotal}"

            binding.orderEdit.setSafeOnClickListener {
                onEditClick.invoke(data)
            }
        }
    }
}

