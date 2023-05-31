package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AllCategoryItemBinding
import com.photoshooto.domain.model.CategoryModel

class AllCategoryListAdapter(private val onSelectCategory: (CategoryModel) -> Unit) :
    RecyclerView.Adapter<AllCategoryListAdapter.RecyclerHolder>() {
    private var items: List<CategoryModel> = emptyList()

    fun swapList(items: ArrayList<CategoryModel>) {
        this.items = items

        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            AllCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(items.get(position))
    }

    inner class RecyclerHolder(val binding: AllCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryModel) {

            binding.tvTitle.text = data.name

            Glide.with(itemView.context)
                .load(data.icon)
                .into(binding.imgCategory)

            itemView.setOnClickListener {
                onSelectCategory(data)
            }
        }
    }

    override fun getItemCount() = items.size
}