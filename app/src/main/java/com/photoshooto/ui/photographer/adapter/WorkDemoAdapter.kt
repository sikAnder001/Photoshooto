package com.photoshooto.ui.photographer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photoshooto.databinding.WorkDemoItemViewBinding
import com.photoshooto.domain.model.WorkDemoItem

class WorkDemoAdapter(
    var context: Context, var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<WorkDemoAdapter.ViewHolder>() {

    var dataList = ArrayList<WorkDemoItem>()

    inner class ViewHolder(var binding: WorkDemoItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WorkDemoItem, position: Int) {
            Glide.with(binding.root.context).load(data.file_path)
                .diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).into(binding.fileImage)
            binding.root.setOnClickListener {
                onItemClickListener.onDetailsClick(position, dataList)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            WorkDemoItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], position)
    }

    override fun getItemCount() = dataList.size

    fun setList(data: ArrayList<WorkDemoItem>) {
        this.dataList = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onDetailsClick(position: Int, data: ArrayList<WorkDemoItem>)
    }
}