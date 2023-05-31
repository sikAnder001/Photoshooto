package com.photoshooto.ui.qrorderhistory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.ApiError
import com.photoshooto.domain.model.EventOrderHistoryModel
import com.photoshooto.domain.usecase.base.UseCaseResponse
import com.photoshooto.domain.usecase.qr_event_order.EventOrderHistoryDetailsUseCase
import com.photoshooto.domain.usecase.qr_event_order.QREventOrderHistoryViewModel
import com.photoshooto.util.API
import kotlinx.coroutines.cancel

class QREventHistoryViewModel constructor(private val eventDetailsUseCase: EventOrderHistoryDetailsUseCase) :
    ViewModel() {

    val eventOrderHistoyDetails = MutableLiveData<EventOrderHistoryModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()


    fun getQREventOrderHistory(limit: Int, page: Int) {
        showProgressbar.value = true
        eventDetailsUseCase.getEventOrderHistory(
            viewModelScope,
            API.testToken,
            limit,
            page,
            object : UseCaseResponse<EventOrderHistoryModel> {
                override fun onSuccess(result: EventOrderHistoryModel) {
                    Log.i(TAG, "result: $result")
                    eventOrderHistoyDetails.value = result
                    showProgressbar.value = false
                }

                override fun onError(apiError: ApiError?) {
                    messageData.value = apiError?.getErrorMessage()
                    showProgressbar.value = false
                }
            }
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = QREventOrderHistoryViewModel::class.java.name
    }
}