package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AdapterCommonSingleSelectBinding
import com.photoshooto.domain.model.CommonMultiSelectItem

class CommonSingleSelectAdapter(var dataList: ArrayList<CommonMultiSelectItem>) :
    RecyclerView.Adapter<CommonSingleSelectAdapter.ViewHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(var binding: AdapterCommonSingleSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonMultiSelectItem) {
            binding.apply {
                rbCheckBox.text = data.value
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            AdapterCommonSingleSelectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.apply {
            rbCheckBox.setOnClickListener {
                holder.bind(dataList[position])
                onItemClickListener?.onDetailsClick(dataList[position].value)
            }
        }
    }

    fun clearSelection() {
        dataList.forEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onDetailsClick(data: String)
    }
}
