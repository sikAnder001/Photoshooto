package com.photoshooto.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.photoshooto.databinding.FragmentCommonNotificationBinding
import com.photoshooto.ui.notification.adapter.NotificationAdapter
import com.photoshooto.ui.notification.model.NotificationItem

class CommonNotificationFragment : Fragment() {
    private val ARG_PARAM1 = "list"
    private val ARG_PARAM2 = "type"

    companion object {
        @JvmStatic
        fun newInstance(list: List<NotificationItem>?, type: String) =
            CommonNotificationFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_PARAM1, ArrayList(list!!))
                    putString(ARG_PARAM2, type)
                    arguments?.clear()
                }
            }
    }

    private var list: MutableList<NotificationItem>? = null
    private var adapterList: MutableList<NotificationItem> = mutableListOf()
    private var _binding: FragmentCommonNotificationBinding? = null
    private val binding get() = _binding!!
    private var adapter: NotificationAdapter? = null
    private var type: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            list = it.getParcelableArrayList<NotificationItem>(ARG_PARAM1)
            type = it.getString(ARG_PARAM2)
            arguments?.clear()
            Log.d("TAG========:::::", "onCreate List : $list")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCommonNotificationBinding.inflate(inflater, container, false)
        initRecyclerView(list)
        return binding.root
    }


    private fun initRecyclerView(list: List<NotificationItem>?) {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.layoutManager = layoutManager
        setAdapter(list?.toMutableList())

    }


    private fun setAdapter(mutableList: MutableList<NotificationItem>?) {
        adapterList.clear()
        if (!mutableList.isNullOrEmpty()) {
            binding.tvNoNotification.isVisible = false
            binding.tvNotify.isVisible = false
            binding.notificationImage.isVisible = false
            binding.recyclerView.isVisible = true

            adapterList.addAll(mutableList)
            if (adapter == null) {
                Log.d("TAG", "adapter : $adapterList")
                adapter = NotificationAdapter(requireActivity(), adapterList)
                binding.recyclerView.adapter = adapter
            }
        } else {
            binding.tvNoNotification.isVisible = true
            binding.tvNotify.isVisible = true
            binding.notificationImage.isVisible = true
            binding.recyclerView.isVisible = false
        }
        adapter?.notifyDataSetChanged()
    }

}