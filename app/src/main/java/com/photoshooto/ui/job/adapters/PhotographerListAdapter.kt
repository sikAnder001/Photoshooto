package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ListPhotographerRowBinding
import com.photoshooto.domain.model.JobModel
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper

class PhotographerListAdapter(
    val onShareClick: (data: JobModel) -> Unit,
    val onBookmarkClick: (data: JobModel, type: String, position: Int) -> Unit,
    val onButtonClick: (data: JobModel) -> Unit,
    val onReportClick: (data: JobModel) -> Unit
) : ListAdapter<JobModel, PhotographerListAdapter.RecyclerHolder>(object :
    ItemCallback<JobModel>() {
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
            ListPhotographerRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(getItem(position))
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

    inner class RecyclerHolder(val binding: ListPhotographerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JobModel) {

            if (data.status.equals("approved")) {
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

            binding.report.isVisible =
                data.userId != SharedPrefsHelper.read(SharedPrefConstant.USER_ID)

            if (data.userProfile.online_status.equals("online", true)) {
                binding.dot.visible()
            } else {
                binding.dot.invisible()
            }

            binding.userName.text = data.userProfile.name
            binding.event.text = data.eventType
            binding.userId.text = data.userId
            binding.city.text = data.city.toString().replace("[", "").replace("]", "")
            binding.verified.isVisible = data.isVerified
            binding.verifiedIcon.isVisible = data.isVerified

            data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }

            binding.rating.text = data.userProfile.rating?.singleDecimalPlaces().toString()
            binding.ratingCount.text = "(${data.feedbacks.size} rating)"
            binding.time.text = data.createdAt.getDateTimeDiffSingle()
            binding.camera.text = data.userId
            binding.adId.text = "AD ID: ${data.id}"

            var equipment = (data.photoCameraUse + data.videoCameraUse + data.otherEquipments)
            if (equipment.isNotEmpty()) {
                if (equipment.size > 1) binding.camera.text =
                    "${equipment.first()} + ${equipment.size - 1} Others"
                else binding.camera.text = equipment.first()
            } else {
                binding.camera.text = ""
            }

            binding.check.isVisible = SharedPrefsHelper.getSubscribed()

            binding.share.setSafeOnClickListener {
                onShareClick.invoke(data)
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

