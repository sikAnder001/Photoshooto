package com.photoshooto.ui.photographersScreens.photographerDashboard.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AllNotificationItemBinding


class AllNotificationAdapter(val context: Context) :

    RecyclerView.Adapter<AllNotificationAdapter.holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val binding =
            AllNotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: holder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 6
    }

    class holder(
        val binding: AllNotificationItemBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }
}