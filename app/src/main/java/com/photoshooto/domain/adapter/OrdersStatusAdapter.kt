package com.photoshooto.domain.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.ItemOrderStatusBinding
import com.photoshooto.domain.model.ListItem
import com.photoshooto.util.*


class OrdersStatusAdapter(var listItem: List<ListItem>) :
    RecyclerView.Adapter<OrdersStatusAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    class ViewPagerHolder(var binding: ItemOrderStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(data: ListItem) {
            binding.apply {
                if (ACCEPT == data.status) {
                    tvOrderId.text = data.id
                    if (STANDEE == data.type) {
                        llType.visibility = View.VISIBLE
                        llSize.visibility = View.GONE
                        tvType.text = data.standee_details?.get(0)?.type
                        Glide.with(context!!).load(data.standee_details?.get(0)?.images?.get(0))
                            .into(ivProduct)
                    } else {
                        Glide.with(context!!).load(data.tshirt_details?.get(0)?.images?.get(0))
                            .into(ivProduct)
                        llType.visibility = View.GONE
                        llSize.visibility = View.VISIBLE
                        recyclerViewSizeSelector.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        recyclerViewSizeSelector.adapter = OrderRequestSizeAdapter(
                            data.tshirt_details?.get(0)?.sizes ?: arrayListOf()
                        )
                    }
                    tvQty.text = data.order_details?.total_units.toString()
                    tvLocation.text = data.deliver_address
                    // tvPaymentStatus.text= listItem.status
                    tvPaymentStatus.visibility = View.VISIBLE
                    tvAccept.visibility = View.VISIBLE
                    tvDecline.visibility = View.GONE
                    tvHold.visibility = View.GONE
                    tvPending.visibility = View.GONE

                } else if (DECLINE == data.status) {
                    tvOrderId.text = data.id
                    if (STANDEE == data.type) {
                        llType.visibility = View.VISIBLE
                        llSize.visibility = View.GONE
                        tvType.text = data.standee_details?.get(0)?.type
                        Glide.with(context!!).load(data.standee_details?.get(0)?.images?.get(0))
                            .into(ivProduct)
                    } else {
                        Glide.with(context!!).load(data.tshirt_details?.get(0)?.images?.get(0))
                            .into(ivProduct)
                        llType.visibility = View.GONE
                        llSize.visibility = View.VISIBLE
                        recyclerViewSizeSelector.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        recyclerViewSizeSelector.adapter = OrderRequestSizeAdapter(
                            data.tshirt_details?.get(0)?.sizes ?: arrayListOf()
                        )
                    }
                    tvQty.text = data.order_details?.total_units.toString()
                    tvLocation.text = data.deliver_address
                    // tvPaymentStatus.text= listItem.status
                    tvPaymentStatus.visibility = View.GONE
                    tvAccept.visibility = View.GONE
                    tvDecline.visibility = View.VISIBLE
                    tvHold.visibility = View.GONE
                    tvPending.visibility = View.GONE
                } else if (HOLD == data.status) {
                    tvOrderId.text = data.id
                    if (STANDEE == data.type) {
                        llType.visibility = View.VISIBLE
                        llSize.visibility = View.GONE
                        tvType.text = data.standee_details?.get(0)?.type
                        Glide.with(context!!).load(data.standee_details?.get(0)?.images?.get(0))
                            .into(ivProduct)
                    } else {
                        Glide.with(context!!).load(data.tshirt_details?.get(0)?.images?.get(0))
                            .into(ivProduct)
                        llType.visibility = View.GONE
                        llSize.visibility = View.VISIBLE
                        recyclerViewSizeSelector.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        recyclerViewSizeSelector.adapter = OrderRequestSizeAdapter(
                            data.tshirt_details?.get(0)?.sizes ?: arrayListOf()
                        )
                    }
                    tvQty.text = data.order_details?.total_units.toString()
                    tvLocation.text = data.deliver_address
                    // tvPaymentStatus.text= listItem.status
                    tvPaymentStatus.visibility = View.GONE
                    tvAccept.visibility = View.GONE

                    tvDecline.visibility = View.GONE
                    tvHold.visibility = View.VISIBLE
                    tvPending.visibility = View.GONE
                } else if (PENDING == data.status) {
                    tvOrderId.text = data.id
                    if (STANDEE == data.type) {
                        llType.visibility = View.VISIBLE
                        llSize.visibility = View.GONE
                        tvType.text = data.standee_details?.get(0)?.type
                        Glide.with(context!!).load(data.standee_details?.get(0)?.images?.get(0))
                            .into(ivProduct)
                    } else {
                        Glide.with(context!!).load(data.tshirt_details?.get(0)?.images?.get(0))
                            .into(ivProduct)
                        llType.visibility = View.GONE
                        llSize.visibility = View.VISIBLE
                        recyclerViewSizeSelector.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        recyclerViewSizeSelector.adapter = OrderRequestSizeAdapter(
                            data.tshirt_details?.get(0)?.sizes ?: arrayListOf()
                        )
                    }
                    tvQty.text = data.order_details?.total_units.toString()
                    tvLocation.text = data.deliver_address
                    // tvPaymentStatus.text= listItem.status
                    tvPaymentStatus.visibility = View.GONE
                    tvAccept.visibility = View.GONE

                    tvDecline.visibility = View.GONE
                    tvHold.visibility = View.GONE
                    tvPending.visibility = View.VISIBLE
                }

                if (imgDropDown.tag == null) {
                    imgDropDown.tag = false
                    llTrack.visibility = View.GONE
                } else if (imgDropDown.tag == true) {
                    llTrack.visibility = View.VISIBLE
                }

                if (data.order_status != null) {

                    when (data.order_status) {

                        ACCEPT -> {
                            view1.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgAccepted.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )
                        }
                        PRINTED -> {

                            view1.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgAccepted.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )

                            view2.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgPrinted.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )
                        }
                        DISPATCHED -> {
                            view1.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgAccepted.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )

                            view2.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgPrinted.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )

                            view3.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgDispatched.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )
                        }
                        DELIVERED -> {
                            view1.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgAccepted.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )

                            view2.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgPrinted.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )

                            view3.setBackgroundColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.green_color
                                )
                            )
                            imgDispatched.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )

                            imgDelivered.backgroundTintList =
                                AppCompatResources.getColorStateList(
                                    context!!,
                                    R.color.green_color
                                )
                        }
                    }

                }

                imgDropDown.setOnClickListener {
                    if (imgDropDown.tag == true) {
                        imgDropDown.tag = false
                        llTrack.visibility = View.GONE
                    } else {
                        imgDropDown.tag = true
                        llTrack.visibility = View.VISIBLE
                    }
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            ItemOrderStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        var data: ListItem = listItem[holder.absoluteAdapterPosition]
        if ("Pending".equals(data.status)) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        } else {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        holder.bind(data)

        holder.binding.tvDetails.setOnClickListener {
            onItemClickListener?.onDetailsClick(listItem[holder.absoluteAdapterPosition])
            // Toast.makeText(context,"",Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    interface OnItemClickListener {
        fun onDetailsClick(data: ListItem)
    }

}
