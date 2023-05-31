package com.photoshooto.ui.userhomepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.photoshooto.databinding.FragmentUserNotificationsBinding
import com.photoshooto.ui.photographersScreens.photographerDashboard.UnReadNotificationAdapter
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.AllNotificationAdapter

class UserNotificationsFragment : Fragment(), TabLayout.OnTabSelectedListener {


    private lateinit var binding: FragmentUserNotificationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.recyclerViewNotification.adapter = AllNotificationAdapter(requireActivity())
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.recyclerViewNotification.layoutManager = LinearLayoutManager(requireActivity())

        binding.filterNotification.setOnClickListener {

        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab?.position == 0 || tab?.position == 1) {
            binding.recyclerViewNotification.adapter = AllNotificationAdapter(requireActivity())
        } else {
            binding.recyclerViewNotification.adapter = UnReadNotificationAdapter(requireActivity())
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }
}