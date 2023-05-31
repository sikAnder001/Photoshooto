package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.AdapterEventTshirtTypesBinding
import com.photoshooto.domain.usecase.product_details.EventTshirtTypesModel

class EventTshirtTypesAdapter(var dataList: ArrayList<EventTshirtTypesModel>) :
    RecyclerView.Adapter<EventTshirtTypesAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    class ViewPagerHolder(var binding: AdapterEventTshirtTypesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EventTshirtTypesModel) {
            binding.apply {
                ivType.setImageResource(data.image ?: R.drawable.img_temp_product_tshirt)
                tvType.text = data.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterEventTshirtTypesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.cardMain.setOnClickListener {
            onItemClickListener?.onItemClick()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onItemClick()
    }
}
