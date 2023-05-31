package com.photoshooto.ui.general
//
//import android.content.Context
//import android.media.Image
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.CheckBox
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.annotation.NonNull
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.vrinda.growero.R
//import com.vrinda.growero.databinding.ItemServiceBinding
//import com.vrinda.growero.domain.model.Typeofservice
//
//
//class SingleSelectionAdapter(var employees: List<Typeofservice>, var itemSelectionListener: ItemSelectionListener,  var mContext: Context) :
//    RecyclerView.Adapter<SingleSelectionAdapter.SingleViewHolder>() {
//    private var checkedPosition = -1
//
//    interface ItemSelectionListener {
//        fun onItemClick( rideList: Typeofservice,position: Int)
//    }
//
//    @NonNull
//    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): SingleViewHolder {
//        val view: View =
//            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_service, viewGroup, false)
//        return SingleViewHolder(view)
//    }
//
//    override fun onBindViewHolder(@NonNull singleViewHolder: SingleViewHolder, position: Int) {
//        singleViewHolder.bind(employees[position])
//    }
//
//    override fun getItemCount(): Int {
//        return employees.size
//    }
//
//    inner class SingleViewHolder(@NonNull itemView: View) :
//        RecyclerView.ViewHolder(itemView) {
//        private val serviceName: TextView
//        private val serviceImg: ImageView
//        private val layout: ConstraintLayout
//        private val checkbox: CheckBox
//
//        fun bind(employee: Typeofservice) {
//
//            if (employee.servicesname != null) {
//                serviceName.text = employee.servicesname
//
//                if (employee.servicesicon!=null){
//                    Glide.with(mContext)
//                        .load("https://apps.vrindasoft.com/groweroapps/public/uploads/"+employee.servicesicon)
//                        .placeholder(R.drawable.qr_code)
//                        .error(R.drawable.ic_info)
//                        .into(serviceImg);
//                }
//
//                if (checkedPosition == -1) {
//                    checkbox.setChecked(false)
//                } else {
//                    if (checkedPosition == adapterPosition) {
//                        checkbox.setChecked(true)
//                    } else {
//                        checkbox.setChecked(false)
//                    }
//                }
//                itemView.setOnClickListener {
//                    itemSelectionListener.onItemClick( employee,position)
//                    if (checkedPosition != adapterPosition) {
//                        notifyItemChanged(checkedPosition)
//                        checkedPosition = adapterPosition
//                    }
//                }
//
//            }
//
//        }
//
//        init {
//            serviceName = itemView.findViewById(R.id.service_name)
//            serviceImg = itemView.findViewById(R.id.service_img)
//            layout = itemView.findViewById(R.id.layout)
//            checkbox = itemView.findViewById(R.id.checkbox)
//        }
//    }
//
//
//
//
//}