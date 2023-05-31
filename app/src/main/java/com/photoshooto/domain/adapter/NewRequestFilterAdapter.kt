package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AdapterNewRequestFilterBinding
import com.photoshooto.domain.model.CommonMultiSelectItem

class NewRequestFilterAdapter(var dataList: ArrayList<CommonMultiSelectItem>) :
    RecyclerView.Adapter<NewRequestFilterAdapter.ViewHolder>() {

    companion object {
        var context: Context? = null
    }

    inner class ViewHolder(var binding: AdapterNewRequestFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonMultiSelectItem) {
            binding.apply {
                checkbox.text = data.value
                checkbox.isChecked = data.isSelected ?: false
                checkbox.setOnCheckedChangeListener { view, status ->
                    data.isSelected = status
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            AdapterNewRequestFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun clearSelection() {
        dataList.forEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }
}
