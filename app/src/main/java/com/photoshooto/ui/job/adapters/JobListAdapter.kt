package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ListJobRowBinding
import com.photoshooto.domain.model.JobModel
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper

class JobListAdapter(
    val onShareClick: (data: JobModel) -> Unit,
    val onBookmarkClick: (data: JobModel, type: String, position: Int) -> Unit,
    val onButtonClick: (data: JobModel) -> Unit,
    val onReportClick: (data: JobModel) -> Unit
) : ListAdapter<JobModel, JobListAdapter.RecyclerHolder>(object : ItemCallback<JobModel>() {
    override fun areItemsTheSame(
        oldItem: JobModel, newItem: JobModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: JobModel, newItem: JobModel
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ListJobRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun removeFavourite(jobModel: JobModel, pos: Int) {
        val currentList = currentList.toList()
        currentList[pos].isFavorite = false
        submitList(currentList)
        notifyItemChanged(pos)
        onBookmarkClick.invoke(jobModel, "remove", pos)
    }

    private fun addFavourite(jobModel: JobModel, pos: Int) {
        val currentList = currentList.toList()
        currentList[pos].isFavorite = true
        submitList(currentList)
        notifyItemChanged(pos)
        onBookmarkClick.invoke(jobModel, "add", pos)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecyclerHolder(val binding: ListJobRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JobModel) {

            if (data.status == "approved") {
                binding.verified.visible()
                binding.verifiedIcon.visible()
            } else {
                binding.verified.gone()
                binding.verifiedIcon.gone()
            }

            if (data.isFavorite) {
                binding.bookmark.setImageResource(R.drawable.ic_bookmark_filled)
            } else {
                binding.bookmark.setImageResource(R.drawable.ic_bookmark)
            }

            if (data.userProfile.online_status.equals("online", true)) {
                binding.dot.visible()
            } else {
                binding.dot.invisible()
            }

            binding.report.isVisible =
                data.userId != SharedPrefsHelper.read(SharedPrefConstant.USER_ID)

            binding.adId.text = "AD ID: ${data.id}"
            binding.eventName.text = (data.title ?: "").ifBlank { data.eventType }
            binding.studioName.text = data.userProfile.studio_name
            binding.time.text = data.createdAt.getDateTimeDiffSingle()
            binding.city.text = data.addressObj.parseAddress()
            binding.stuId.text = "ID: ${data.userId}"
            binding.stDate.text =
                "${data.startDate.getFormattedDatetime(outputFormat = "dd/MM/yyyy")}, ${data.startTime}"

            binding.endDate.text =
                "${data.endDate.getFormattedDatetime(outputFormat = "dd/MM/yyyy")}, ${data.endTime}"


            data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }

            binding.share.setSafeOnClickListener {
                onShareClick.invoke(data)
            }

            binding.check.isVisible = if (data.userProfile.subscriptions_end != null) {
                !data.userProfile.subscriptions_end.checkIfDateGone(inputFormat = "MM/dd/yyyy")
            } else {
                false
            }


            binding.bookmark.setSafeOnClickListener {
                if (data.isFavorite) {
                    removeFavourite(data, position)
                } else {
                    addFavourite(data, position)
                }
            }

            binding.btnDetails.setSafeOnClickListener {
                onButtonClick.invoke(data)
            }

            binding.report.setSafeOnClickListener {
                onReportClick.invoke(data)
            }
        }
    }
}

