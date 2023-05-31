package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.CheckboxWithTextItemBinding
import com.photoshooto.domain.model.SpinnerModel

class CheckBoxFilterAdapter(
    val onItemClick: (data: SpinnerModel, click: Boolean) -> Unit
) : ListAdapter<SpinnerModel, CheckBoxFilterAdapter.RecyclerHolder>(object :
    ItemCallback<SpinnerModel>() {
    override fun areItemsTheSame(
        oldItem: SpinnerModel, newItem: SpinnerModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: SpinnerModel, newItem: SpinnerModel
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            CheckboxWithTextItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(getItem(position), holder.adapterPosition)
    }

    inner class RecyclerHolder(val binding: CheckboxWithTextItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SpinnerModel, position: Int) {

            binding.checkboxJob.text = data.baseName
            binding.checkboxJob.isChecked = data.isSelected

            binding.checkboxJob.setOnCheckedChangeListener { compoundButton, check ->
                if (compoundButton.isPressed) {
                    currentList[position].isSelected = check
                    onItemClick(data, check)
                }
            }
        }
    }
}

