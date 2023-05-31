package com.photoshooto.ui.usedPhotographyAdmin.adapter

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
import com.photoshooto.databinding.ProductListedItemBinding
import com.photoshooto.domain.model.get_productlist_model.List
import com.photoshooto.util.urlAddingForPicture
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ProductListAdapter(
    var context: Context?,
    val viewDetailsInterface: ViewDetailsInterface,
    var approveInterface: ApproveInterface,
    var declineInterface: DeclineInterface
) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {


    var data = ArrayList<List>()

    inner class ViewHolder(productListItemBinding: ProductListedItemBinding) :
        RecyclerView.ViewHolder(productListItemBinding.root) {

        var productListItemBinding = productListItemBinding

        @SuppressLint("SuspiciousIndentation")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: List, position: Int) {

            with(productListItemBinding) {

                try{
                    Glide.with(context!!)
                        .load(urlAddingForPicture(data.images[0]))
                        .error(R.drawable.ic_profile_placeholder).into(imgProducts)
                }catch (e: Exception){

                }




                prName.text = data.brand
                modelName.text = data.name
                tvGood.text = data.condition.toString().split(",").get(0)
                tvYear.text = "${data.userProfile!!.experience} years".toString()


                try {
                    tvLoc.text = data.sellingLocation.toString().split(",").get(2)
                } catch (e: Exception) {

                }





                price.text = "₹${data.price}".toString()

                tvdate.text=data.purchaseDate
                tvPid.text=data.id

                try{
                    val odt = OffsetDateTime.parse(data.createdAt)
                    val dtf = DateTimeFormatter.ofPattern("MMM dd, uuuu", Locale.ENGLISH)
                    val dtfTime = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
                    System.out.println(dtf.format(odt))

                    tvtime.text = "" + dtfTime.format(odt)
                }catch (e: Exception) {

                }





                if (data.status == "approved") {
                    llBtn.visibility = View.INVISIBLE
                    // mainCard.setBackgroundColor(ContextCompat.getColor(context!!, R.color.card_gray))

                    btnviewDetails.visibility = View.VISIBLE
                    tvApproved.visibility = View.VISIBLE
                    tvDecline.visibility = View.GONE
                    btnviewDetailsProduct.visibility=View.GONE
                    /*statusTv.text = "Approved"
                    statusTv.setTextColor(ContextCompat.getColor(context!!, R.color.green_status))
                    statusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.tick_green_hollow,
                        0,
                        0,
                        0
                    )*/
                } else if (data.status == "rejected") {
                    llBtn.visibility = View.INVISIBLE
                    // mainCard.setBackgroundColor(ContextCompat.getColor(context!!, R.color.card_gray))
                    btnviewDetails.visibility = View.VISIBLE
                    tvDecline.visibility = View.VISIBLE
                    tvApproved.visibility = View.GONE
                    btnviewDetailsProduct.visibility=View.GONE

                    /*statusTv.text = "Declined"
                    statusTv.setTextColor(ContextCompat.getColor(context!!, R.color.orange_clr))
                    statusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.cross_orage_hollow,
                        0,
                        0,
                        0
                    )*/
                } else {

                    llBtn.visibility = View.VISIBLE
                    btnviewDetails.visibility = View.GONE
                    tvApproved.visibility = View.GONE
                    tvDecline.visibility = View.GONE
                    btnviewDetailsProduct.visibility=View.VISIBLE
                }




                btnviewDetails.setOnClickListener {
                    viewDetailsInterface.onClick("Approve", data.id!!)
                }
                btnviewDetailsProduct.setOnClickListener {
                    viewDetailsInterface.onClick("Approve", data.id!!)
                }

                btnApproval.setOnClickListener {
                    approveInterface.onClick("approve", data.id!!)
                }

                btnDecline.setOnClickListener {
                    declineInterface.onClick("decline", data.id!!)
                }


            }


        }


    }

    fun setList(data: ArrayList<List>) {
        this.data = data
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val productListItemBinding = ProductListedItemBinding.inflate(inflater, parent, false)
        return ViewHolder(productListItemBinding)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ProductListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position], position)

    }

    override fun getItemCount(): Int {

        return try {
            data.size
        } catch (e: Exception) {
            0
        }

    }

    interface ViewDetailsInterface2 {
        fun onClick(data: String, id: String)

    }


    interface ViewDetailsInterface {
        fun onClick(data: String, id: String)

    }

    interface ApproveInterface {

        fun onClick(data: String, id: String)

    }

    interface DeclineInterface {
        fun onClick(data: String, id: String)

    }


}