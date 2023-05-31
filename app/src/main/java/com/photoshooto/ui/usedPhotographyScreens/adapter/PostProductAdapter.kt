package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.PostProductItemBinding

class PostProductAdapter(private val onClose: (String) -> Unit) :
    RecyclerView.Adapter<PostProductAdapter.RecyclerHolder>() {

    private var items: List<String> = emptyList()

    fun swapList(items: ArrayList<String>) {
        this.items = items

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            PostProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(items.get(position))
    }

    inner class RecyclerHolder(val binding: PostProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {

            binding.tvTitle.text = data

            binding.imgClose.setOnClickListener {

                onClose(data)
            }
        }
    }

    override fun getItemCount() = items.size

}