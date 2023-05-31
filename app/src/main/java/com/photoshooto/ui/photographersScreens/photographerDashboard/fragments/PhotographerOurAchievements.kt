package com.photoshooto.ui.photographersScreens.photographerDashboard.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.model.QuickBeans

class PhotographerOurAchievements(
    var arrayAchive: ArrayList<QuickBeans>
) : RecyclerView.Adapter<PhotographerOurAchievements.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_our_achievements, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}