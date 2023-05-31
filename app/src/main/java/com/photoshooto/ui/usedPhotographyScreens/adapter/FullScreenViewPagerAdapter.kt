package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.util.ZoomImageView


class FullScreenViewPagerAdapter() :
    RecyclerView.Adapter<FullScreenViewPagerAdapter.FullScreenViewPager>() {
    var items: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullScreenViewPager {
        return FullScreenViewPager(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.full_screen_viewpager, parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FullScreenViewPager, position: Int) {
        val item = items[position]
        if (item != null) {
            holder.bind(item, position)
        }
    }

    fun swapList(items: java.util.ArrayList<String>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class FullScreenViewPager(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgZoom: ZoomImageView

        fun bind(item: String, position: Int) {

            if (item.contains("https")) {
                Glide.with(itemView.context)
                    .load(item)
                    .into(imgZoom)
            } else {
                val bmOptions = BitmapFactory.Options()

                val bitmap = BitmapFactory.decodeFile(item, bmOptions)
                imgZoom.setImageBitmap(bitmap)
            }
        }

        init {
            imgZoom = itemView.findViewById(R.id.imgZoom)
        }
    }
}