package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.ProductsImageListItemBinding

class ProductsImageListAdapter(private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<ProductsImageListAdapter.RecyclerHolder>() {

    private var items: List<String> = emptyList()

    fun swapList(items: List<String>) {
        this.items = items

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ProductsImageListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(items.get(position))
    }

    inner class RecyclerHolder(val binding: ProductsImageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {

            Glide.with(itemView.context)
                .load(data)
                .into(binding.imgProducts)

            itemView.setOnClickListener {
                onClick(data)
            }
        }
    }

    override fun getItemCount() = items.size

}