package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.ListPhotographerHomeRowBinding
import com.photoshooto.domain.model.AvailablePhotographers
import com.photoshooto.ui.job.utility.invisible
import com.photoshooto.ui.job.utility.loadImageUser
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import com.photoshooto.ui.job.utility.visible
import com.photoshooto.util.SharedPrefsHelper

class PhotographerHomeListAdapter(
    val onDetailClick: (data: AvailablePhotographers) -> Unit
) : ListAdapter<AvailablePhotographers, PhotographerHomeListAdapter.RecyclerHolder>(object :
    ItemCallback<AvailablePhotographers>() {
    override fun areItemsTheSame(
        oldItem: AvailablePhotographers,
        newItem: AvailablePhotographers
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: AvailablePhotographers,
        newItem: AvailablePhotographers
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ListPhotographerHomeRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecyclerHolder(val binding: ListPhotographerHomeRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AvailablePhotographers) {

            binding.userName.text = data.userProfile.name
            binding.event.text = data.eventType
            binding.userId.text = data.userId
            binding.verify.isVisible = data.isVerified
            binding.city.text = data.city.toString().replace("[", "").replace("]", "")

            if (data.userProfile.online_status.equals("online", true)) {
                binding.dot.visible()
            } else {
                binding.dot.invisible()
            }

            data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }

            binding.camera.text = data.userId

            val equipment = (data.photoCameraUse + data.videoCameraUse + data.otherEquipments)
            if (equipment.isNotEmpty()) {
                if (equipment.size > 1)
                    binding.camera.text = "${equipment.first()} & more"
                else
                    binding.camera.text = equipment.first()
            } else {
                binding.camera.text = ""
            }

            binding.btnDetails.setSafeOnClickListener {
                onDetailClick.invoke(data)
            }

            binding.check.isVisible = SharedPrefsHelper.getSubscribed()

            /* binding.name.text = data.baseName.trim()

             binding.size.text = if (data.sizeDir > 0) {
                 "${data.sizeDir} Items"
             } else {
                 "${data.sizeFiles} Items"
             }*/

        }
    }
}

