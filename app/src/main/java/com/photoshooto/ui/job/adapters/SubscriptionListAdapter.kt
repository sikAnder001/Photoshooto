package com.photoshooto.ui.job.adapters

import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ListSubscribePlanBinding
import com.photoshooto.domain.model.SubscribePlan
import com.photoshooto.ui.job.utility.invisible
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import com.photoshooto.ui.job.utility.visible

class SubscriptionListAdapter(
    val onItemClick: (data: SubscribePlan) -> Unit
) : ListAdapter<SubscribePlan, SubscriptionListAdapter.RecyclerHolder>(object :
    ItemCallback<SubscribePlan>() {
    override fun areItemsTheSame(
        oldItem: SubscribePlan, newItem: SubscribePlan
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: SubscribePlan, newItem: SubscribePlan
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ListSubscribePlanBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(getItem(position), position)
    }


    private fun selectItem(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.forEachIndexed { index, subscribePlan ->
            subscribePlan.isSelected = index == position
        }
        submitList(currentList)
        notifyDataSetChanged()
    }

    inner class RecyclerHolder(val binding: ListSubscribePlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SubscribePlan, position: Int) {

            binding.planName.text = data.type

            if (data.isRecommended) {
                binding.recommended.visible()
            } else {
                binding.recommended.invisible()
            }

            if (data.isSelected) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.planName.backgroundTintList =
                        binding.planName.context.getColorStateList(R.color.textColor)
                }
                binding.plansRow.setBackgroundResource(R.drawable.bg_plan_selected)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.planName.backgroundTintList =
                        binding.planName.context.getColorStateList(R.color.grey_81)
                }
                binding.plansRow.setBackgroundResource(R.drawable.bg_plan_unselected)
            }

            binding.row.setSafeOnClickListener {
                selectItem(position)
                onItemClick.invoke(data)
            }

            val tmp = if (data.isYearSelected) "₹ ${data.yearPrice}" else "₹ ${data.price}"

            val priceSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(binding.price.context, R.color.colorBlack)
                    ds.textSize = 50.0f
                    ds.typeface = Typeface.DEFAULT_BOLD
                    ds.isUnderlineText = false
                }
            }

            val spannableString = SpannableString(tmp)
            spannableString.setSpan(
                priceSpan, 0, tmp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (data.isYearSelected) {
                binding.price.text = TextUtils.concat(
                    spannableString, " /6 Months"
                )
            } else {
                binding.price.text = TextUtils.concat(
                    spannableString, " /Month"
                )
            }
        }
    }
}

