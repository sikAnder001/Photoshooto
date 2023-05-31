package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.photoshooto.R
import com.photoshooto.domain.model.ProductsModel
import com.photoshooto.ui.photographer.adapter.*
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val SECOND = 1
private const val MINUTE = 60 * SECOND
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR
private const val MONTH = 30 * DAY
private const val YEAR = 12 * MONTH

class UsedProductsListAdapter(private val onClick: (ProductsModel) -> Unit) :
    RecyclerView.Adapter<UsedProductsListAdapter.UsedProductsListViewHolder>() {

    private var items: List<ProductsModel> = emptyList()
    private var itemsFiltered: List<ProductsModel> = ArrayList<ProductsModel>()

    private var isGridView = false
    private var isRemove = false

    private lateinit var productsImageListAdapter: ProductsImageListAdapter

    fun swapList(items: List<ProductsModel>?, isGridView: Boolean, isRemove: Boolean = false) {
        if (items != null) {
            this.items = items
            this.itemsFiltered = items
        }
        this.isGridView = isGridView
        this.isRemove = isRemove

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsedProductsListViewHolder {
        if (isGridView) {
            return UsedProductsListViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.used_products_grid_item, parent, false)
            )
        } else {
            return UsedProductsListViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.used_products_list_item, parent, false)
            )
        }

    }

    override fun getItemCount() = itemsFiltered.size

    override fun onBindViewHolder(holderView: UsedProductsListViewHolder, position: Int) {
        val item = itemsFiltered[position]
        holderView.bind(item, position)
    }

    inner class UsedProductsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val vpImage: ViewPager2
        private val indicator: DotsIndicator
        private val tvSoldOut: TextView
        private val tvBrand: TextView
        private val tvName: TextView
        private val tvLocation: TextView
        private val tvPrice: TextView
        private val tvTime: TextView
        private val tvCondition: TextView
        private val btnBuy: Button
        private val imgVerified: ImageView


        @SuppressLint("NewApi")
        fun bind(productsModel: ProductsModel, position: Int) {

            productsImageListAdapter = ProductsImageListAdapter(onClick = {

            })

            vpImage.adapter = productsImageListAdapter

            tvCondition.text = productsModel.condition
            tvBrand.text = productsModel.brand
            tvName.text = productsModel.name
            tvLocation.text = productsModel.sellingLocation
            tvPrice.text = "₹${productsModel.price}"
            if (productsModel.purchaseDate.length != 0) {
                tvTime.text = getDateDiff(productsModel.purchaseDate)
            }


            /*if (position == 4){
                vpImage.alpha = 0.5F
                tvSoldOut.isVisible = true
                btnBuy.isEnabled = false
            }else{
                vpImage.alpha = 1F
                tvSoldOut.isVisible = false
                btnBuy.isEnabled = true
            }*/

            productsImageListAdapter.swapList(productsModel.images)

            if (productsModel.images.size == 1) {
                indicator.isVisible = false
            } else {
                indicator.isVisible = true
            }

            imgVerified.isVisible = productsModel.status.equals("approved")

            indicator.attachTo(vpImage)

            if (isRemove) {
                btnBuy.text = itemView.context.getString(R.string.remove)
            } else {
                btnBuy.text = itemView.context.getString(R.string.buy_now)
            }

            itemView.setOnClickListener {
                onClick(productsModel)
            }

            btnBuy.setOnClickListener {
                onClick(productsModel)
            }
        }

        init {
            vpImage = itemView.findViewById(R.id.vpImage)
            indicator = itemView.findViewById(R.id.indicator)
            tvSoldOut = itemView.findViewById(R.id.tvSoldOut)
            tvBrand = itemView.findViewById(R.id.tvBrand)
            tvName = itemView.findViewById(R.id.tvName)
            tvLocation = itemView.findViewById(R.id.tvLocation)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            tvTime = itemView.findViewById(R.id.tvTime)
            tvCondition = itemView.findViewById(R.id.tvCondition)
            btnBuy = itemView.findViewById(R.id.btnBuy)
            imgVerified = itemView.findViewById(R.id.imgVerified)

        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    fun getDateDiff(data: String): String {
        val format = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = data
        val registerDate = uTCToLocal(format, format, date)
        val userDob: Date = dateFormat.parse(registerDate)

        val today = Date()
        val diff: Long = (today.time - userDob.time) / 1000
        return when {
            diff < 60 * MINUTE -> "${diff / MINUTE} Minutes"
            diff < 24 * HOUR -> "${diff / HOUR} Hours"
            diff < 30 * DAY -> "${diff / DAY} Days"
            diff < 12 * MONTH -> "${diff / MONTH} Months"
            diff < 2 * YEAR -> "${diff / YEAR} Years"
            else -> "${diff / YEAR} Years"
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun uTCToLocal(
        dateFormatInPut: String?, dateFomratOutPut: String?, datesToConvert: String?
    ): String? {
        var dateToReturn = datesToConvert
        val sdf = android.icu.text.SimpleDateFormat(dateFormatInPut)
        sdf.timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
        var gmt: Date? = null
        val sdfOutPutToSend = android.icu.text.SimpleDateFormat(dateFomratOutPut)
        sdfOutPutToSend.timeZone = android.icu.util.TimeZone.getDefault()
        try {
            gmt = sdf.parse(datesToConvert)
            dateToReturn = sdfOutPutToSend.format(gmt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateToReturn
    }


}