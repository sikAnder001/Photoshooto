package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterStandeeCartProductBinding
import com.photoshooto.domain.usecase.product_details.AddToCartStandeeElement
import com.photoshooto.util.amount

class StandeeCartProductAdapter(var products: ArrayList<AddToCartStandeeElement>) :
    RecyclerView.Adapter<StandeeCartProductAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemChangedListener: OnItemChangedListener? = null

    inner class ViewPagerHolder(var binding: AdapterStandeeCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AddToCartStandeeElement) {
            binding.apply {
                data.images?.let { images ->
                    context?.let {
                        Glide.with(it)
                            .load(images[0])
                            .into(ivProductImage)

                        ivProductImage.shapeAppearanceModel =
                            ivProductImage.shapeAppearanceModel.toBuilder()
                                .setAllCornerSizes(10f)
                                .build()
                    }
                }
                tvQty.text = data.quantity.toString()
                tvProductName.text = data.type
                tvDescription.text = data.description
                context?.let {
                    tvPrice.text = data.price.toDouble().amount(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterStandeeCartProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(products[holder.absoluteAdapterPosition])
        holder.binding.apply {
            products[holder.absoluteAdapterPosition].let { data ->
                cartAdd.setOnClickListener {
                    data.quantity = data.quantity + 1
                    tvQty.text = data.quantity.toString()
                    onItemChangedListener?.onQtyChanged()
                }
                cartMinus.setOnClickListener {
                    if (data.quantity > 1) {
                        data.quantity = data.quantity - 1
                        tvQty.text = data.quantity.toString()
                        onItemChangedListener?.onQtyChanged()
                    }
                }
                btnRemove.setOnClickListener {
                    onItemChangedListener?.onItemRemoveClick()
                }
                btnEdit.setOnClickListener {
                    onItemChangedListener?.onItemEditClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    interface OnItemChangedListener {
        fun onQtyChanged()
        fun onItemRemoveClick()
        fun onItemEditClick()
    }
}
