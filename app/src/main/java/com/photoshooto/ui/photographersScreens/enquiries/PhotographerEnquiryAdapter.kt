package com.photoshooto.ui.photographersScreens.enquiries

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.PhotographerEnquiriesItemsBinding
import com.photoshooto.util.callPhoneNumber
import com.photoshooto.util.sendEmail


class PhotographerEnquiryAdapter(val context: Context, val type: String) :
    RecyclerView.Adapter<PhotographerEnquiryAdapter.Holder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding =
            PhotographerEnquiriesItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        with(holder.binding) {

            if (tvViewMore.tag == null) {
                tvViewMore.tag = false
                contactIcon.visibility = View.VISIBLE
                llMore.visibility = View.GONE
            } else if (tvViewMore.tag == true) {
                llMore.visibility = View.VISIBLE
                contactIcon.visibility = View.GONE
            }
            tvViewMore.setOnClickListener {
                if (tvViewMore.tag == true) {
                    tvViewMore.tag = false
                    contactIcon.visibility = View.VISIBLE
                    llMore.visibility = View.GONE
                    eventDate1.visibility = View.VISIBLE
                } else {
                    tvViewMore.tag = true
                    contactIcon.visibility = View.GONE
                    llMore.visibility = View.VISIBLE
                    eventDate1.visibility = View.GONE
                }
            }


            btnContact.setOnClickListener {
                callPhoneNumber(
                    context!! as FragmentActivity,
                    (R.string.support_phone_no.toString())
                )
            }

            btnSendMessage.setOnClickListener {
                sendEmail(
                    context!! as FragmentActivity,
                    (R.string.support_email_id.toString()),
                    "Support"
                )
            }

            if ("unread".equals(type, ignoreCase = true)) {
                cardEnquiryConstraint.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.new_reuqest_blue
                    )
                )
            } else {
                cardEnquiryConstraint.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = 5


    class Holder(
        val binding: PhotographerEnquiriesItemsBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }
}