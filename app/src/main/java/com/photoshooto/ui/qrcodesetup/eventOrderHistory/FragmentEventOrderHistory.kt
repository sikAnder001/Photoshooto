package com.photoshooto.ui.qrcodesetup.eventOrderHistory

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.databinding.FragmentEventOrderHistoryBinding
import com.photoshooto.util.OnItemClickView

class FragmentEventOrderHistory : Fragment() {

    private lateinit var mBinding: FragmentEventOrderHistoryBinding

    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_event_order_history,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        setMyQrDummy()
        setClickEvents()
    }

    private fun setMyQrDummy() {
        val list = arrayListOf("", "", "")
        mBinding.recyclerViewMyQrCodes.apply {
            adapter =
                EventOrderHistoryAdapter(requireActivity(), list, object : OnItemClickView<String> {
                    override fun onItemClick(model: String, position: Int, view: View) {
                        openOptionMenu(view)
                    }
                })
        }
    }

    var popupWindow: PopupWindow? = null
    private fun openOptionMenu(viewLocation: View) {
        if (popupWindow != null) {
            if (popupWindow!!.isShowing) {
                popupWindow!!.dismiss()
                popupWindow = null
            }
        }

        val inflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_my_qr_code_option, null)
        popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isFocusable = true
        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow?.showAsDropDown(viewLocation)
    }

    private fun setClickEvents() {
        mBinding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }
        mBinding.btnProceed.setOnClickListener {
            //navController?.navigate(FragmentEventOrderHistoryDirections.actionMyQrCodeToSetupEvent())
            findNavController().navigate(R.id.action_fragmentEventOrderHistory2_to_fragmentCreateEvents)
        }
    }
}