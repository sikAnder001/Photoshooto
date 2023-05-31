//package com.photoshooto.ui.signup
//
//import android.util.Log
//import android.widget.Toast
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.photoshooto.domain.model.ApiError
//import com.photoshooto.domain.model.VerifyMovileModel
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import com.photoshooto.domain.usecase.signup.SendOtpMobileUseCase
//import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
//import com.photoshooto.util.PreferenceManager
//import kotlinx.coroutines.cancel
//
//
//class SignUpViewModel constructor(private val verifyMobileUseCase: SendOtpMobileUseCase) :
//    ViewModel() {
//
//    val sendMobileOtp = MutableLiveData<VerifyMovileModel>()
//    val _sendMobileOtp: LiveData<VerifyMovileModel> = sendMobileOtp
//
//    val showProgressbar = MutableLiveData<Boolean>()
//    val messageData = MutableLiveData<String>()
//
//    fun getMobileCheck(sendOtpModel: SendOtpRequestModel) {
//
//        showProgressbar.value = true
//        verifyMobileUseCase.sendOtpMobile(
//
//            viewModelScope, sendOtpModel,
//            object : UseCaseResponse<VerifyMovileModel> {
//                override fun onSuccess(result: VerifyMovileModel) {
//                    Log.i(TAG, "result: $result")
//                    with(PreferenceManager) {
//                        saveVerification_key = result.data?.verification_key
//                        saveOtpKey = result.data?.otp
//                        //Log.i(TAG, "server Key "+saveVerification_key.toString())
//                        Log.i(TAG, "serveotp "+ saveOtpKey.toString())
//
//                    }
//                    sendMobileOtp.value = result
//                    showProgressbar.value = false
//                }
//
//                override fun onError(apiError: ApiError?) {
//                    Log.i(TAG, "result: "+apiError?.getErrorMessage())
//                    messageData.value = apiError?.getErrorMessage()
//                    showProgressbar.value = false
//                }
//            },
//        )
//    }
//
// /*   override fun onCleared() {
//        viewModelScope.cancel()
//        super.onCleared()
//    }*/
//
//    companion object {
//        private val TAG = SignUpViewModel::class.java.name
//    }
//
//}