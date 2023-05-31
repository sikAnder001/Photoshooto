package com.photoshooto.ui.job.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ListSubscribeFeaturesBinding
import com.photoshooto.domain.model.FeaturePlan
import com.photoshooto.ui.job.utility.gone
import com.photoshooto.ui.job.utility.invisible
import com.photoshooto.ui.job.utility.visible

class FeatureListAdapter(
    val onItemClick: (data: FeaturePlan) -> Unit
) : ListAdapter<FeaturePlan, FeatureListAdapter.RecyclerHolder>(object :
    ItemCallback<FeaturePlan>() {
    override fun areItemsTheSame(
        oldItem: FeaturePlan, newItem: FeaturePlan
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: FeaturePlan, newItem: FeaturePlan
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ListSubscribeFeaturesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(
            getItem(position), position,
            this.currentList.size
        )
    }

    inner class RecyclerHolder(val binding: ListSubscribeFeaturesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FeaturePlan, position: Int, size: Int) {

            binding.name.text = data.featureName

            if (data.featureId.equals("pro_profile_link_in")) {
                binding.name.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, binding.name.context.getDrawable(R.drawable.whatsapp_icon),

                    null
                )
            } else {
                binding.name.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, null,
                    null
                )
            }

            data.plan1.apply {
                if (equals("true")) {
                    binding.text1.invisible()
                    binding.option1.setImageResource(R.drawable.ic_plan_tick)
                } else if (equals("false") or equals("0")) {
                    binding.text1.invisible()
                    binding.option1.setImageResource(R.drawable.ic_plan_cross)
                } else {
                    binding.option1.invisible()
                    binding.text1.text = this
                }
            }

            data.plan2.apply {
                if (equals("true")) {
                    binding.text2.invisible()
                    binding.option2.setImageResource(R.drawable.ic_plan_tick)
                } else if (equals("false") or equals("0")) {
                    binding.text2.invisible()
                    binding.option2.setImageResource(R.drawable.ic_plan_cross)
                } else {
                    binding.option2.invisible()
                    binding.text2.text = this.toString()
                }
            }

            data.plan3.apply {
                if (equals("true")) {
                    binding.text3.invisible()
                    binding.option3.setImageResource(R.drawable.ic_plan_tick)
                } else if (equals("false") or equals("0")) {
                    binding.text3.invisible()
                    binding.option3.setImageResource(R.drawable.ic_plan_cross)
                } else {
                    binding.option3.invisible()
                    binding.text3.text = this.toString()
                }
            }

            data.plan4.apply {
                if (equals("true")) {
                    binding.text4.invisible()
                    binding.option4.setImageResource(R.drawable.ic_plan_tick)
                } else if (equals("false") or equals("0")) {
                    binding.text4.invisible()
                    binding.option4.setImageResource(R.drawable.ic_plan_cross)
                } else {
                    binding.option4.invisible()
                    binding.text4.text = this.toString()
                }
            }


            if (data.featureId.equals("total_yearly_savings")) {
                binding.text1.apply {
                    setTextColor(Color.BLACK)
                    setTextAppearance(itemView.context, R.style.tvFontBold)
                    text = "+${text}%"
                }

                binding.text2.apply {
                    setTextColor(Color.BLACK)
                    setTextAppearance(itemView.context, R.style.tvFontBold)
                    text = "+${text}%"
                }

                binding.text3.apply {
                    setTextColor(Color.BLACK)
                    setTextAppearance(itemView.context, R.style.tvFontBold)
                    text = "+${text}%"
                }

                binding.text4.apply {
                    setTextColor(Color.BLACK)
                    setTextAppearance(itemView.context, R.style.tvFontBold)
                    text = "+${text}%"
                }

                binding.name.apply {
                    setTextColor(Color.BLACK)
                    setTextAppearance(itemView.context, R.style.tvFontBold)
                }
            }

            when (data.selectedPlan) {
                "Promo" -> {
                    binding.bgView1.visible()
                    binding.bgView2.invisible()
                    binding.bgView3.invisible()
                    binding.bgView4.invisible()
                }
                "Studio" -> {
                    binding.bgView1.invisible()
                    binding.bgView2.invisible()
                    binding.bgView3.visible()
                    binding.bgView4.invisible()
                }
                "Teams" -> {
                    binding.bgView1.invisible()
                    binding.bgView2.invisible()
                    binding.bgView3.invisible()
                    binding.bgView4.visible()
                }
                else -> {
                    binding.bgView1.invisible()
                    binding.bgView2.visible()
                    binding.bgView3.invisible()
                    binding.bgView4.invisible()
                }
            }

            if (!data.plan1Visible) {
                binding.bgView1.gone()
            }

            if (data.plan1Visible) {
                if (data.plan1 in listOf("true", "false", "0")) {
                    binding.text1.invisible()
                    binding.option1.visible()
                } else {
                    binding.text1.visible()
                    binding.option1.invisible()
                }
            } else {
                binding.text1.gone()
                binding.option1.gone()
            }
        }
    }
}

