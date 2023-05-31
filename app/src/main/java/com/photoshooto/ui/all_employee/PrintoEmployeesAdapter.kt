package com.photoshooto.ui.all_employee

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.AdapterAllEmployeeBinding
import com.photoshooto.domain.model.get_productlist_model.List
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.model.AllEmployeesBeans
import com.photoshooto.util.urlAddingForPicture
import java.util.ArrayList

class PrintoEmployeesAdapter (var context: Context?,
                              val viewDetailsInterface: PrintoEmployeesAdapter.ViewDetailsInterface
) : RecyclerView.Adapter<PrintoEmployeesAdapter.ViewHolder>() {


    var data = ArrayList<com.photoshooto.domain.model.all_employees_model.List>()


    inner class ViewHolder(adapterAllEmployeeBinding: AdapterAllEmployeeBinding) :
        RecyclerView.ViewHolder(adapterAllEmployeeBinding.root) {

        var adapterAllEmployeeBinding =  adapterAllEmployeeBinding

        @SuppressLint("SuspiciousIndentation")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: com.photoshooto.domain.model.all_employees_model.List, position: Int) {

            with(adapterAllEmployeeBinding) {
                try{
                    Glide.with(context!!)
                        .load(data.profileDetails?.profileImage?.let { urlAddingForPicture(it) })
                        .error(R.drawable.ic_profile_placeholder).into(ivUserProfile)
                }catch (e: Exception){

                }

                tvName.text = data.profileDetails?.name
                tvtype.text = data.type
                tvCode.text = data.username

                tvDetails.setOnClickListener {
                    viewDetailsInterface.onClick( data.id!!)
                }






            }
        }
    }

    fun setList(data: ArrayList<com.photoshooto.domain.model.all_employees_model.List>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val adapterAllEmployeeBinding= AdapterAllEmployeeBinding.inflate(inflater, parent, false)
        return ViewHolder( adapterAllEmployeeBinding)

    }

    override fun getItemCount(): Int {
        return try {
            data.size
        } catch (e: Exception) {
            0
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
        holder.bind(data[position], position)

    }

    interface ViewDetailsInterface {
        fun onClick(id: String)

    }
}