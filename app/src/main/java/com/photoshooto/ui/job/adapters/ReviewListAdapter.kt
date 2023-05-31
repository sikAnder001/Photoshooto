package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.ListReviewRowBinding
import com.photoshooto.domain.model.ReviewFeedbackModel
import com.photoshooto.ui.job.utility.getFormattedDatetime
import com.photoshooto.ui.job.utility.loadImageUser
import com.photoshooto.ui.job.utility.setSafeOnClickListener

class ReviewListAdapter(
    val onItemClick: (data: ReviewFeedbackModel) -> Unit
) : ListAdapter<ReviewFeedbackModel, ReviewListAdapter.RecyclerHolder>(object :
    ItemCallback<ReviewFeedbackModel>() {
    override fun areItemsTheSame(
        oldItem: ReviewFeedbackModel, newItem: ReviewFeedbackModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ReviewFeedbackModel, newItem: ReviewFeedbackModel
    ): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ListReviewRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(getItem(position))

    }

    inner class RecyclerHolder(val binding: ListReviewRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReviewFeedbackModel) {

            binding.description.text = data.message

            binding.date.text = "Reviewed in ${
                data.createdAt.getFormattedDatetime(
                    outputFormat = "dd MMMM, yyyy"
                )
            }"
            binding.userId.text = "ID ${data.userId}"

            binding.rating.rating = data.rating.toFloat()

            data.userProfile.profile_image?.let { loadImageUser(binding.imageViewProfile, it) }

            binding.name.text = data.userProfile.name

            binding.description.setSafeOnClickListener {
                if (binding.description.maxLines == 2) {
                    binding.description.maxLines = 100
                } else {
                    binding.description.maxLines = 2
                }
            }
        }
    }
}

