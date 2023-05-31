package com.photoshooto.domain.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.AdapterSelectAddressBinding
import com.photoshooto.domain.model.AddressElement

class SelectAddressAdapter(var dataList: ArrayList<AddressElement>) :
    RecyclerView.Adapter<SelectAddressAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewPagerHolder(var binding: AdapterSelectAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AddressElement) {
            binding.apply {
                tvAddressType.text = "(${data.type})"
                tvAddress.text = "${data.user_profile?.name}\n" +
                        "${data.user_profile?.mobile}\n" +
                        "${data.flat_no}, ${data.address}, ${data.city}, ${data.state} ${data.pincode}\n" +
                        "${data.landmark}"
                if (data.is_default) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cardMain.strokeColor = context?.getColor(R.color.orange_clr) ?: 0
                    } else {
                        cardMain.strokeColor = context?.resources?.getColor(R.color.orange_clr) ?: 0
                    }
                    cardMain.elevation = 5f
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cardMain.strokeColor = context?.getColor(R.color.grey32) ?: 0
                    } else {
                        cardMain.strokeColor = context?.resources?.getColor(R.color.grey32) ?: 0
                    }
                    cardMain.elevation = 0f
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterSelectAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.apply {
            btnEdit.setOnClickListener {
                onItemClickListener?.onEditClick(position, dataList[position])
            }
            cardMain.setOnClickListener {
                onItemClickListener?.onItemClick(position, dataList[position])
            }

            imgDelete.setOnClickListener {
                onItemClickListener?.onDeleteClick(position, dataList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, data: AddressElement)
        fun onEditClick(position: Int, data: AddressElement)
        fun onDeleteClick(position: Int, data: AddressElement)
    }
}
