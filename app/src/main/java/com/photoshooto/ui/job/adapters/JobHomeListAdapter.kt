package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.ListJobHomeRowBinding
import com.photoshooto.domain.model.JobModel
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefsHelper

class JobHomeListAdapter(
    val onDetailClick: (data: JobModel) -> Unit
) : ListAdapter<JobModel, JobHomeListAdapter.JobHolder>(object :
    ItemCallback<JobModel>() {
    override fun areItemsTheSame(oldItem: JobModel, newItem: JobModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: JobModel, newItem: JobModel): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobHolder {
        return JobHolder(
            ListJobHomeRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: JobHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class JobHolder(val binding: ListJobHomeRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JobModel) {

            binding.studioName.text = data.userProfile.studio_name
            binding.studioId.text = data.userId
            binding.eventName.text = data.title.toString().ifBlank { data.eventType }

            binding.city.text = data.addressObj.parseAddress()
            binding.dateEnd.text = data.endTime
            binding.verify.isVisible = data.isVerified

            binding.dateStart.text = data.startDate.getFormattedDatetime(
                outputFormat = "dd-MM-YY,",
                inputFormat = "MM/dd/yyyy",
                withZoneCalculation = false
            ) + " ${data.startTime}"

            binding.dateEnd.text = data.endDate.getFormattedDatetime(
                outputFormat = "dd-MM-YY,",
                inputFormat = "MM/dd/yyyy",
                withZoneCalculation = false
            ) + " ${data.endTime}"

            data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }

            if (data.userProfile.online_status.equals("online", true)) {
                binding.dot.visible()
            } else {
                binding.dot.invisible()
            }

            binding.btnDetails.setSafeOnClickListener {
                onDetailClick.invoke(data)
            }

            binding.check.isVisible = SharedPrefsHelper.getSubscribed()

            if (data.status == "approved") {
                binding.check.visible()
            } else {
                binding.check.gone()
            }
        }
    }
}

