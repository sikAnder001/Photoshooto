package com.photoshooto.ui.userhomepage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.databinding.MyEnquiriesItemsBinding
import com.photoshooto.domain.model.GetEnquiryResponseModel

class UserMyEnquiriesAdapter(val list: List<GetEnquiryResponseModel.Data.ListData>) :
    RecyclerView.Adapter<UserMyEnquiriesAdapter.Holder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding =
            MyEnquiriesItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = list.get(position)
        with(holder.binding) {

            if (tvViewMore.tag == null) {
                tvViewMore.tag = false
                llMore.visibility = View.GONE
            } else if (tvViewMore.tag == true) {
                llMore.visibility = View.VISIBLE
            }
            tvViewMore.setOnClickListener {
                if (tvViewMore.tag == true) {
                    tvViewMore.tag = false
                    llMore.visibility = View.GONE
                } else {
                    tvViewMore.tag = true
                    llMore.visibility = View.VISIBLE
                }
            }

            try {
                tvUserName.text = data.name!!
                tvDate.text = data.endDate!!
                tvEventDate.text = data.startDate!!
                tvPersonName.text = data.userProfile?.name
                tvPhone.text = data.userProfile?.mobile
                tvEventType.text = data.eventType?.get(0)
                tvLocation.text = data.location
                tvLocality.text = data.locality
                tvStatus.text = data.status
            } catch (e: Exception) {
            }
        }

    }

    override fun getItemCount(): Int = list.size


    class Holder(
        val binding: MyEnquiriesItemsBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }
}