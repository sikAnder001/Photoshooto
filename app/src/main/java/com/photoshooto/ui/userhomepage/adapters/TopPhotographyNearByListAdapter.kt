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
import com.photoshooto.databinding.TopPhotographyNearItemBinding
import com.photoshooto.domain.model.UserDashboardModel
import com.photoshooto.util.DOMAIN

class TopPhotographyNearByListAdapter(private val photographers: List<UserDashboardModel.Data.TopPhotographer?>) :
    RecyclerView.Adapter<TopPhotographyNearByListAdapter.Holder>() {
    companion object {
        var context: Context? = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        context = parent.context
        val binding =
            TopPhotographyNearItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val user = photographers[position]
        holder.bind(user!!)
        holder.binding.llMain.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userID", user.id!!)
            it.findNavController()
                .navigate(
                    R.id.action_homePageFragment_to_photographerDetailFragment,
                    bundle
                )
        }
    }

    override fun getItemCount(): Int {
        return photographers.size
    }

    class Holder(
        val binding: TopPhotographyNearItemBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDashboardModel.Data.TopPhotographer) {

            val profileDetails = user.profileDetails
            Glide.with(context)
                .load(DOMAIN + profileDetails?.profile_image)
                .placeholder(R.drawable.dummy_photographer_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgPhotographer)

            binding.tvUserName.text = profileDetails?.name

            val profession = profileDetails?.profession
            binding.tvCategory.text =
                if (!profession.isNullOrEmpty() && !profession.equals("-", ignoreCase = true)) {
                    profession
                } else "Photographer"
            binding.ratingBar.rating = profileDetails?.rates ?: 0f
            //binding.tvCity.text = profileDetails?.

        }
    }
}