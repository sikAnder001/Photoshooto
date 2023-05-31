package com.photoshooto.ui.photographersScreens.photographerDashboard.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.photoshooto.R
import com.squareup.picasso.Picasso

class PhotographerViewPagerAdapter(
    private val context: Context,
    private val imagesArray: Array<Int>,

    ) : PagerAdapter() {
    override fun getCount(): Int {
        return imagesArray.size


    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`

    }


    override fun instantiateItem(container: View, position: Int): Any {

        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_photographer_viewpager, null)
        val imageView = view.findViewById<ImageView>(R.id.img) as ImageView




        Picasso.get().load(imagesArray[position]).into(imageView)


        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}