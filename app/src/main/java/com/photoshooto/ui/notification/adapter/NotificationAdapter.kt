package com.photoshooto.ui.notification.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photoshooto.databinding.NotifiItemBinding
import com.photoshooto.ui.notification.NotificationActivity.Companion.getDate
import com.photoshooto.ui.notification.NotificationActivity.Companion.getTime
import com.photoshooto.ui.notification.model.NotificationItem

class NotificationAdapter constructor(
    private val context: Context,
    private val values: List<NotificationItem>
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    init {
    }

    companion object {
        public const val TAG = "NotificationAdapter"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = NotifiItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = values.get(position)
        holder.bind(task, holder.itemView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val itemBinding: NotifiItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: NotificationItem, itemView: View) {
            Log.d("Mazroid == ", task.id)

            Glide.with(context)
                .load(task.userProfile.backgroundImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemBinding.productImage)

            itemBinding.tvName.text = task.userProfile.name ?: ""
            itemBinding.tvDesc.text = task.message ?: ""
            itemBinding.tvDate.text = getDate(task.createdAt) ?: ""
            itemBinding.tvTime.text = getTime(task.createdAt) ?: ""
        }
    }

}
