package com.photoshooto.ui.general
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.vrinda.growero.R
//import com.vrinda.growero.databinding.ItemServiceBinding
//import com.vrinda.growero.domain.model.Typeofservice
//import com.vrinda.growero.util.setSingleClickListener
//
//class TypeServicesAdapter(
//    var mList: List<Typeofservice>,
//    var mItemClickListener: OnItemClickListener,  var mContext: Context
//) : RecyclerView.Adapter<TypeServicesAdapter.PlanViewHolder>() {
//
//    interface OnItemClickListener {
//        fun onItemSelected(list: Typeofservice, position: Int)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
//        val mBinding: ItemServiceBinding =
//            ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return PlanViewHolder(mBinding)
//    }
//
//    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
//        holder.bind(mList[position], position)
//    }
//
//    override fun getItemCount(): Int {
//        return mList.size
//    }
//
//    var lastSelectedPossion: Int = 0
//
//    inner class PlanViewHolder(var itemBinding: ItemServiceBinding) :
//        RecyclerView.ViewHolder(itemBinding.root) {
//        fun bind(service: Typeofservice, position: Int) {
//
//            if (service.servicesname != null) {
//
//                itemBinding.serviceName.text = service.servicesname
//
//                if (service.servicesicon!=null){
//                    Glide.with(mContext)
//                    .load("https://apps.vrindasoft.com/groweroapps/public/uploads/"+service.servicesicon)
//                        .placeholder(R.drawable.qr_code)
//                        .error(R.drawable.ic_info)
//                        .into(itemBinding!!.serviceImg);
//                }
//
//                itemBinding.layout.setSingleClickListener {
//                    lastSelectedPossion = adapterPosition
//                    service.isSelected = true
//                    mItemClickListener.onItemSelected(service, position)
//                }
//
//                if (position == lastSelectedPossion) {
//                    itemBinding.checkbox.setChecked(true)
//                } else {
//                    itemBinding.checkbox.setChecked(false)
//                }
//
//            } else {
//                itemBinding.layout.visibility = View.GONE
//            }
//
//        }
//
//    }
//
//}
