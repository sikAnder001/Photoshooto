package com.photoshooto.ui.usedPhotographyAdmin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.photoshooto.ui.usedPhotographyAdmin.fragment.ProductListedFragment
import com.photoshooto.ui.usedPhotographyAdmin.fragment.ReportedProductFragment


class TabViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle,val param: String) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return ProductListedFragment(param)
            }
            1 -> {
                return ReportedProductFragment(param)
            }
            else -> return ProductListedFragment(param)
        }
    }

}