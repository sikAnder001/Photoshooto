//package com.photoshooto.ui.signup
//
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.photoshooto.domain.model.ApiError
//import com.photoshooto.domain.model.RegisterVerifyOtpModel
//import com.photoshooto.domain.model.VerifyMovileModel
//import com.photoshooto.domain.usecase.base.UseCaseResponse
//import com.photoshooto.domain.usecase.signup.SignupUseCase
//import kotlinx.coroutines.cancel
//
//class SignupVerifyOtpViewModel constructor(private val vignupUseCase: SignupUseCase) :
//    ViewModel() {
//
//    val sendMobileOtp = MutableLiveData<VerifyMovileModel>()
//    val showProgressbar = MutableLiveData<Boolean>()
//    val messageData = MutableLiveData<String>()
//
//    fun signupVerifyOtpCall(verifyOtp: RegisterVerifyOtpModel) {
//        showProgressbar.value = true
//        vignupUseCase.signupVerifyOtpCall(
//            viewModelScope, verifyOtp,
//            object : UseCaseResponse<VerifyMovileModel> {
//                override fun onSuccess(result: VerifyMovileModel) {
//                    Log.i(TAG, "result: $result")
//                    sendMobileOtp.value = result
//                    showProgressbar.value = false
//                }
//
//                override fun onError(apiError: ApiError?) {
//                    messageData.value = apiError?.getErrorMessage()
//                    showProgressbar.value = false
//                }
//            },
//        )
//    }
//
//    override fun onCleared() {
//        viewModelScope.cancel()
//        super.onCleared()
//    }
//
//    companion object {
//        private val TAG = SignupVerifyOtpViewModel::class.java.name
//    }
//
//}