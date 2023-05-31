package com.photoshooto.domain.usecase.qr_event_order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.EventOrderHistoryModel
import kotlinx.coroutines.cancel

class QREventOrderHistoryViewModel constructor(private val eventDetailsUseCase: EventOrderHistoryDetailsUseCase) :
    ViewModel() {

    val eventOrderHistoyDetails = MutableLiveData<EventOrderHistoryModel>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = QREventOrderHistoryViewModel::class.java.name
    }
}