package com.photoshooto.domain.usecase.verify_otp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.UserProfileModel
import com.photoshooto.domain.usecase.base.UseCaseResponse

class VerifyOtpViewModel constructor(private val resetPwdUseCase: VerifyOtpUseCase) :
    ViewModel() {

    val otpVerifiedData = MutableLiveData<UserProfileModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun verifyOtp(body: VerifyOTPRequestModel?) {
        showProgressbar.value = true
        resetPwdUseCase.verifyOtp(
            viewModelScope, body,
            object : UseCaseResponse<UserProfileModel> {
                override fun onSuccess(result: UserProfileModel) {
                    Log.i(TAG, "result: $result")
                    otpVerifiedData.postValue(result)
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                    println("praveen apiError " + apiError)
                }
            },
        )
    }

    companion object {
        private val TAG = VerifyOtpViewModel::class.java.name
    }

}