package com.photoshooto.ui.qrcodesetup.eventOrderHistory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.ItemEventOrderHistoryBinding
import com.photoshooto.util.OnItemClickView

class EventOrderHistoryAdapter(
    private val mContext: Context,
    private var listItem: List<String>,
    private var mListener: OnItemClickView<String>
) : RecyclerView.Adapter<EventOrderHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(var mBinding: ItemEventOrderHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bindData(position: Int, data: String) {
            mBinding.imageItemQrOption.setOnClickListener {
                mListener.onItemClick(data, position, it)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEventOrderHistoryBinding.inflate(
                LayoutInflater.from(mContext), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position, listItem[position])
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}
