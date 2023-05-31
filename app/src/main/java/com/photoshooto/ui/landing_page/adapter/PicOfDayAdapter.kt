package com.photoshooto.ui.landing_page.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photoshooto.databinding.AdapterPicOfDayLandingScreenBinding
import com.photoshooto.domain.model.PicOfDay

class PicOfDayAdapter(
    private val context: Context,
    private val data: List<PicOfDay>,
) : RecyclerView.Adapter<PicOfDayAdapter.ViewHolder>() {
    private val TAG = PicOfDayAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterPicOfDayLandingScreenBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(
        val inflater: AdapterPicOfDayLandingScreenBinding,
    ) : RecyclerView.ViewHolder(inflater.root) {
        fun bind(item: PicOfDay) {
            Log.e("file path", "" + item.file_path)
            Glide.with(context).load(item.file_path).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(inflater.picOfDayImage)
        }
    }

}