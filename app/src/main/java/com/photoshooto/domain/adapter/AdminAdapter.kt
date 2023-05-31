package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.shape.CornerFamily
import com.photoshooto.R
import com.photoshooto.databinding.AdapterAdminBinding
import com.photoshooto.domain.model.UserElement
import com.photoshooto.util.DOMAIN

class AdminAdapter(var dataList: ArrayList<UserElement>, var isForInactiveAdmins: Boolean = false) :
    RecyclerView.Adapter<AdminAdapter.ViewHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(var binding: AdapterAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserElement) {
            binding.apply {
                data.profile_details?.profile_image?.let { image ->
                    if (image.isNotEmpty()) {
                        context?.let {
                            Glide.with(it)
                                .load("$DOMAIN$image").into(ivThumbnail)
                        }
                    }
                }
                ivThumbnail.shapeAppearanceModel = ivThumbnail.shapeAppearanceModel.toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, 20f)
                    .build()
                tvName.text = data.profile_details?.name
                tvCode.text = "EMP: ${data.id}"
                if (isForInactiveAdmins) {
                    btnDetails.background = context?.getDrawable(R.drawable.btn_manage_admin_grey)
                } else btnDetails.background =
                    context?.getDrawable(R.drawable.btn_manage_admin_blue)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            AdapterAdminBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.apply {
            btnDetails.setOnClickListener {
                onItemClickListener?.onDetailsClick(position, dataList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        /* return if (dataList.size > 3) {
             3
         } else {
             dataList.size
         }*/
        return dataList.size
    }

    interface OnItemClickListener {
        fun onDetailsClick(position: Int, data: UserElement)
    }
}
