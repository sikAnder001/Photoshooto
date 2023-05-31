package com.photoshooto.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.photoshooto.R
import de.hdodenhof.circleimageview.CircleImageView


@BindingAdapter("image")
fun loadImage(view: ImageView, url: String?) {
    Glide.with(view)
        .load(url)
        .error(R.drawable.splas_logo_ps)
        .load(url).into(view)
}

@BindingAdapter("circleImage")
fun loadCircleImage(view: CircleImageView, url: String?) {
    Glide.with(view).load(url).into(view)
}