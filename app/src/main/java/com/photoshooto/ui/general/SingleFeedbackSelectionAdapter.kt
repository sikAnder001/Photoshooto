package com.photoshooto.ui.general

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.domain.model.Servicerequest
import java.text.SimpleDateFormat

class SingleFeedbackSelectionAdapter(
    var employees: List<Servicerequest>,
    var itemSelectionListener: ItemSelectionListener,
    var mContext: Context
) :
    RecyclerView.Adapter<SingleFeedbackSelectionAdapter.SingleViewHolder>() {
    private var checkedPosition = -1

    interface ItemSelectionListener {
        fun onItemClick(list: Servicerequest, position: Int)
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_service, viewGroup, false)
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
        private val bookeddate: TextView
        private val serviceImg: ImageView
        private val layout: ConstraintLayout
        private val checkbox: CheckBox

        fun bind(employee: Servicerequest) {

            val simpledateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm aa")
            val date = outputDateFormat.format(simpledateformat.parse(employee.dateofBooking))
            serviceImg.visibility = View.GONE
            serviceName.text = employee.typesofServices
            bookeddate.text = " Booked On  " + date

            if (checkedPosition == -1) {
                checkbox.setChecked(false)
            } else {
                if (checkedPosition == adapterPosition) {
                    checkbox.setChecked(true)
                } else {
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

        init {
            bookeddate = itemView.findViewById(R.id.bookeddate)
            serviceName = itemView.findViewById(R.id.service_name)
            serviceImg = itemView.findViewById(R.id.service_img)
            layout = itemView.findViewById(R.id.layout)
            checkbox = itemView.findViewById(R.id.checkbox)
        }
    }


}