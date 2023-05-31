package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.AdapterChooseStandeeBinding
import com.photoshooto.domain.model.StandeeElement
import com.photoshooto.util.hide
import com.photoshooto.util.show

class ChooseStandeeAdapter(var sizes: List<StandeeElement>) :
    RecyclerView.Adapter<ChooseStandeeAdapter.ViewPagerHolder>() {

    companion object {
        var selectedItemPosition = 0
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null
    fun getSelectedElement(): StandeeElement? {
        if (sizes.isEmpty())
            return null
        return sizes[selectedItemPosition]
    }

    inner class ViewPagerHolder(var binding: AdapterChooseStandeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StandeeElement) {
            binding.apply {
                context?.apply {
                    Glide.with(this)
                        .load(data.images?.get(0))
                        .into(ivStandee)
                    ivStandee.shapeAppearanceModel = ivStandee.shapeAppearanceModel
                        .toBuilder()
                        .setAllCornerSizes(10f)
                        .build()
                }
                tvName.text = data.type
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterChooseStandeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(sizes[position])
        holder.binding.apply {
            if (position == selectedItemPosition) {
                groupSelected.show()
            } else groupSelected.hide()
            root.setOnClickListener {
                if (selectedItemPosition != position) {
                    val oldPosition = selectedItemPosition
                    selectedItemPosition = position
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedItemPosition)
                    onItemClickListener?.onItemClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return sizes.size
    }

    interface OnItemClickListener {
        fun onItemClick()
    }
}
