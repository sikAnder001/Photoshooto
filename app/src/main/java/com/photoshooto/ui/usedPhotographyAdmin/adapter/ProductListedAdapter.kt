package com.photoshooto.ui.usedPhotographyAdmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.domain.model.TempData

class ProductListedAdapter(
    private val onTab: (TempData) -> Unit,
    private val onDecline: (TempData) -> Unit,
    private val onRemove: (TempData) -> Unit
) :
    RecyclerView.Adapter<ProductListedAdapter.ProductListedViewHolder>() {

    private var items: List<TempData> = emptyList()
    private var isReported = false

    fun swapList(items: ArrayList<TempData>, isReported: Boolean = false) {
        this.items = items
        this.isReported = isReported

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListedViewHolder {
        if (isReported) {
            return ProductListedViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.reported_product_item, parent, false)
            )
        } else {
            return ProductListedViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.product_listed_item, parent, false)
            )
        }

    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holderView: ProductListedViewHolder, position: Int) {
        val item = items[position]
        holderView.bind(item, position)
    }

    inner class ProductListedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val btnDecline: Button
        private val btnRemove: Button

        fun bind(tempData: TempData, position: Int) {

            itemView.setOnClickListener {

                onTab(tempData)

            }

            btnDecline.setOnClickListener {
                onDecline(tempData)
            }

            btnRemove.setOnClickListener {

                onRemove(tempData)
            }

        }

        init {
            btnDecline = itemView.findViewById(R.id.btnDecline)
            btnRemove = itemView.findViewById(R.id.btnRemove)
        }
    }

}