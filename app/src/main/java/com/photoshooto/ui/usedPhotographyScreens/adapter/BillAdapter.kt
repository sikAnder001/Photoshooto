package com.photoshooto.ui.usedPhotographyScreens.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.domain.model.FileImagePRQ

class BillAdapter(
    private val onClose: (FileImagePRQ) -> Unit,
    private val onView: (FileImagePRQ) -> Unit
) :
    RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    private var item: List<FileImagePRQ> = emptyList()

    fun swapList(items: ArrayList<FileImagePRQ>) {
        this.item = items


        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        return BillViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.post_product_item, parent, false)
        )
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holderView: BillViewHolder, position: Int) {
        val item = item[position]
        if (item != null) {
            holderView.bind(item, position)
        }
    }

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView
        private val imgClose: ImageView

        fun bind(file: FileImagePRQ, position: Int) = with(itemView) {

            tvTitle.text = "Bill_${position + 1}"

            imgClose.setOnClickListener {

                onClose(file)
            }
            itemView.setOnClickListener {

                onView(file)
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            imgClose = itemView.findViewById(R.id.imgClose)

        }
    }
}