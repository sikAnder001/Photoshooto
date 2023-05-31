package com.photoshooto.domain.usecase.reset_pwd

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.AllStatusModel
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.cancel


class ResetPwdViewModel constructor(private val resetPwdUseCase: ResetPwdUseCase) :
    ViewModel() {

    val resetPwdData = MutableLiveData<AllStatusModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun resetpassword(body: PwdChangeRequestModel?, token: String?) {
        showProgressbar.value = true
        resetPwdUseCase.resetpassword(
            viewModelScope, body, token,
            object : UseCaseResponse<AllStatusModel> {
                override fun onSuccess(result: AllStatusModel) {
                    Log.i(TAG, "result: $result")
                    resetPwdData.postValue(result)
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

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = ResetPwdViewModel::class.java.name
    }

}