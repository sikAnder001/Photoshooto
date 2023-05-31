package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.AdapterCartProductBinding
import com.photoshooto.domain.usecase.product_details.TshirtElement
import com.photoshooto.domain.usecase.product_details.TshirtSize

class CartProductAdapter(
    var products: ArrayList<TshirtElement>,
    var availableSize: ArrayList<TshirtSize>
) :
    RecyclerView.Adapter<CartProductAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemChangedListener: OnItemChangedListener? = null

    inner class ViewPagerHolder(var binding: AdapterCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TshirtElement) {
            binding.apply {
                data.images?.let { images ->
                    context?.let {
                        Glide.with(it)
                            .load(images[0])
                            .into(ivProductImage)
                    }
                }
                val totalUnits = (data.sizes ?: arrayListOf()).sumOf { it.quantity }
                tvTotalUnits.text = "${context?.getString(R.string.label_total_units)}: $totalUnits"
                tvProductName.text = context?.getString(R.string.label_tshirt)
                tvSortDescription.text = data.description
                tvOrderId.text = "${context?.getString(R.string.label_order_id)}: ${data.id}"
                recyclerViewSize.layoutManager = LinearLayoutManager(context)
                recyclerViewSize.adapter = CartProductSizeAdapter(
                    (data.sizes ?: arrayListOf()) as ArrayList,
                    availableSize
                )
            }
        }

        fun updateTotalUnits(data: TshirtElement) {
            val totalUnits = (data.sizes ?: arrayListOf()).sumOf { it.quantity }
            binding.tvTotalUnits.text =
                "${context?.getString(R.string.label_total_units)}: $totalUnits"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterCartProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(products[holder.absoluteAdapterPosition])
        holder.binding.apply {
            (recyclerViewSize.adapter as CartProductSizeAdapter).onItemChangedListener =
                object : CartProductSizeAdapter.OnItemChangedListener {
                    override fun onAddClick(position: Int) {
                        holder.updateTotalUnits(products[holder.absoluteAdapterPosition])
                        onItemChangedListener?.onQtyChanged()
                    }

                    override fun onRemoveClick(position: Int) {
                        holder.updateTotalUnits(products[holder.absoluteAdapterPosition])
                        if ((products[holder.absoluteAdapterPosition].sizes
                                ?: arrayListOf()).isEmpty()
                        ) {
                            products.removeAt(holder.absoluteAdapterPosition)
                            notifyItemRemoved(holder.absoluteAdapterPosition)
                        }
                        onItemChangedListener?.onQtyChanged()
                    }
                }
            ivAddNewSize.setOnClickListener {
                products[holder.absoluteAdapterPosition].apply {
                    ((sizes ?: arrayListOf()) as ArrayList).add(
                        TshirtSize(
                            true,
                            1,
                            availableSize[0].size
                        )
                    )
                    (recyclerViewSize.adapter as CartProductSizeAdapter).notifyItemInserted(
                        (sizes ?: arrayListOf()).size - 1
                    )
                    holder.updateTotalUnits(this)
                    onItemChangedListener?.onQtyChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    interface OnItemChangedListener {
        fun onQtyChanged()
    }
}
