package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.AdapterProductSizeBinding
import com.photoshooto.domain.model.TshirtSize

class ProductSizeAdapter(var sizes: List<TshirtSize>) :
    RecyclerView.Adapter<ProductSizeAdapter.ViewPagerHolder>() {

    companion object {
        var selectedItemPosition = -1
        var context: Context? = null
    }

    fun getSelectedElement(): TshirtSize? {
        if (selectedItemPosition == -1)
            return null
        return sizes[selectedItemPosition]
    }

    class ViewPagerHolder(var binding: AdapterProductSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TshirtSize) {
            binding.tvSize.text = data.size
            if (data.is_avilable) {
                binding.tvSize.isEnabled = true
                if (selectedItemPosition == bindingAdapterPosition) {
                    binding.tvSize.background =
                        context?.resources?.getDrawable(R.drawable.bg_blue_product_size, null)
                    binding.tvSize.setTextColor(context?.resources?.getColor(R.color.white)!!)
                } else {
                    binding.tvSize.background =
                        context?.resources?.getDrawable(R.drawable.bg_grey_product_size, null)
                    binding.tvSize.setTextColor(context?.resources?.getColor(R.color.black)!!)
                }
            } else binding.tvSize.isEnabled = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterProductSizeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(sizes[position])

        holder.binding.tvSize.setOnClickListener {
            if (selectedItemPosition != position) {
                val oldPosition = selectedItemPosition
                selectedItemPosition = position
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedItemPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return sizes.size
    }
}
