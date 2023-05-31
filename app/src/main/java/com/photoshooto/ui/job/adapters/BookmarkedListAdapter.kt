package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ListJobRowBinding
import com.photoshooto.databinding.ListPhotographerRowBinding
import com.photoshooto.domain.model.BookmarkJob
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import com.photoshooto.util.visible

class BookmarkedListAdapter(
    val onShareClick: (data: BookmarkJob, type: String) -> Unit,
    private val onBookmarkClick: (data: BookmarkJob) -> Unit,
    val onButtonClick: (data: BookmarkJob, type: String) -> Unit,
    val onReportClick: (data: BookmarkJob, type: String) -> Unit
) : ListAdapter<BookmarkJob, RecyclerView.ViewHolder>(object : ItemCallback<BookmarkJob>() {
    override fun areItemsTheSame(oldItem: BookmarkJob, newItem: BookmarkJob): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BookmarkJob, newItem: BookmarkJob): Boolean {
        return oldItem == newItem
    }
}) {

    private val TYPE_HIRE_ME: Int = 1   //photograpger
    private val TYPE_HIRE_YOU: Int = 2  //jos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HIRE_ME) {
            RecyclerHolderHireMe(
                ListPhotographerRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            RecyclerHolderHireYou(
                ListJobRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (holder is RecyclerHolderHireYou) {
            holder.bind(getItem(position) as BookmarkJob, position)
        } else if (holder is RecyclerHolderHireMe) {
            holder.bind(getItem(position) as BookmarkJob, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is BookmarkJob) if (item.photographerId.isNullOrBlank()) TYPE_HIRE_YOU         //job
        else TYPE_HIRE_ME         //photographer
        else super.getItemViewType(position)
    }

    private fun removeItem(bookmarkJob: BookmarkJob, pos: Int) {
        val currentList = currentList.toMutableList()
        currentList.remove(bookmarkJob)
        submitList(currentList)
        onBookmarkClick.invoke(bookmarkJob)
    }

    inner class RecyclerHolderHireYou(val binding: ListJobRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BookmarkJob, position: Int) {

            binding.adId.text = "AD ID: ${data.jobId}"
            binding.bookmark.setImageResource(R.drawable.ic_bookmark_filled)

            binding.share.setSafeOnClickListener {
                onShareClick.invoke(data, "HIRE_YOU")
            }

            binding.eventName.text =
                (data.jobDetails.title ?: "").ifBlank { data.jobDetails.eventType ?: "" }

            val studioName = data.userProfile.studio_name
            if (!studioName.isNullOrEmpty()) {
                binding.studioName.text = studioName
                binding.studioName.visible()
                binding.check.isVisible = SharedPrefsHelper.getSubscribed()

            } else {
                binding.studioName.gone()
                binding.check.gone()
            }

            binding.stuId.text = "ID: ${data.userId}"
            binding.time.text = data.createdAt.getDateTimeDiffSingle()
            binding.city.text = data.jobDetails.addressObj.parseAddress()
            binding.stDate.text =
                "${data.jobDetails.startDate.getFormattedDatetime(outputFormat = "dd/MM/yyyy")}, ${data.jobDetails.startTime}"

            binding.endDate.text =
                "${data.jobDetails.endDate.getFormattedDatetime(outputFormat = "dd/MM/yyyy")}, ${data.jobDetails.endTime}"
            data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }

            binding.report.visibility =
                if (data.userId == SharedPrefsHelper.read(SharedPrefConstant.USER_ID)) {
                    View.INVISIBLE
                } else View.VISIBLE

            binding.bookmark.setSafeOnClickListener {
                removeItem(data, position)
            }

            binding.btnDetails.setSafeOnClickListener {
                onButtonClick.invoke(data, "HIRE_YOU")
            }

            binding.report.setSafeOnClickListener {
                onReportClick.invoke(data, "HIRE_YOU")
            }
        }
    }

    inner class RecyclerHolderHireMe(val binding: ListPhotographerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BookmarkJob, position: Int) {

            binding.adId.text = "AD ID: ${data.jobId ?: ""}"
            binding.bookmark.setImageResource(R.drawable.ic_bookmark_filled)


            binding.userName.text = data.userProfile.name
            binding.event.text = data.jobDetails.eventType
            binding.userId.text = data.userId
            binding.city.text = if (data.jobDetails.city.isNotEmpty()) {
                data.jobDetails.city[0]
            } else {
                ""
            }
            binding.verified.isVisible = data.jobDetails.isVerified
            binding.verifiedIcon.isVisible = data.jobDetails.isVerified

            data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }

            binding.rating.text = data.userProfile.rating?.singleDecimalPlaces().toString()
            binding.ratingCount.text = "(${data.jobDetails.feedbacks.size} rating)"
            binding.time.text = data.createdAt.getDateTimeDiffSingle()
            binding.camera.text = data.userId
            binding.adId.text = "AD ID: ${data.id}"

            val equipment =
                (data.jobDetails.photoCameraUse + data.jobDetails.videoCameraUse + data.jobDetails.otherEquipments)
            if (equipment.isNotEmpty()) {
                if (equipment.size > 1) binding.camera.text =
                    "${equipment.first()} + ${equipment.size - 1} Others"
                else binding.camera.text = equipment.first()
            } else {
                binding.camera.text = ""
            }


            binding.check.isVisible = if (data.userProfile.subscriptions_end != null) {
                !data.userProfile.subscriptions_start?.checkIfDateGone(inputFormat = "MM/dd/yyyy")!!
            } else {
                false
            }

            binding.share.setSafeOnClickListener {
                onShareClick.invoke(data, "HIRE_ME")
            }

            binding.bookmark.setSafeOnClickListener {
                removeItem(data, position)
            }

            binding.btnDetails.setSafeOnClickListener {
                onButtonClick.invoke(data, "HIRE_ME")
            }

            binding.report.visibility =
                if (data.userId == SharedPrefsHelper.read(SharedPrefConstant.USER_ID)) {
                    View.INVISIBLE
                } else View.VISIBLE

            binding.report.setSafeOnClickListener {
                onReportClick.invoke(data, "HIRE_ME")
            }
        }
    }
}

