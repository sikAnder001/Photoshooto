package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ItemEventOrderListBinding
import com.photoshooto.domain.model.EventOrderHistoryElement


class QREventOrderPagerAdapter(var eventList: List<EventOrderHistoryElement>) :
    RecyclerView.Adapter<QREventOrderPagerAdapter.ViewPagerHolder>(), View.OnClickListener {

    companion object {
        var context: Context? = null
    }

    class ViewPagerHolder(var binding: ItemEventOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EventOrderHistoryElement) {
            binding.apply {

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            ItemEventOrderListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(eventList[holder.position])
        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnClickListener {
            //it.findNavController().navigate(QREventOrderHistoryFragmentDirections.actionFragmentEventItemToFragmentEventDetails())
            //Navigation.createNavigateOnClickListener(R.id.action_FragmentEventItem_to_FragmentEventDetails)
        }

        holder.binding.apply {
            val listItem: EventOrderHistoryElement = eventList.get(position)
            tvEventName.text = listItem.event_name
            tvOrderId.text = listItem.user_id
            tvStandeeType.text = listItem.standee_type
            tvLocation.text = listItem.location
            // tvPaymentStatus.text= listItem.status
            tvDate.text = listItem.event_start_date
            //tvTime.text= listItem.updated_at
        }

        holder.binding.cardEventItem.setOnClickListener(View.OnClickListener {
            holder.binding.apply {
                it.findNavController().popBackStack()
                //it.findNavController().navigate(QRCodeEventOrderHistoryFragmentDirections.actionFragmentQRcodeToFragmentEventTab())
            }
        })

    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            (R.id.card_event_item) -> {

            }
        }
    }
}