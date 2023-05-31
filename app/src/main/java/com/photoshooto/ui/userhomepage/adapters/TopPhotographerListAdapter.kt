package com.photoshooto.ui.userhomepage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.AdapterTopPhotographerListItemsBinding
import com.photoshooto.domain.model.UserElement

class TopPhotographerListAdapter(
    val list: List<UserElement>?,
    var onUserItemClickListener: OnUserItemClickListener
) :
    RecyclerView.Adapter<TopPhotographerListAdapter.Holder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val binding =
            AdapterTopPhotographerListItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list!![position], context)

        holder.binding.tvInquiry.setOnClickListener {
            onUserItemClickListener.onEnquireClicked(list[position])
        }
        holder.binding.lvMain.setOnClickListener {
            onUserItemClickListener.onViewProfileClicked(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }


    class Holder(
        val binding: AdapterTopPhotographerListItemsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserElement, context: Context) {

            with(binding) {
                tvUserName.text = data.profile_details?.name
                /*tvPhotographerType.text = data.profile_details?.profession?.get(0)*/
                tvStudioName.text = data.profile_details?.studio_name
                val profileImage = data.profile_details?.profile_image
                if (profileImage.isNullOrEmpty()) {
                    Glide.with(context).load(profileImage)
                        .placeholder(R.drawable.ic_girl_profile)
                        .into(binding.profileImage)
                }
            }
        }

    }

    interface OnUserItemClickListener {
        fun onEnquireClicked(user: UserElement)
        fun onViewProfileClicked(user: UserElement)
    }

}