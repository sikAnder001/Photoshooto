package com.photoshooto.ui.intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.ItemIntroSlideBinding

class IntroSliderAdapter(private val introSlides: List<IntroSlide>) :
    RecyclerView.Adapter<IntroSliderAdapter.IntoSlideViewHolder>() {

    inner class IntoSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemIntroSlideBinding.bind(view)
        var textTitle = binding.tvTitle
        var textDesc = binding.tvDescription
        var imageIcon = binding.imgSlideIcon
        fun bind(introSlide: IntroSlide) {
            textTitle.text = introSlide.title
            textDesc.text = introSlide.description
            imageIcon.setImageResource(introSlide.icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntoSlideViewHolder {
        return IntoSlideViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_intro_slide,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

    override fun onBindViewHolder(holder: IntoSlideViewHolder, position: Int) {
        holder.bind(introSlides[position])
    }


}