package com.photoshooto.ui.job.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.trimmedLength
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.EventCalendarRowBinding
import com.photoshooto.domain.model.CalendarEvent
import com.photoshooto.ui.job.utility.getFormattedDatetime
import com.photoshooto.ui.job.utility.setSafeOnClickListener

class CalendarEventAdapter(
    val onItemClick: (data: CalendarEvent) -> Unit
) : ListAdapter<CalendarEvent, CalendarEventAdapter.RecyclerHolder>(object :
    ItemCallback<CalendarEvent>() {
    override fun areItemsTheSame(
        oldItem: CalendarEvent, newItem: CalendarEvent
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CalendarEvent, newItem: CalendarEvent
    ): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(
            EventCalendarRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecyclerHolder(val binding: EventCalendarRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(data: CalendarEvent) {

            var stDate = data.startDate.getFormattedDatetime(
                outputFormat = "dd MMM yy",
                inputFormat = "MM/dd/yyy",
                withZoneCalculation = false
            ).lowercase()
            stDate = StringBuilder(stDate).also { it.setCharAt(stDate.trimmedLength() - 3, '\'') }
                .toString()

            var enDate = data.endDate.getFormattedDatetime(
                outputFormat = "dd MMMyy",
                inputFormat = "MM/dd/yyy",
                withZoneCalculation = false
            ).lowercase()
            enDate = enDate.replaceRange(
                enDate.trimmedLength() - 2, enDate.trimmedLength(), "'" +
                        "${enDate.substring(enDate.trimmedLength() - 2, enDate.trimmedLength())}"
            )

            binding.rowDate.text = "${stDate} - ${enDate}"
            binding.rowTime.text = "${data.startTime} - ${data.endTime}"
            binding.rowLocation.text =
                "Location:- " + data.city.toString().replace("[", "").replace("]", "")

            if (data.evType == "Booked") {
                binding.viewEvent.backgroundTintList =
                    binding.viewEvent.context.getColorStateList(R.color.orange_clr)
            } else {
                binding.viewEvent.backgroundTintList =
                    binding.viewEvent.context.getColorStateList(R.color.green_color)
            }

            binding.viewEvent.setSafeOnClickListener {
                onItemClick.invoke(data)
            }
        }
    }
}

