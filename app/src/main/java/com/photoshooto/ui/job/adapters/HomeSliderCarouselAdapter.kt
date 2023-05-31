package com.photoshooto.ui.job.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.ItemBannerBinding
import com.photoshooto.domain.model.Banner
import com.photoshooto.ui.job.utility.loadImage

class HomeSliderCarouselAdapter(
    private val arrayList: ArrayList<Banner>,
    private val callback: (temp: Banner, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var rowBinding: ItemBannerBinding

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        rowBinding =
            ItemBannerBinding
                .inflate(LayoutInflater.from(container.context), container, false)
        return ViewHolder(rowBinding)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        loadImage(
            rowBinding.imgBanner,
            arrayList[position].background_image!!
        )

        holder.itemView.setOnClickListener {
            callback.invoke(arrayList[position], position)
        }
    }

    inner class ViewHolder(binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}
