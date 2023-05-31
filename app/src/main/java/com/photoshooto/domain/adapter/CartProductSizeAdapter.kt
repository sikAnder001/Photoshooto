package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AdapterCartProductSizeBinding
import com.photoshooto.domain.usecase.product_details.TshirtSize
import com.photoshooto.util.hide
import com.photoshooto.util.show

class CartProductSizeAdapter(
    var sizes: ArrayList<TshirtSize>,
    var availableSize: ArrayList<TshirtSize>
) :
    RecyclerView.Adapter<CartProductSizeAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemChangedListener: OnItemChangedListener? = null

    inner class ViewPagerHolder(var binding: AdapterCartProductSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TshirtSize) {
            binding.apply {
                tvQty.text = data.quantity.toString()
                val sizeAdapter = ProductSizeDropDownAdapter(
                    context!!,
                    availableSize.map { a -> a.size } as ArrayList)
                spinnerSize.adapter = sizeAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterCartProductSizeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val data = sizes[holder.absoluteAdapterPosition]
        holder.bind(data)
        holder.binding.apply {
            if (position == 0) {
                tvQtyLabel.show()
                tvSizeLabel.show()
            } else {
                tvQtyLabel.hide()
                tvSizeLabel.hide()
            }
            cartAdd.setOnClickListener {
                data.quantity = (data.quantity ?: 0).plus(1)
                notifyItemChanged(holder.absoluteAdapterPosition)
                onItemChangedListener?.onAddClick(holder.absoluteAdapterPosition)
            }
            cartMinus.setOnClickListener {
                if (data.quantity == 1) {
                    if (sizes.size > 1) {
                        sizes.removeAt(holder.absoluteAdapterPosition)
                        notifyItemRemoved(holder.absoluteAdapterPosition)
                    }
                } else {
                    data.quantity = (data.quantity ?: 0).minus(1)
                    notifyItemChanged(holder.absoluteAdapterPosition)
                }
                onItemChangedListener?.onRemoveClick(holder.absoluteAdapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return sizes.size
    }

    interface OnItemChangedListener {
        fun onAddClick(position: Int)
        fun onRemoveClick(position: Int)
    }
}
