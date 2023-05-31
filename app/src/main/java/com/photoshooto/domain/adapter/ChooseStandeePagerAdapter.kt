package com.photoshooto.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.AdapterViewpagerChooseStandeeBinding

class ChooseStandeePagerAdapter(var dataList: List<String>) :
    RecyclerView.Adapter<ChooseStandeePagerAdapter.ViewPagerHolder>() {
    companion object {
        var context: Context? = null
    }

    inner class ViewPagerHolder(var binding: AdapterViewpagerChooseStandeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(standee: String) {
            binding.apply {
                context?.apply {
                    Glide.with(this)
                        .load(standee)
                        .into(ivStandee)

                    ivStandee.shapeAppearanceModel = ivStandee.shapeAppearanceModel
                        .toBuilder()
                        .setAllCornerSizes(20f)
                        .build()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        context = parent.context
        return ViewPagerHolder(
            AdapterViewpagerChooseStandeeBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(dataList[position])
        holder.binding.ivStandee.setOnClickListener {
            val factory = LayoutInflater.from(context)
            val customLayout: View = factory.inflate(R.layout.image_enlarge_dialog, null)
            val dialog = AlertDialog.Builder(
                context!!
            ).create()
            dialog.setView(customLayout)
            val imgClose = customLayout.findViewById<ImageView>(R.id.imgClose)
            val imgStandee = customLayout.findViewById<ImageView>(R.id.imgStandee)
            Glide.with(context!!)
                .load(dataList[position])
                .into(imgStandee)
            dialog.show()
            imgClose.setOnClickListener { view1: View? -> dialog.dismiss() }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
