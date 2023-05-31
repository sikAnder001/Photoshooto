package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AdapterCouponsBinding
import com.photoshooto.domain.model.CouponData
import com.photoshooto.util.hide
import com.photoshooto.util.show

class CouponsAdapter(var dataList: ArrayList<CouponData>) :
    RecyclerView.Adapter<CouponsAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    class ViewPagerHolder(var binding: AdapterCouponsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CouponData) {
            binding.apply {
                tvName.text = data.title
                tvDescription.text = data.description
                tvCode.text = data.couponCode
                if (data.icon == null) {
                    ivThumbnail.hide()
                } else {
                    ivThumbnail.setImageResource(data.icon)
                    ivThumbnail.show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterCouponsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.btnApply.setOnClickListener {
            onItemClickListener?.onApplyClick(dataList[position].couponCode ?: "")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onApplyClick(code: String)
    }
}
