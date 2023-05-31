package com.photoshooto.ui.userhomepage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.AdapterPhotographerListItemBinding
import com.photoshooto.domain.model.UserElement

class FavouritePhotographerListAdapter(
    var context: Context,
    private val onUserItemClickListener: OnUserItemClickListener
) :
    RecyclerView.Adapter<FavouritePhotographerListAdapter.Holder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding =
            AdapterPhotographerListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        if (holder.binding.imgFav.tag == null) {
            holder.binding.imgFav.tag = true
            holder.binding.imgFav.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_red_heart_filled
                )
            )
        } else if (holder.binding.imgFav.tag == false) {
            holder.binding.imgFav.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_red_heart
                )
            )
        }


        holder.binding.imgFav.setOnClickListener {

            if (holder.binding.imgFav.tag == true) {
                holder.binding.imgFav.tag = false
                holder.binding.imgFav.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_red_heart
                    )
                )
                // onUserItemClickListener.onUserFavProfileClicked(list[position], false)
            } else {
                holder.binding.imgFav.tag = true
                holder.binding.imgFav.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_red_heart_filled
                    )
                )
                //onUserItemClickListener.onUserFavProfileClicked(list[position], true)
            }


        }

        /* holder.bind(list!![position])
         holder.binding.tvViewProfile.setOnClickListener {
             onUserItemClickListener.onViewProfileClicked(list[position])
         }

         holder.binding.imgCall.setOnClickListener {
             onUserItemClickListener.onEnquireClicked(list[position])
         }*/
    }

    override fun getItemCount(): Int {
        return 10
    }

    class Holder(
        val binding: AdapterPhotographerListItemBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserElement) {
            /*with(binding) {
                tvUserName.text = data.profile_details?.name
                tvPhotographerType.text = data.profile_details?.profession
                tvStudioName.text = data.profile_details?.studio_name
                if (!data.profile_details?.profile_image.isNullOrEmpty()) {
                    Glide.with(context).load(DOMAIN + data.profile_details?.profile_image)
                        .into(binding.profileImage)
                }
            }*/
        }
    }

    interface OnUserItemClickListener {
        fun onEnquireClicked(user: UserElement)
        fun onViewProfileClicked(user: UserElement)
        fun onUserFavProfileClicked(user: UserElement, isFav: Boolean)
    }
}