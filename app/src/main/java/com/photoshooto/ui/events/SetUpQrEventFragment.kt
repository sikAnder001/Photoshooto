package com.photoshooto.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.photoshooto.R
import com.photoshooto.databinding.FragmentSetUpQrEventBinding
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.model.QuickBeans


class SetUpQrEventFragment : Fragment() {

    private lateinit var setUpQrEventBinding: FragmentSetUpQrEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpQrEventBinding = FragmentSetUpQrEventBinding.inflate(inflater, container, false)

        setUpQrEventBinding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setUpQrEventBinding.tvViewAll2.setOnClickListener {
            view?.findNavController()
                ?.navigate(
                    R.id.action_setUpQrEventFragment_to_searchEventFragment
                )
        }
        initview()

        return setUpQrEventBinding.root
    }


    private fun initview() {


        val arrayQrEvents = ArrayList<QuickBeans>()
        arrayQrEvents.add(
            QuickBeans(
                R.drawable.qr_code_image,
                "Elite Qr Code",
                "Created on 15/07/23"
            )
        )

        val qreventsAdapter = QREventsAdapter(arrayQrEvents)
        setUpQrEventBinding.qrcodeRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        setUpQrEventBinding.qrcodeRecyclerView.adapter = qreventsAdapter


        val arrayRecent = ArrayList<QuickBeans>()
        arrayRecent.add(QuickBeans(R.drawable.dummy_image, "Ram’s Wedding", "Elite Qr Code"))

        val recentAdapter = RecentEventsAdapter(arrayRecent)
        setUpQrEventBinding.recentEventRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        setUpQrEventBinding.recentEventRecyclerView.adapter = recentAdapter


        val arrayEvent = ArrayList<QuickBeans>()
        arrayEvent.add(QuickBeans(R.drawable.img_folder, "Ram’s Wedding", "300 Items | 3.2 GB"))

        val qreventAdapter = EventsAdapter(arrayEvent)
        setUpQrEventBinding.qrEventRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        setUpQrEventBinding.qrEventRecyclerView.adapter = qreventAdapter

    }


}