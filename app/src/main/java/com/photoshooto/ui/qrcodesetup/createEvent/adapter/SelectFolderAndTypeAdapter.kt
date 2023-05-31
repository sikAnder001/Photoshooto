package com.photoshooto.ui.qrcodesetup.createEvent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ItemSelectFolderAndTypeBinding
import com.photoshooto.util.OnItemClick
import com.photoshooto.util.checkStringValue

class SelectFolderAndTypeAdapter(
    private val mContext: Context,
    private var listItem: List<String>,
    mSelectedValue: String,
    var mListener: OnItemClick<String>
) : RecyclerView.Adapter<SelectFolderAndTypeAdapter.ViewHolder>() {

    var mPositionSelect = -1

    init {
        if (checkStringValue(mSelectedValue)) {
            mPositionSelect = listItem.indexOf(mSelectedValue)
        }
    }

    inner class ViewHolder(var mBinding: ItemSelectFolderAndTypeBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bindData(position: Int, data: String) {
            mBinding.tvItemSelectTitle.text = data
            if (mPositionSelect == position) {
                mBinding.constraintMain.setBackgroundColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.text_054871
                    )
                )
                mBinding.imageItemSelect.visibility = View.VISIBLE
                mBinding.tvItemSelectTitle.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.white
                    )
                )
            } else {
                mBinding.constraintMain.setBackgroundColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.white
                    )
                )
                mBinding.imageItemSelect.visibility = View.INVISIBLE
                mBinding.tvItemSelectTitle.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.text_717171
                    )
                )
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
            ItemSelectFolderAndTypeBinding.inflate(
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
