package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ListJobRowBinding
import com.photoshooto.databinding.ListPhotographerRowBinding
import com.photoshooto.domain.model.JobModel
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefsHelper
import com.photoshooto.util.invisible
import com.squareup.picasso.Picasso

class MyPostListAdapter(
    val onShareClick: (data: JobModel, type: String) -> Unit,
    val onEditClick: (data: JobModel) -> Unit,
    val onButtonClick: (data: JobModel, position: Int) -> Unit,
) : ListAdapter<JobModel, RecyclerView.ViewHolder>(object : ItemCallback<JobModel>() {
    override fun areItemsTheSame(oldItem: JobModel, newItem: JobModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: JobModel, newItem: JobModel): Boolean {
        return oldItem == newItem
    }
}) {

    private val TYPE_HIRE_ME: Int = 1   //photograpger
    private val TYPE_HIRE_YOU: Int = 2  //job

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
            holder.bind(getItem(position) as JobModel, position)
        } else if (holder is RecyclerHolderHireMe) {
            holder.bind(getItem(position) as JobModel, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is JobModel)
            if (item.type == "hireyou")
                TYPE_HIRE_YOU         //job
            else
                TYPE_HIRE_ME         //photographer
        else super.getItemViewType(position)
    }

    inner class RecyclerHolderHireYou(val binding: ListJobRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JobModel, position: Int) {

            binding.bookmark.invisible()
            binding.edit.invisible()

            binding.share.setSafeOnClickListener {
                onShareClick.invoke(data, "HIRE_YOU")
            }

            binding.edit.setSafeOnClickListener {
                onEditClick.invoke(data)
            }

            binding.check.isVisible = SharedPrefsHelper.getSubscribed()

            binding.btnDetails.text = "Remove"
            binding.btnDetails.setSafeOnClickListener {

                onButtonClick.invoke(data, position)
            }

            binding.report.invisible()

            if (data.status == "approved") {
                binding.verified.visible()
                binding.verifiedIcon.visible()
            } else {
                binding.verified.gone()
                binding.verifiedIcon.gone()
            }

            binding.verified.isVisible = data.isVerified
            binding.verifiedIcon.isVisible = data.isVerified

            if (data.userProfile.online_status.equals("online", true)) {
                binding.dot.visible()
            } else {
                binding.dot.invisible()
            }

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

            //  data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }
            //FOR SOME UNKNOWN Reason with Glide onclick listener were not working
            Picasso.get().load(data.userProfile.profile_image ?: "")
                .placeholder(R.drawable.icn_placeholder_user)
                .error(R.drawable.icn_placeholder_user)
                .into(binding.imageViewProfile)

        }
    }

    inner class RecyclerHolderHireMe(val binding: ListPhotographerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JobModel, position: Int) {

            //FOR SOME UNKNOWN Reason with Glide onclick listener were not working
            Picasso.get().load(data.userProfile.profile_image ?: "")
                .placeholder(R.drawable.icn_placeholder_user)
                .error(R.drawable.icn_placeholder_user)
                .into(binding.imageViewProfile)

            binding.adId.text = "AD ID: ${data.id}"
            binding.bookmark.invisible()
            binding.edit.invisible()

            if (data.userProfile.online_status.equals("online", true)) {
                binding.dot.visible()
            } else {
                binding.dot.invisible()
            }

            binding.check.isVisible = if (data.userProfile.subscriptions_end != null) {
                !data.userProfile.subscriptions_end.checkIfDateGone(inputFormat = "MM/dd/yyyy")
            } else {
                false
            }

            binding.userName.text = data.userProfile.name
            binding.event.text = data.eventType
            binding.userId.text = data.userId
            binding.city.text = data.city.toString().replace("[", "").replace("]", "")
            binding.verified.isVisible = data.isVerified
            binding.verifiedIcon.isVisible = data.isVerified


            binding.rating.text = data.userProfile.rating?.singleDecimalPlaces().toString()
            binding.ratingCount.text = "(${data.feedbacks.size} rating)"
            binding.time.text = data.createdAt.getDateTimeDiffSingle()
            binding.camera.text = data.userId
            binding.adId.text = "AD ID: ${data.id}"

            val equipment = (data.photoCameraUse + data.videoCameraUse + data.otherEquipments)
            if (equipment.isNotEmpty()) {
                if (equipment.size > 1) binding.camera.text =
                    "${equipment.first()} + ${equipment.size - 1} Others"
                else binding.camera.text = equipment.first()
            } else {
                binding.camera.text = ""
            }

            if (data.status == "approved") {
                binding.verified.visible()
                binding.verifiedIcon.visible()
            } else {
                binding.verified.gone()
                binding.verifiedIcon.gone()
            }
            binding.share.setSafeOnClickListener {
                onShareClick.invoke(data, "HIRE_ME")
            }

            binding.edit.setSafeOnClickListener {
                onEditClick.invoke(data)
            }

            binding.btnDetails.text = "Remove"
            binding.btnDetails.setSafeOnClickListener {
                onButtonClick.invoke(data, position)
            }
            binding.report.invisible()


            //data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }
        }
    }
}