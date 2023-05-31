package com.photoshooto.ui.admin_screen.manage_job_posting

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class JobTabViewPagerAdapter(
    childFragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val param: String
) :
    FragmentStateAdapter(childFragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return JobPostedFragment(param)
            }
            1 -> {
                return ReportedJobFragment(param)
            }
            else -> return JobPostedFragment(param)
        }
    }

}
