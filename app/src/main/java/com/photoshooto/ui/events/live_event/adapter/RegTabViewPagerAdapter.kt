package com.photoshooto.ui.events.live_event.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.photoshooto.ui.events.live_event.AuthorisedUserFragment
import com.photoshooto.ui.events.live_event.UnauthorisedUserFragment


class RegTabViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return AuthorisedUserFragment()
            }
            1 -> {
                return UnauthorisedUserFragment()
            }
            else -> return AuthorisedUserFragment()
        }
    }

}