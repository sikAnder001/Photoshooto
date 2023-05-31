package com.photoshooto.domain.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterQrViewpagerStandeeBinding
import com.photoshooto.domain.model.StandeeElement

class QrStandeePagerAdapter(var dataList: List<StandeeElement>) :
    RecyclerView.Adapter<QrStandeePagerAdapter.ViewPagerHolder>() {
    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewPagerHolder(var binding: AdapterQrViewpagerStandeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(standee: StandeeElement) {
            binding.apply {
                context?.apply {
                    Glide.with(this)
                        .load(standee.images?.get(0))
                        .into(ivStandeeThumbnail)
                }
                tvStandeeName.text = standee.type
                tvStandeeHeight.text = "Height : ${standee.height}"
                tvStandeeWidth.text = "Width : ${standee.width}"
                tvStandeeWeight.text = "Weight : ${standee.weight}"
                tvStandeePrice.text =
                    Html.fromHtml("Price : <b><font color=\"#F01B1B\">&#8377; ${standee.price} /- </font></b> Incl.taxes")
                tvStandeeDescription.text = standee.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterQrViewpagerStandeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
