package com.photoshooto.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.model.QuickBeans

class QREventsAdapter(
    var arrayQREvent: ArrayList<QuickBeans>
) : RecyclerView.Adapter<QREventsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_qrcode_recycler, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}