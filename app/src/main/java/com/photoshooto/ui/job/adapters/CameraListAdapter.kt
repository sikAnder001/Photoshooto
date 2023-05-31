package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.ListCameraHomeRowBinding
import com.photoshooto.domain.model.CameraModel

class CameraListAdapter(
    val onDetailClick: (data: CameraModel) -> Unit
) : ListAdapter<CameraModel, CameraListAdapter.RecyclerHolder>(object :
    ItemCallback<CameraModel>() {
    override fun areItemsTheSame(oldItem: CameraModel, newItem: CameraModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CameraModel, newItem: CameraModel): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            ListCameraHomeRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecyclerHolder(val binding: ListCameraHomeRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CameraModel) {

            /* binding.name.text = data.baseName.trim()

             binding.size.text = if (data.sizeDir > 0) {
                 "${data.sizeDir} Items"
             } else {
                 "${data.sizeFiles} Items"
             }*/

        }
    }
}

