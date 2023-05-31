package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.AdapterAllEmployeeBinding
import com.photoshooto.domain.model.UserElement

class AllEmployeeAdapter(var dataList: ArrayList<UserElement>) :
    RecyclerView.Adapter<AllEmployeeAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewPagerHolder(var binding: AdapterAllEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserElement) {
            binding.apply {
                data.profile_details?.let {
                    context?.let { con ->
                        Glide.with(con)
                            .load(it.profile_image ?: R.drawable.ic_temp_user_profile)
                            .into(ivUserProfile)
                    }
                    tvName.text = it.name
                    tvUsername.text = data.username ?: "-"
                    tvEmpCode.text = data.employee_code ?: "-"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterAllEmployeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.tvDetails.setOnClickListener {
            onItemClickListener?.onDetailsClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onDetailsClick(position: Int)
    }
}
