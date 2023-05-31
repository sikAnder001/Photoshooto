package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.ProductsCategoryListItemBinding
import com.photoshooto.domain.model.CategoryModel

class ProductsCategoryListAdapter(private val onSelectCategory: (CategoryModel) -> Unit) :
    RecyclerView.Adapter<ProductsCategoryListAdapter.RecyclerHolder>() {

    private var items: List<CategoryModel> = emptyList()

    fun swapList(items: List<CategoryModel>) {
        this.items = items

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ProductsCategoryListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(items.get(position))
    }

    inner class RecyclerHolder(val binding: ProductsCategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryModel: CategoryModel) {

            binding.tvCategoryName.text = categoryModel.name


            if (categoryModel.id.equals("0")) {
                binding.imgCategory.setImageResource(R.drawable.ic_view_all)
            } else {
                Glide.with(itemView.context)
                    .load(categoryModel.icon)
                    .into(binding.imgCategory)
            }

            itemView.setOnClickListener {
                onSelectCategory(categoryModel)
            }
        }
    }

    override fun getItemCount() = items.size

}