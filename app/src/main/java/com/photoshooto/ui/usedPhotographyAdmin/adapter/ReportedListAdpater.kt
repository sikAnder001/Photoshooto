package com.photoshooto.ui.usedPhotographyAdmin.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.ReportedProductItemBinding
import com.photoshooto.domain.model.reportedListModel.ReportedList
import com.photoshooto.util.urlAddingForPicture
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ReportedListAdpater(
    var context: Context?,
    val selectedDeleteInterface:  ReportedListAdpater.SelectedDeleteInterface,
    val viewDetailsInterface:  ReportedListAdpater.ViewDetailsInterface,
    var removeInterface:  ReportedListAdpater.RemoveInterface


) : RecyclerView.Adapter< ReportedListAdpater.ViewHolder>() {

    var data=ArrayList<ReportedList>()

    inner class ViewHolder(reportedListItemBinding: ReportedProductItemBinding) :
        RecyclerView.ViewHolder(reportedListItemBinding.root) {

        var reportedListItemBinding = reportedListItemBinding
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: com.photoshooto.domain.model.reportedListModel.ReportedList, position: Int) {

            with(reportedListItemBinding) {

                try {
                    Glide.with(context!!)
                        .load(urlAddingForPicture(data.productDetails!!.images[0]))
                        .error(R.drawable.ic_profile_placeholder).into(imgProducts)
                }
                catch (e: Exception){

                }


                prName.text = data.productDetails!!.brand
                modelName.text = data.productDetails!!.name
                tvGood.text = data.productDetails!!.condition.toString().split(",").get(0)
                tvYear.text = "${data.userProfile!!.experience} years".toString()
                reportCount.text="${data.reportCount} Reported".toString()


                try {
                    tvLoc.text = data.productDetails!!.sellingLocation.toString().split(",").get(2)
                }
                catch (e: Exception){

                }


                price.text = "₹${data.productDetails!!.price}".toString()
                tvdate.text=data.productDetails!!.purchaseDate
                tvPid.text=data.id

                try{
                    val odt = OffsetDateTime.parse(data.productDetails!!.createdAt)
                    val dtf = DateTimeFormatter.ofPattern("MMM dd, uuuu", Locale.ENGLISH)
                    val dtfTime = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
                    System.out.println(dtf.format(odt))

                    tvtime.text = "" + dtfTime.format(odt)
                }catch (e: Exception){

                }




                btnviewDetails.setOnClickListener {
                    viewDetailsInterface.onClick("Remove", data.id.toString())
                }
                btnRemove.setOnClickListener {
                    removeInterface.onClick("remove", data.id!!)
                }

            }


        }


    }

    fun setList(data: kotlin.collections.ArrayList<ReportedList>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val reportedListItemBinding = ReportedProductItemBinding.inflate(inflater, parent, false)
        return ViewHolder(reportedListItemBinding)


    }

    override fun getItemCount(): Int {
        return try {
            data.size
        } catch (e: Exception) {
            0
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ReportedListAdpater.ViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
    interface SelectedDeleteInterface {
        fun onClick(data: String)

    }

    interface ViewDetailsInterface {
        fun onClick(data: String, id: String)

    }


    interface RemoveInterface {
        fun onClick(data: String, id: String)

    }

}