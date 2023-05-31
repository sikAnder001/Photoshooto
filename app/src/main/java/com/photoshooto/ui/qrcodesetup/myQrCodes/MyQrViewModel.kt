package com.photoshooto.ui.qrcodesetup.myQrCodes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.GetEventResponse
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.qrCodeSetup.myQrCode.MyQrUseCase
import kotlinx.coroutines.cancel


class MyQrViewModel constructor(private val createEventUseCase: MyQrUseCase) :
    ViewModel() {

    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    val getEventListResponse = MutableLiveData<GetEventResponse>()

    fun getEventList(token: String?) {
        showProgressbar.value = true
        createEventUseCase.getEventList(
            viewModelScope, token,
            object : UseCaseResponse<GetEventResponse> {
                override fun onSuccess(result: GetEventResponse) {
                    Log.i(TAG, "result: $result")
                    getEventListResponse.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }
            },
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = MyQrViewModel::class.java.name
    }

}