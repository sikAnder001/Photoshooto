package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.databinding.ItemOrderRequestBinding
import com.photoshooto.domain.model.ListItem
import com.photoshooto.util.ACCEPT
import com.photoshooto.util.DECLINE
import com.photoshooto.util.PENDING
import com.photoshooto.util.STANDEE


class OrdersRequestAdapter(var listItem: List<ListItem>) :
    RecyclerView.Adapter<OrdersRequestAdapter.ViewPagerHolder>() {

    companion object {
        var context: Context? = null
    }

    var onItemClickListener: OnItemClickListener? = null

    class ViewPagerHolder(var binding: ItemOrderRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListItem) {
            binding.apply {
                tvOrderId.text = data.id
                if (PENDING.equals(data.status, ignoreCase = true)) {
                    if (STANDEE.equals(data.type)) {
                        llType.visibility = View.VISIBLE
                        llSize.visibility = View.GONE
                        tvType.text = data.standee_details?.get(0)?.type
                        if (data.standee_details?.get(0)?.images?.get(0) != null) {
                            Glide.with(context!!).load(data.standee_details.get(0)?.images?.get(0))
                                .into(ivProduct)
                        }
                    } else {
                        if (data.tshirt_details?.get(0)?.images?.get(0) != null) {
                            Glide.with(context!!).load(data.tshirt_details.get(0)?.images?.get(0))
                                .into(ivProduct)
                        }
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
                    llRequest.visibility = View.VISIBLE
                    llStatus.visibility = View.GONE
                    llTrack.visibility = View.GONE
                } else if (ACCEPT.equals(data.status, ignoreCase = true)) {
                    tvOrderId.text = data.id
                    if (STANDEE.equals(data.type)) {
                        llType.visibility = View.VISIBLE
                        llSize.visibility = View.GONE
                        tvType.text = data.standee_details?.get(0)?.type
                        if (data.standee_details?.get(0)?.images?.get(0) != null) {
                            Glide.with(context!!).load(data.standee_details.get(0)?.images?.get(0))
                                .into(ivProduct)
                        }

                    } else {
                        val strT_img = data.tshirt_details?.get(0)?.images?.get(0)
                        if (strT_img != null) {
                            try {
                                Glide.with(context!!).load(strT_img).into(ivProduct)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        llType.visibility = View.GONE
                        llSize.visibility = View.VISIBLE
                        recyclerViewSizeSelector.layoutManager = LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false
                        )
                        recyclerViewSizeSelector.adapter = OrderRequestSizeAdapter(
                            data.tshirt_details?.get(0)?.sizes ?: arrayListOf()
                        )
                    }
                    tvQty.text = data.order_details?.total_units.toString()
                    tvLocation.text = data.deliver_address
                    // tvPaymentStatus.text= listItem.status
                    tvPaymentStatus.visibility = View.VISIBLE
                    tvAcceptStatus.visibility = View.VISIBLE
                    tvDeclineStatus.visibility = View.GONE

                    llRequest.visibility = View.GONE
                    llStatus.visibility = View.VISIBLE
                    llTrack.visibility = View.VISIBLE
                } else if (DECLINE.equals(data.status, ignoreCase = true)) {
                    tvOrderId.text = data.id
                    if (STANDEE.equals(data.type)) {
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
                        recyclerViewSizeSelector.layoutManager = LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false
                        )
                        recyclerViewSizeSelector.adapter = OrderRequestSizeAdapter(
                            data.tshirt_details?.get(0)?.sizes ?: arrayListOf()
                        )
                    }
                    tvQty.text = data.order_details?.total_units.toString()
                    tvLocation.text = data.deliver_address
                    // tvPaymentStatus.text= listItem.status
                    tvPaymentStatus.visibility = View.GONE
                    tvAcceptStatus.visibility = View.GONE
                    tvDeclineStatus.visibility = View.VISIBLE

                    llRequest.visibility = View.GONE
                    llTrack.visibility = View.GONE
                    llStatus.visibility = View.VISIBLE
                }


            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            ItemOrderRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        var data: ListItem = listItem[holder.absoluteAdapterPosition]
        /*if (!"Pending".equals(data.status)){
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }else{
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }*/
        holder.bind(data)
        holder.binding.tvDetails.setOnClickListener {
            onItemClickListener?.onDetailsClick(data)
            // Toast.makeText(context,"",Toast.LENGTH_SHORT).show()
        }

        holder.binding.tvAccept.setOnClickListener {
            onItemClickListener?.onAcceptClick(data)
            // Toast.makeText(context,"",Toast.LENGTH_SHORT).show()
        }
        holder.binding.tvDecline.setOnClickListener {
            onItemClickListener?.onDeclineClick(data)
            // Toast.makeText(context,"",Toast.LENGTH_SHORT).show()
        }
        holder.binding.tvHold.setOnClickListener {
            onItemClickListener?.onHoldClick(data)
            // Toast.makeText(context,"",Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    interface OnItemClickListener {
        fun onDetailsClick(data: ListItem)
        fun onAcceptClick(data: ListItem)
        fun onDeclineClick(data: ListItem)
        fun onHoldClick(data: ListItem)
    }

}
