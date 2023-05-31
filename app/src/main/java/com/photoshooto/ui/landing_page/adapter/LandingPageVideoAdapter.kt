package com.photoshooto.ui.landing_page.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.photoshooto.domain.model.VideoItem
import com.photoshooto.ui.landing_page.ExoPlayerViewFragment

class LandingPageVideoAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var values: List<VideoItem>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return values.size
    }

    override fun createFragment(position: Int): Fragment {
        return ExoPlayerViewFragment.newInstance(values[position].file_path!!)
    }

}
