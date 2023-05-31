package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.AdapterFilterSortItemBinding
import com.photoshooto.domain.model.CommonMultiSelectItem

class FilterAndSortSelectAdapter(
    private var mContext: Context,
    var dataList: ArrayList<CommonMultiSelectItem>
) : RecyclerView.Adapter<FilterAndSortSelectAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(var binding: AdapterFilterSortItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonMultiSelectItem) {
            binding.apply {
                if (data.isSelected!!) {
                    binding.constraintMainFilterSort.setBackgroundColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.lightorange
                        )
                    )
                } else {
                    binding.constraintMainFilterSort.setBackgroundColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.transparent
                        )
                    )
                }
                tvValue.text = data.value
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterFilterSortItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.apply {
            root.setOnClickListener {
                dataList[position].isSelected = !(dataList[position].isSelected ?: false)
                holder.bind(dataList[position])
                onItemClickListener?.onDetailsClick(dataList[holder.absoluteAdapterPosition])
            }
        }
    }

    fun clearSelection() {
        dataList.forEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }

    fun getDataFilter(): ArrayList<CommonMultiSelectItem> {
        return dataList
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onDetailsClick(data: CommonMultiSelectItem)
    }
}
