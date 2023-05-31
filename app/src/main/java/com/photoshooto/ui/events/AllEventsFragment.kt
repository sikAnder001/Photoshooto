package com.photoshooto.ui.events

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.photoshooto.R
import com.photoshooto.databinding.FragmentAllEventsBinding
import com.photoshooto.ui.photographersScreens.photographerDashboard.fragments.model.QuickBeans


class AllEventsFragment : Fragment() {
    private var newBinding: FragmentAllEventsBinding? = null
    private val binding get() = newBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newBinding = FragmentAllEventsBinding.inflate(inflater, container, false)

        initview()


        return binding.root
    }


    private fun initview() {


        newBinding?.tvFolder?.setOnClickListener {
            newBinding?.tvFolder?.setBackgroundResource(R.drawable.selected_tab_bg)
            newBinding?.tvTracker?.setBackgroundResource(R.drawable.tab_bg)
            newBinding?.tvPayment?.setBackgroundResource(R.drawable.tab_bg)
            newBinding?.tvFolder?.setTextColor(resources.getColor(R.color.blue_text))
            newBinding?.tvTracker?.setTextColor(resources.getColor(R.color.grey11))
            newBinding?.tvPayment?.setTextColor(resources.getColor(R.color.grey11))
            /* newBinding?.tvFolder?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semi_bold)
             newBinding?.tvTracker?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
             newBinding?.tvPayment?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

 */
        }

        newBinding?.tvTracker?.setOnClickListener {
            newBinding?.tvFolder?.setBackgroundResource(R.drawable.tab_bg)
            newBinding?.tvTracker?.setBackgroundResource(R.drawable.selected_tab_bg)
            newBinding?.tvPayment?.setBackgroundResource(R.drawable.tab_bg)
            newBinding?.tvFolder?.setTextColor(resources.getColor(R.color.grey11))
            newBinding?.tvTracker?.setTextColor(resources.getColor(R.color.blue_text))
            newBinding?.tvPayment?.setTextColor(resources.getColor(R.color.grey11))
            /* newBinding?.tvFolder?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
             newBinding?.tvTracker?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semi_bold)
             newBinding?.tvPayment?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
 */


        }

        newBinding?.tvPayment?.setOnClickListener {
            newBinding?.tvFolder?.setBackgroundResource(R.drawable.tab_bg)
            newBinding?.tvTracker?.setBackgroundResource(R.drawable.tab_bg)
            newBinding?.tvPayment?.setBackgroundResource(R.drawable.selected_tab_bg)
            newBinding?.tvFolder?.setTextColor(resources.getColor(R.color.grey11))
            newBinding?.tvTracker?.setTextColor(resources.getColor(R.color.grey11))
            newBinding?.tvPayment?.setTextColor(resources.getColor(R.color.blue_text))

            /* newBinding?.tvFolder?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
             newBinding?.tvTracker?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
             newBinding?.tvPayment?.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semi_bold)

 */
        }

        newBinding?.tvViewAll?.setOnClickListener {


            view?.findNavController()
                ?.navigate(
                    R.id.action_allEventsFragment_to_setUpQrEventFragment
                )


        }

        /* newBinding?.createTitle?.setOnClickListener {
            selectEventPopup()

         }*/


        val arrayEvent = ArrayList<QuickBeans>()
        arrayEvent.add(QuickBeans(R.drawable.img_folder, "Ram’s Wedding", "300 Items | 3.2 GB"))

        val eventAdapter = EventsAdapter(arrayEvent)
        newBinding!!.qrEventRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newBinding!!.qrEventRecyclerView.adapter = eventAdapter


        val array = ArrayList<QuickBeans>()
        arrayEvent.add(QuickBeans(R.drawable.img_folder, "Raj’s Wedding", "300 Items | 3.2 GB"))

        val eventNormalAdapter = EventsAdapter(array)
        newBinding!!.normalERecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newBinding!!.normalERecyclerView.adapter = eventNormalAdapter

    }


    private fun selectEventPopup() {
        val selectEvent = Dialog(requireContext())
        selectEvent.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectEvent.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        selectEvent.setCancelable(false)
        selectEvent.setContentView(R.layout.popup_select_event)
        selectEvent.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        selectEvent.setCanceledOnTouchOutside(true)
        val btnQrE: Button = selectEvent.findViewById(R.id.btn_qrEvent) as Button
        val btnNormalE: Button = selectEvent.findViewById(R.id.btn_nEvent) as Button

        btnQrE.setOnClickListener {
            selectEvent.dismiss()
            view?.findNavController()
                ?.navigate(
                    R.id.action_allEventsFragment_to_qrEventsFragment3
                )
        }

        btnNormalE.setOnClickListener {
            selectEvent.dismiss()
        }


        selectEvent.show()
    }


}