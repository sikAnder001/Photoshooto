package com.photoshooto.ui.qrcodesetup.createEvent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ItemGeneratedQrCodeBinding
import com.photoshooto.domain.model.StandeeElement
import com.photoshooto.util.OnItemClick
import com.photoshooto.util.getDrawableValue

class GeneratedQrCodesAdapter(
    private val mContext: Context,
    private var listItem: List<StandeeElement>,
    mSelectedPos: Int = -1,
    private var mListener: OnItemClick<StandeeElement>
) : RecyclerView.Adapter<GeneratedQrCodesAdapter.ViewHolder>() {

    var mPositionSelect = -1

    init {
        if (mSelectedPos != (-1)) {
            mPositionSelect = mSelectedPos
        }
    }

    inner class ViewHolder(var mBinding: ItemGeneratedQrCodeBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bindData(position: Int, data: StandeeElement) {

            mBinding.tvItemGeneratedQrTitle.text = data.type

            if (mPositionSelect == position) {
                mBinding.imageItemQrCode.background =
                    mContext.getDrawableValue(R.drawable.border_selected)
                mBinding.imageItemSelected.visibility = View.VISIBLE
            } else {
                mBinding.imageItemQrCode.background =
                    mContext.getDrawableValue(R.drawable.border_not_selected)
                mBinding.imageItemSelected.visibility = View.INVISIBLE
            }

            mBinding.constraintMain.setOnClickListener {
                mPositionSelect = position
                mListener.onItemClick(data, position)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGeneratedQrCodeBinding.inflate(
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
