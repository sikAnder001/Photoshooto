package com.photoshooto.bottomsheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R

class SingleSelectionCheckAdapter(
    var employees: List<String>,
    var itemSelectionListener: ItemSelectionListener,
    var mContext: Context
) :
    RecyclerView.Adapter<SingleSelectionCheckAdapter.SingleViewHolder>() {
    private var checkedPosition = -1

    interface ItemSelectionListener {
        fun onItemClick(rideList: String, position: Int)
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view: View =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_plans_selection, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(employees[position])
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    inner class SingleViewHolder(@NonNull itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val serviceName: TextView
        private val layout: ConstraintLayout
        private val checkbox: CheckBox

        fun bind(employee: String) {
            if (employee != null) {
                serviceName.text = employee
                if (checkedPosition == -1) {
                    checkbox.setChecked(false)
                    println("praveen checkedPosition   if (checkedPosition == -1) { " + checkedPosition)
//                    layout.setSelected(true)
//                    layout.setBackgroundColor(mContext.resources.getColor(R.color.light_blue))
                } else {
//                    layout.setSelected(false)
                    if (checkedPosition == adapterPosition) {
                        checkbox.setChecked(true)

                        println("praveen checkedPosition   == adapterPosition  " + checkedPosition)
//                        layout.setBackgroundColor(mContext.resources.getColor(R.color.lightgray))
                        layout.setBackgroundColor(mContext.resources.getColor(R.color.light_blue))

                    } else {
                        layout.setBackgroundColor(mContext.resources.getColor(R.color.lightgray))
                        println("praveen checkedPosition   == adapterPosition else   " + checkedPosition)
//                        layout.setBackgroundColor(mContext.resources.getColor(R.color.light_blue))
                        checkbox.setChecked(false)
                    }
                }
                itemView.setOnClickListener {
                    itemSelectionListener.onItemClick(employee, position)
                    if (checkedPosition != adapterPosition) {
                        notifyItemChanged(checkedPosition)
                        checkedPosition = adapterPosition
                    }
                }

            }

        }

        init {
            serviceName = itemView.findViewById(R.id.service_name)
            layout = itemView.findViewById(R.id.layout)
            checkbox = itemView.findViewById(R.id.checkbox)
        }
    }


}