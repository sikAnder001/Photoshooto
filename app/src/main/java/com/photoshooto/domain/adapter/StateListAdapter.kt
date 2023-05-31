package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AdapterStateListBinding
import com.photoshooto.domain.model.StatesData

class StateListAdapter(var dataList: ArrayList<StatesData>) :
    RecyclerView.Adapter<StateListAdapter.ViewHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(var binding: AdapterStateListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StatesData) {
            binding.apply {
                tvValue.text = data.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            AdapterStateListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(dataList[position])
        }
    }

    interface OnItemClickListener {
        fun onItemClick(value: StatesData)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
