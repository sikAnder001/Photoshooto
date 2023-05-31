package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.AdapterQrViewpagerImageSliderBinding

class ImagePagerAdapter(
    var images: List<Int>,
    private var itemListener: OnItemClickListener
) : RecyclerView.Adapter<ImagePagerAdapter.ViewPagerHolder>() {
    companion object {
        lateinit var context: Context
    }

    inner class ViewPagerHolder(private var binding: AdapterQrViewpagerImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Int) {
            binding.picOfDayImage.setImageDrawable(ContextCompat.getDrawable(context, image))

            binding.rootView.setOnClickListener {
                itemListener.onImageClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterQrViewpagerImageSliderBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    interface OnItemClickListener {
        fun onImageClick()
    }
}
