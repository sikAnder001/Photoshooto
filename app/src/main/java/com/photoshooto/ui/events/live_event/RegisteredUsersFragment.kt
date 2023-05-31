package com.photoshooto.ui.events.live_event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.photoshooto.R
import com.photoshooto.databinding.FragmentRegisteredUsersBinding
import com.photoshooto.domain.model.User
import com.photoshooto.ui.events.live_event.adapter.RegTabViewPagerAdapter
import com.photoshooto.util.SharedPrefsHelper

class RegisteredUsersFragment : Fragment() {

    private var user: User? = null
    private lateinit var regBinding: FragmentRegisteredUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        regBinding =
            FragmentRegisteredUsersBinding.inflate(inflater, container, false)

        return regBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = SharedPrefsHelper.getUserCommon()

        val tabAuthorised =
            regBinding.tbLayout.newTab().setText(getString(R.string.authorised_user))
        val tabUnauthorised =
            regBinding.tbLayout.newTab().setText(getString(R.string.unauthorised_user))
        regBinding.tbLayout.addTab(tabAuthorised)
        regBinding.tbLayout.addTab(tabUnauthorised)

        tabAuthorised.view.setOnClickListener {
            regBinding.vpTab.currentItem = 0
//            setBottomText(0)

        }
        tabUnauthorised.view.setOnClickListener {
            regBinding.vpTab.currentItem = 1
//            setBottomText(1)

        }

        val adapter = RegTabViewPagerAdapter(childFragmentManager, lifecycle)

        regBinding.vpTab.adapter = adapter


        val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                regBinding.tbLayout.getTabAt(position)?.select()

//                setBottomText(position)
            }
        }
        regBinding.vpTab.registerOnPageChangeCallback(myPageChangeCallback)
    }

}