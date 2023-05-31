package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.ImageThumpListItemBinding

class ImageThumpListAdapter(private val onClick: (Int) -> Unit) :
    RecyclerView.Adapter<ImageThumpListAdapter.RecyclerHolder>() {

    private var items: List<String> = emptyList()

    var selectedPosition = 0
    var isFirstTime = true

    fun swapList(items: List<String>) {
        this.items = items

        notifyDataSetChanged()
    }

    fun swapItem(items: Int) {
        this.selectedPosition = items
        this.isFirstTime = items == 0
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ImageThumpListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(items.get(position), position)
    }

    inner class RecyclerHolder(val binding: ImageThumpListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String, position: Int) {

            Glide.with(itemView.context)
                .load(data)
                .into(binding.imgThump)

            itemView.setOnClickListener {
                selectedPosition = position
                isFirstTime = false

                onClick(position)
                notifyDataSetChanged()
            }

            if (isFirstTime && position == 0) {
                binding.rlClick.setBackgroundResource(R.drawable.ic_image_border)
            } else if (selectedPosition == position) {
                binding.rlClick.setBackgroundResource(R.drawable.ic_image_border)
            } else {
                binding.rlClick.setBackgroundResource(R.drawable.ic_image_border_white)
            }
        }
    }

    override fun getItemCount() = items.size
}