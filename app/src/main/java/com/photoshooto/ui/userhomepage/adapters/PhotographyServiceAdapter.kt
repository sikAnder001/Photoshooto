package com.photoshooto.ui.userhomepage.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photoshooto.R
import com.photoshooto.databinding.AdapterPhotographyServicesItemsBinding
import com.photoshooto.domain.model.PhotographerServiceModel

class PhotographyServiceAdapter(
    val context: Context,
    val list: List<PhotographerServiceModel.Data?>
) :
    RecyclerView.Adapter<PhotographyServiceAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            AdapterPhotographyServicesItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {


        holder.binding.tvTitle.text = list[position]?.type!!

        val icon = list[position]?.icon
        Glide.with(context)
            .load(icon)
            .placeholder(R.drawable.ic_wedding_photography)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.profileImage)
        holder.binding.lvPhotographyType.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", list[position]?.type!!)

            it.findNavController()
                .navigate(
                    R.id.action_topPhotographerServiceFragment_to_photographerTypeFragment,
                    bundle
                )
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class Holder(
        val binding: AdapterPhotographyServicesItemsBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }

}