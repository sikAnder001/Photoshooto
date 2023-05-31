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
import com.photoshooto.databinding.PhotographerServiceItemsBinding
import com.photoshooto.domain.model.UserDashboardModel


class PhotographerServiceAdapter(private val photographerService: List<UserDashboardModel.Data.PhotographerService?>) :
    RecyclerView.Adapter<PhotographerServiceAdapter.Holder>() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        context = parent.context
        return Holder(
            PhotographerServiceItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val services = photographerService[position]

        var service = services?.type

        if (!service.isNullOrEmpty()) {
            if (service.contains("Photography")) {
                service = service.replace("Photography", "")
                holder.binding.textServices.text = service.trim()
            }

            if (service.contains("/Ring Ceremony")) {
                service = service.replace("/Ring Ceremony", "")
                holder.binding.textServices.text = service.trim()
            }
        }


        /*Glide.with(context)
            .load(services?.icon)
            .into(object : CustomTarget<Drawable?>() {
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    holder.binding.photographyServiceImg.background = resource
                }
            })*/
        Glide.with(context)
            .load(services?.icon)
            .placeholder(R.drawable.ic_wedding_photography)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.photographyServiceImg)


        holder.binding.photographyServiceImg.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", services?.type)

            it.findNavController()
                .navigate(
                    R.id.action_homePageFragment_to_photographerTypeFragment,
                    bundle
                )
        }
    }

    override fun getItemCount(): Int {
        return photographerService.size
    }


    inner class Holder(var binding: PhotographerServiceItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}