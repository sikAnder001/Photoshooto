package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.LocationListItemBinding
import com.photoshooto.domain.model.CategoryModel
import java.util.*


class ProductFilterAdapter(private val onTab: (CategoryModel, Int) -> Unit) :
    RecyclerView.Adapter<ProductFilterAdapter.RecyclerHolder>() {

    private var items: List<CategoryModel> = emptyList()
    private var itemsFiltered: List<CategoryModel> = ArrayList<CategoryModel>()

    var selectedPosition = 0
    var isFirstTime = true

    fun swapList(items: ArrayList<CategoryModel>) {
        this.items = items
        this.itemsFiltered = items

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            LocationListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(itemsFiltered.get(position), position)
    }

    inner class RecyclerHolder(val binding: LocationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(data: CategoryModel, position: Int) {

            binding.tvLocation.text = data.name

            itemView.setOnClickListener {

                data.select = !data.select

                onTab(data, position)

                notifyDataSetChanged()

            }

            val typefaceRegular = itemView.context.resources.getFont(R.font.poppins_regular)
            val typefaceMedium = itemView.context.resources.getFont(R.font.poppins_medium)

            if (data.select) {
                binding.imgLocation.setImageResource(R.drawable.ic_radio_check_product)
                binding.tvLocation.setTypeface(typefaceMedium)
            } else {
                binding.imgLocation.setImageResource(R.drawable.ic_radio_uncheck_product)
                binding.tvLocation.setTypeface(typefaceRegular)
            }
        }
    }

    override fun getItemCount() = itemsFiltered.size

    val filter: Filter
        get() = object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                itemsFiltered = if (charString.isEmpty()) {
                    items
                } else {
                    val filteredList: ArrayList<CategoryModel> =
                        ArrayList<CategoryModel>()
                    for (row in items) {
                        if (row.name.lowercase(Locale.ROOT)
                                ?.contains(charString.lowercase(Locale.ROOT)) == true
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = itemsFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                itemsFiltered = filterResults.values as ArrayList<CategoryModel>
                notifyDataSetChanged()
            }
        }

}