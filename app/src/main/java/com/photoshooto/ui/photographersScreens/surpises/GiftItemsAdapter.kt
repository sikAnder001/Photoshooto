package com.photoshooto.ui.photographersScreens.photographerDashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AdapterUnlockGiftItemsBinding

class GiftItemsAdapter(val context: Context, var onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<GiftItemsAdapter.holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val binding = AdapterUnlockGiftItemsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return GiftItemsAdapter.holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        holder.binding.btnUnlockNow.setOnClickListener(View.OnClickListener {
            onItemClickListener.onItemClick(position)
        })
    }

    override fun getItemCount(): Int {
        return 6
    }

    class holder(
        val binding: AdapterUnlockGiftItemsBinding, private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}