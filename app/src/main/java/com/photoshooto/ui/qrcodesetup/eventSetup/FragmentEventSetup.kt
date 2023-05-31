package com.photoshooto.ui.qrcodesetup.eventSetup

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.photoshooto.R
import com.photoshooto.databinding.DialogEventSetupDoneBinding
import com.photoshooto.databinding.FragmentEventSetupBinding
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateEventRequest
import com.photoshooto.ui.qrcodesetup.createEvent.CreateEventViewModel
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FragmentEventSetup : Fragment() {

    private lateinit var mBinding: FragmentEventSetupBinding

    private val mViewModel: CreateEventViewModel by sharedViewModel()

    private var navController: NavController? = null

    private var mCreateEventRequest: CreateEventRequest? = null
    private var mFolderName = ""
    private var mEventDuration = ""
    private var mQRImage = ""

    private var mUserCount = 0
    private var mStandeeCount = 0

    private val arguments: FragmentEventSetupArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_event_setup, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        mCreateEventRequest = arguments.mCreateEventRequest
        mFolderName = arguments.mFolderName
        mEventDuration = arguments.mEventDuration
        mQRImage = checkStringReturnValue(arguments.mImage)

        attachObserver()
        setAddedValue()
        setClickEvent()
    }

    private fun attachObserver() {
        with(mViewModel) {
            createEventResponse.observe(requireActivity()) {
                if (it.success!!) {
                    openSetupDoneDialog()
                }
                it.message?.let { it1 -> onToast(it1, requireActivity()) }
            }
            showProgressbar.observe(requireActivity()) { isVisible ->
                mBinding.progressBarCommon.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setAddedValue() {
        if (mCreateEventRequest != null) {
            mCreateEventRequest!!.apply {
                mBinding.tvValueEventType.text = checkStringReturnValue(event_type)
                mBinding.tvValueSelectedFolder.text = checkStringReturnValue(mFolderName)
                mBinding.tvValueEventStartDate.text = checkStringReturnValue(event_start_date)
                mBinding.tvValueEventEndDate.text = checkStringReturnValue(event_end_date)
                mBinding.tvValueEventStartTime.text = checkStringReturnValue(event_start_time)
                mBinding.tvValueEventEndTime.text = checkStringReturnValue(event_end_time)
                mBinding.tvValueEventDuration.text = checkStringReturnValue(mEventDuration)
                mBinding.tvValueEventQrCodes.text = checkStringReturnValue(standee_type)
                mBinding.tvValueClientName.text = checkStringReturnValue(client_name)
                mBinding.tvValueClientNumber.text = checkStringReturnValue(client_contact_number)
                mBinding.tvValueLocation.text = checkStringReturnValue(location)

                if (checkStringValue(mQRImage)) {
                    requireActivity().loadImage(mBinding.imageStand, mQRImage)
                }
            }
        }
    }

    private fun setClickEvent() {
        mBinding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.imageEdit.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.btnProceed.setOnClickListener {
            if (mUserCount == 0) {
                onToast("Add at least one user", requireActivity())
                return@setOnClickListener
            }
            if (mStandeeCount == 0) {
                onToast("Add at least one standee", requireActivity())
                return@setOnClickListener
            }

            mCreateEventRequest!!.client_contact_number =
                mCreateEventRequest!!.client_contact_number!!.split(" ")[1]
            mCreateEventRequest!!.no_users = "$mUserCount"
            mCreateEventRequest!!.quantity = "$mStandeeCount"

            mViewModel.callCreateEvent(
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                mCreateEventRequest!!
            )
            //openSetupDoneDialog()
        }

        mBinding.cardViewUserPlus.setOnClickListener {
            mUserCount += 1
            mBinding.tvUserCount.text = "$mUserCount"
        }

        mBinding.cardViewUserMinus.setOnClickListener {
            if (mUserCount > 0) {
                mUserCount -= 1
                mBinding.tvUserCount.text = "$mUserCount"
            }
        }

        mBinding.cardViewStandeePlus.setOnClickListener {
            mStandeeCount += 1
            mBinding.tvStandeeCount.text = "$mStandeeCount"
        }

        mBinding.cardViewStandeeMinus.setOnClickListener {
            if (mStandeeCount > 0) {
                mStandeeCount -= 1
                mBinding.tvStandeeCount.text = "$mStandeeCount"
            }
        }
    }

    private fun openSetupDoneDialog() {
        val dialog = Dialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mDialogBinding = DialogEventSetupDoneBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialog.setContentView(mDialogBinding.root)
        dialog.setCancelable(false)

        mDialogBinding.tvDialogThanks.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.actionEventToMyQrCodeCreated)
        }

        dialog.show()
    }
}