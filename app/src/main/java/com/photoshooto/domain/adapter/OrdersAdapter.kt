package com.photoshooto.domain.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.ItemOrderBinding
import com.photoshooto.domain.model.ListItem
import com.photoshooto.util.STANDEE
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class OrdersAdapter(var mContext: Context, var listItem: List<ListItem>) :
    RecyclerView.Adapter<OrdersAdapter.ViewPagerHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    inner class ViewPagerHolder(var binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(data: ListItem) {
            binding.apply {
                when (data.status) {
                    "accept" -> {
                        ivStatus.setImageResource(R.drawable.ic_accepted)
                    }
                    "decline" -> {
                        ivStatus.setImageResource(R.drawable.ic_rejected)
                    }
                    else -> {
                        ivStatus.setImageResource(R.drawable.ic_pending)
                    }
                }

                tvName.text = data.user_profile?.name
                tvOrderId.text = data.id
                if (STANDEE == data.type) {
                    llType.visibility = View.VISIBLE
                    tvType.text = data.standee_details?.get(0)?.type
                    Glide.with(mContext).load(data.standee_details?.get(0)?.images?.get(0))
                        .into(ivProduct)
                } else {
                    llType.visibility = View.GONE
                    Glide.with(mContext).load(data.tshirt_details?.get(0)?.images?.get(0))
                        .into(ivProduct)
                }
                tvQty.text = data.order_details?.total_units.toString()
                tvTransactionId.text = data.user_id
                tvLocation.text = data.user_profile?.address
                // tvPaymentStatus.text= listItem.status

                val odt = OffsetDateTime.parse(data.updated_at);
                val dtf = DateTimeFormatter.ofPattern("MMM dd, uuuu", Locale.ENGLISH)
                val dtfTime = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
                System.out.println(dtf.format(odt));

                tvDate.text = "" + dtf.format(odt)
                tvTime.text = "" + dtfTime.format(odt)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        return ViewPagerHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(listItem[holder.absoluteAdapterPosition])

        holder.binding.llProduct.setOnClickListener {
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
