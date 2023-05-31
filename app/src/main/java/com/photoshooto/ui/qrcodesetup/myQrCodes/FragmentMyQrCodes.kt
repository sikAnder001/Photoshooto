package com.photoshooto.ui.qrcodesetup.myQrCodes

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
import com.photoshooto.databinding.FragmentMyQrCodesBinding
import com.photoshooto.domain.model.GetEventResponse
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentMyQrCodes : Fragment() {

    private lateinit var mBinding: FragmentMyQrCodesBinding
    private val mViewModel: MyQrViewModel by viewModel()
    private var navController: NavController? = null

    private var mListMyQrCodes = arrayListOf<GetEventResponse.GetEventData.EventModel?>()
    private var mAdapterQrCodes: MyQRCodesAdapter? = null
    private var popupWindow: PopupWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_qr_codes, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        attachObserver()
        setClickEvents()

        mViewModel.getEventList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
    }

    private fun attachObserver() {
        with(mViewModel) {
            getEventListResponse.observe(requireActivity()) {
                if (it.success!!) {
                    mListMyQrCodes = arrayListOf()
                    if (it.data != null) {
                        if (!it.data!!.list.isNullOrEmpty()) {
                            mListMyQrCodes.addAll(it.data!!.list!!)
                        }
                    }

                    setListData()

                } else {
                    it.message?.let { it1 -> onToast(it1, requireActivity()) }
                }
            }

            showProgressbar.observe(requireActivity()) { isVisible ->
                mBinding.progressBarCommon.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setListData() {
        if (mListMyQrCodes.isNotEmpty()) {
            mAdapterQrCodes = MyQRCodesAdapter(
                requireActivity(),
                mListMyQrCodes,
                object : OnItemClickView<GetEventResponse.GetEventData.EventModel> {
                    override fun onItemClick(
                        model: GetEventResponse.GetEventData.EventModel,
                        position: Int,
                        view: View
                    ) {
                        openOptionMenu(view)
                    }
                },
                object : OnItemClick<GetEventResponse.GetEventData.EventModel> {
                    override fun onItemClick(
                        model: GetEventResponse.GetEventData.EventModel,
                        position: Int
                    ) {
                        //navController?.navigate(FragmentMyQrCodesDirections.actionMyQrCodeToEventOrderHistory())
                        findNavController().navigate(R.id.action_fragmentMyQrCodes2_to_QREventOrderHistoryFragment)
                    }
                })
            mBinding.recyclerViewMyQrCodes.adapter = mAdapterQrCodes

            mBinding.recyclerViewMyQrCodes.show()
            mBinding.tvNoDataAvailable.hide()
        } else {
            mBinding.recyclerViewMyQrCodes.hide()
            mBinding.tvNoDataAvailable.show()
        }
    }

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
            findNavController().navigate(R.id.action_fragmentMyQrCodes2_to_fragmentCreateEvents)
        }
    }
}