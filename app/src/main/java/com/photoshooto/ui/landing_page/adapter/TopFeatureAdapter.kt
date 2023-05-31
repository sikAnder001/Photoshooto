package com.photoshooto.ui.landing_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photoshooto.databinding.AdapterTopFeatureBinding
import com.photoshooto.domain.model.TopFeature

/**
 * landing screen top feature adapter
 * created by amardeep manav
 */
class TopFeatureAdapter constructor(
    private val context: Context,
    private val values: List<TopFeature>,
    private val itemListener: OnItemClickListener
) : RecyclerView.Adapter<TopFeatureAdapter.ViewHolder>() {

    private val TAG = TopFeatureAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = AdapterTopFeatureBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = values[position]
        holder.bind(task, holder.itemView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(private val itemBinding: AdapterTopFeatureBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(topFeature: TopFeature, itemView: View) {

            Glide.with(context)
                .load(topFeature.icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemBinding.icon)

            val title = topFeature.title
            itemBinding.title.text = if (title == "Upgrade") {
                "Upgrade Pro"
            } else {
                topFeature.title ?: ""
            }
            itemView.rootView.setOnClickListener {
                itemListener.onDetailsClick(topFeature)
            }
        }
    }

    interface OnItemClickListener {
        fun onDetailsClick(topFeature: TopFeature)
    }
}
