package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.AdapterCommonMultiSelectBinding
import com.photoshooto.domain.model.CommonMultiSelectItem

class CommonMultiSelectAdapter(var dataList: ArrayList<CommonMultiSelectItem>) :
    RecyclerView.Adapter<CommonMultiSelectAdapter.ViewHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(var binding: AdapterCommonMultiSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonMultiSelectItem) {
            binding.apply {
                ivCheckBox.setImageResource(if (data.isSelected == true) R.drawable.ic_check_box_24 else R.drawable.ic_check_box_outline)
                tvValue.text = data.value
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            AdapterCommonMultiSelectBinding.inflate(
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
        return dataList;
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onDetailsClick()
    }
}
