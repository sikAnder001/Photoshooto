package com.photoshooto.ui.photographer.adapter

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.ReviewItemViewBinding
import com.photoshooto.domain.model.ReviewByUserIdResponse
import com.photoshooto.util.urlAddingForPicture
import java.text.ParseException
import java.util.*

private const val SECOND = 1
private const val MINUTE = 60 * SECOND
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR
private const val MONTH = 30 * DAY
private const val YEAR = 12 * MONTH

class ReviewAdapter(
    var dataList: List<ReviewByUserIdResponse.ReviewData.UserReviewsList>,
    var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {


    inner class ViewHolder(var binding: ReviewItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(data: ReviewByUserIdResponse.ReviewData.UserReviewsList) {

            binding.name.text = data.userProfile?.name
            binding.timeStamp.text = data.updatedAt
            binding.ratingText.text = "" + data.rating + " Rating"

            binding.ratingText.apply {
                if (data.rating!! > 3) {
                    backgroundTintList = context?.getColorStateList(R.color.green_color)
                    context?.getColor(R.color.green_color)?.let { setTextColor(it) }
                    compoundDrawableTintList = context?.getColorStateList(R.color.green_color)
                } else {
                    backgroundTintList = context?.getColorStateList(R.color.red_color)
                    context?.getColor(R.color.red_color)?.let { setTextColor(it) }
                    compoundDrawableTintList = context?.getColorStateList(R.color.red_color)
                }
            }

            binding.description.text = data.message
            binding.tvLikes.text = " ${data.likes} "
            binding.timeStamp.text = getDateDiff(data)
            Glide.with(binding.root.context).load(data.userProfile?.profileImage?.let {
                urlAddingForPicture(
                    it
                )
            })
                .error(
                    R.drawable.ic_temp_user_profile
                )
                .placeholder(R.drawable.ic_temp_user_profile)
                .into(binding.profileImage)

        }


        @SuppressLint("NewApi")
        @RequiresApi(Build.VERSION_CODES.N)
        fun getDateDiff(data: ReviewByUserIdResponse.ReviewData.UserReviewsList): String {
            val format = "yyyy-MM-dd'T'HH:mm:ss"
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            val date = data.createdAt!!
            val registerDate = uTCToLocal(format, format, date)
            val userDob: Date = dateFormat.parse(registerDate)

            val today = Date()
            val diff: Long = (today.time - userDob.time) / 1000
            return when {
                diff < MINUTE -> "Just now"
                diff < 2 * MINUTE -> "a minute ago"
                diff < 60 * MINUTE -> "${diff / MINUTE} minutes ago"
                diff < 2 * HOUR -> "an hour ago"
                diff < 24 * HOUR -> "${diff / HOUR} hours ago"
                diff < 2 * DAY -> "yesterday"
                diff < 30 * DAY -> "${diff / DAY} days ago"
                diff < 2 * MONTH -> "a month ago"
                diff < 12 * MONTH -> "${diff / MONTH} months ago"
                diff < 2 * YEAR -> "a year ago"
                else -> "${diff / YEAR} years ago"
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun uTCToLocal(
            dateFormatInPut: String?, dateFomratOutPut: String?, datesToConvert: String?
        ): String? {
            var dateToReturn = datesToConvert
            val sdf = SimpleDateFormat(dateFormatInPut)
            sdf.timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
            var gmt: Date? = null
            val sdfOutPutToSend = SimpleDateFormat(dateFomratOutPut)
            sdfOutPutToSend.timeZone = android.icu.util.TimeZone.getDefault()
            try {
                gmt = sdf.parse(datesToConvert)
                dateToReturn = sdfOutPutToSend.format(gmt)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return dateToReturn
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ReviewItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size


    interface OnItemClickListener {
        fun onDetailsClick(position: Int, data: ReviewByUserIdResponse.ReviewData.UserReviewsList)
    }
}