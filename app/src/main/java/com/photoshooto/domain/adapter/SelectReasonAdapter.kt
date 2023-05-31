package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.AdapterSelectReasonBinding
import com.photoshooto.domain.model.ReasonElement

class SelectReasonAdapter(var dataList: ArrayList<ReasonElement>) :
    RecyclerView.Adapter<SelectReasonAdapter.ViewHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(var binding: AdapterSelectReasonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReasonElement) {
            binding.apply {
                ivCheckBox.setImageResource(if (data.is_selected) R.drawable.ic_check_box_24 else R.drawable.ic_check_box_outline)
                context?.resources?.let {
                    viewMain.setBackgroundColor(
                        if (data.is_selected) it.getColor(R.color.white) else it.getColor(R.color.lightgray)
                    )
                }
                tvValue.text = data.reason_name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            AdapterSelectReasonBinding.inflate(
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
                dataList[position].is_selected = !(dataList[position].is_selected ?: false)
                holder.bind(dataList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onDetailsClick()
    }
}
