package com.photoshooto.ui.invite.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.InvitedItemBinding
import com.photoshooto.ui.job.utility.visible
import com.photoshooto.util.gone
import com.photoshooto.util.invisible

class InviteAdapter(
    var dataList: List<String>,
    var onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<InviteAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: InvitedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            if (data.isNotEmpty()) {
                binding.ivAdd.gone()
                binding.tvJoined.visible()
                binding.tvUserName.visible()
                binding.tvUserName.text = data

                Glide.with(binding.root.context).load(R.drawable.ic_person_tick)
                    .error(R.drawable.ic_wedding_photography)
                    .placeholder(R.drawable.ic_wedding_photography)
                    .into(binding.ivProfile)
            } else {
                binding.ivAdd.visible()
                binding.tvJoined.invisible()
                binding.tvUserName.invisible()

                binding.root.setOnClickListener {
                    onItemClickListener.onClickInvite()
                }

                Glide.with(binding.root.context).load(R.drawable.ic_person)
                    .error(R.drawable.ic_wedding_photography)
                    .placeholder(R.drawable.ic_wedding_photography)
                    .into(binding.ivProfile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            InvitedItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size

    interface OnItemClickListener {
        fun onClickInvite()
    }
}