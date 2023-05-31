package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ItemviewBottomSheetBinding
import com.photoshooto.util.setSingleClickListener

class BottomSheetRvAdapter(
    var mList: List<String>,
    var mItemClickListener: OnItemClickListener,
    var context: Context
) : RecyclerView.Adapter<BottomSheetRvAdapter.PlanViewHolder>() {

    interface OnItemClickListener {
        fun onItemSelected(list: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        return PlanViewHolder(
            ItemviewBottomSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    var lastSelectedPossion: Int = -1

    inner class PlanViewHolder(var itemBinding: ItemviewBottomSheetBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(service: String) {

            itemBinding.text.text = service

            itemBinding.text.setSingleClickListener {
                lastSelectedPossion = adapterPosition
                mItemClickListener.onItemSelected(service)
            }
            if (position == lastSelectedPossion) {
                itemBinding.layout.setSelected(true)
                itemBinding.layout.setBackgroundColor(context.resources.getColor(R.color.lightorange))
            } else {
                itemBinding.layout.setSelected(false)
                itemBinding.layout.setBackgroundColor(context.resources.getColor(R.color.lightgray))
            }


        }
    }

}
